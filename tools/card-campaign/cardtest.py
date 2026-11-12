#!/usr/bin/env python3
"""Runs card test classes and prints pass/fail per test.

    cardtest.py setup                 builds the modules once with Maven and records the test classpath
                                      (a few minutes the first time; again after an engine/card-class change)
    cardtest.py run <what...>         compiles just those test files with javac and runs them (seconds)
    cardtest.py run <what...> --maven runs them through Maven/Surefire instead (slow, no setup needed;
                                      what CI does)
    cardtest.py classpath             prints the recorded classpath (for a debugger or a probe)

<what> is a card id (1_1, 1_188), a card id and one method (1_188#MouseDroidVehiclePresentReachWorks), a
selector (set:1 = every card of set 1 that has a test file), or a path to a test .java file.  A card with more
than one test file (Card_1_39_Tests plus Card_1_039_Foo_Tests) runs all of them.

Options for the fast path:
    --out <name>   compile into src/gemp-swccg-server/target/cardtest/<name> (default "default"), so several
                   agents can run their own cards at once without treading on each other
    --verbose      also print what the game logged while the tests ran

Fast path details.  `setup` runs `mvn -q -B -DskipTests -pl gemp-swccg-server -am install` from src/ and then
`dependency:build-classpath` in gemp-swccg-server.  `run` compiles the named test files (and runner/
CardTestRunner.java) against the server's test-classes, each sibling module's target/classes and the
dependency jars, then runs them with JUnit 4 under the same JVM flags Surefire uses (the --add-opens list and
-Dgemp.shuffleSeed=1), from the gemp-swccg-server folder.  A change to a card class or engine source needs
`mvn -q -o -DskipTests -pl gemp-swccg-cards compile` (or that module) or `setup` again before `run` sees it;
a change to the test file itself is picked up at once.  Test classes written for JUnit 5 (org.junit.jupiter)
are not run by the fast path; it says so, and --maven runs them.

Exit status: 0 when every test run passed, 1 when any failed, 2 when nothing ran or it did not compile.
"""
import os
import re
import shutil
import subprocess
import sys
import time
import xml.etree.ElementTree as ET
from pathlib import Path

import campaign as c

TARGET = c.SERVER / "target"
CP_FILE = TARGET / "cardtest.classpath"
RUNNER = Path(__file__).resolve().parent / "runner" / "CardTestRunner.java"
SIBLINGS = ("gemp-swccg-logic", "gemp-swccg-common", "gemp-swccg-cards")
JVM_FLAGS = ["--add-opens", "java.base/java.util=ALL-UNNAMED", "--add-opens", "java.base/java.lang=ALL-UNNAMED",
             "--add-opens", "java.base/java.util.concurrent=ALL-UNNAMED",
             "--add-opens", "java.base/java.util.concurrent.atomic=ALL-UNNAMED",
             "--add-opens", "java.base/java.util.regex=ALL-UNNAMED", "--add-opens", "java.base/java.lang.invoke=ALL-UNNAMED",
             "-Dgemp.shuffleSeed=1"]
NOISE = re.compile(r"Picked up JAVA_TOOL_OPTIONS")


def env():
    # The cloud workspace sets JAVA_TOOL_OPTIONS (proxy, truststore); it only adds a noise line to java's
    # output, so it is kept for Maven (which needs the proxy) and dropped for javac/java.
    e = dict(os.environ)
    e.pop("JAVA_TOOL_OPTIONS", None)
    return e


def mvn():
    return shutil.which("mvn") or str(c.SRC / ("mvnw.cmd" if os.name == "nt" else "mvnw"))


# ---------------------------------------------------------------- what to run

def resolve(words):
    """-> [(test file, method or None)]"""
    out = []
    for w in words:
        method = None
        if "#" in w:
            w, method = w.split("#", 1)
        p = Path(w)
        if w.endswith(".java"):
            p = p if p.is_file() else c.REPO / w
            if not p.is_file():
                print("note: no such file %s" % w, file=sys.stderr)
                continue
            out.append((p.resolve(), method))
            continue
        try:
            ids = c.resolve_ids([w])
        except ValueError as e:
            print("note: %s" % e, file=sys.stderr)
            continue
        selector = w.startswith("set:")
        for cid in ids:
            files = c.test_files(cid)
            if not files and not selector:
                print("note: %s has no test file (expected %s.java)" % (cid, c.test_class_simple_name(cid)),
                      file=sys.stderr)
            for f in files:
                out.append((f, method))
    seen, uniq = set(), []
    for item in out:
        if item not in seen:
            seen.add(item)
            uniq.append(item)
    return uniq


def is_junit5(path):
    return "org.junit.jupiter" in Path(path).read_text(encoding="utf-8", errors="replace")


# ---------------------------------------------------------------- fast path

def setup():
    t = time.time()
    subprocess.check_call([mvn(), "-q", "-B", "-DskipTests", "-pl", "gemp-swccg-server", "-am", "install"], cwd=c.SRC)
    TARGET.mkdir(exist_ok=True)
    subprocess.check_call([mvn(), "-q", "-B", "dependency:build-classpath", "-Dmdep.outputFile=" + str(CP_FILE)],
                          cwd=c.SERVER)
    print("Built in %ds.  Classpath written to %s" % (time.time() - t, c.rel(CP_FILE)))


def classpath(out_dir=None):
    if not CP_FILE.is_file():
        raise SystemExit("No classpath yet.  Run: cardtest.py setup   (or use run --maven)")
    deps = [d for d in CP_FILE.read_text().strip().split(os.pathsep) if d]
    local = []
    for module in SIBLINGS:
        classes = c.SRC / module / "target" / "classes"
        if classes.is_dir():
            local.append(str(classes))
            # prefer the module's freshly compiled classes over the jar Maven installed
            deps = [d for d in deps if not re.search(r"[/\\]%s[/\\]" % re.escape(module), d)]
    parts = ([str(out_dir)] if out_dir else []) + [str(TARGET / "test-classes"), str(TARGET / "classes")] + local + deps
    return os.pathsep.join(parts)


def run_fast(items, out_name, verbose):
    j5 = [f for f, _ in items if is_junit5(f)]
    if j5:
        print("JUnit 5 test classes are not run by the fast path; use --maven for: "
              + ", ".join(sorted({f.name for f in j5})))
        items = [(f, m) for f, m in items if f not in j5]
    if not items:
        print("Nothing to run.")
        return 2
    out_dir = TARGET / "cardtest" / out_name
    if out_dir.exists():
        shutil.rmtree(out_dir)
    out_dir.mkdir(parents=True)
    cp = classpath(out_dir)
    files = sorted({str(f) for f, _ in items})
    javac = subprocess.run(["javac", "-nowarn", "-proc:none", "-encoding", "UTF-8", "-d", str(out_dir), "-cp", cp]
                           + files + [str(RUNNER)], cwd=c.SERVER, env=env(), capture_output=True, text=True)
    if javac.returncode != 0:
        sys.stdout.write("\n".join(l for l in (javac.stdout + javac.stderr).splitlines() if not NOISE.search(l)) + "\n")
        print("Did not compile.")
        return 2
    targets = [c.test_fqcn(f) + ("#" + m if m else "") for f, m in items]
    proc = subprocess.Popen(["java"] + JVM_FLAGS + ["-cp", cp, "CardTestRunner"] + targets, cwd=c.SERVER, env=env(),
                            stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True, errors="replace")
    total = None
    for line in proc.stdout:
        if line.startswith("@@"):
            line = line[3:]
            if line.startswith("TOTAL"):
                total = line
                continue
            sys.stdout.write(line)
        elif verbose and not NOISE.search(line):
            sys.stdout.write("   | " + line)
    rc = proc.wait()
    if total:
        sys.stdout.write(total)
        return 0 if rc == 0 else 1
    print("The runner stopped without a result (exit %d); run with --verbose to see why." % rc)
    return 2


# ---------------------------------------------------------------- Maven path

def run_maven(items, verbose):
    selectors = []
    for f, m in items:
        name = f.stem + ("#" + m if m else "")
        if name not in selectors:
            selectors.append(name)
    reports = TARGET / "surefire-reports"
    started = time.time()
    cmd = [mvn(), "-B", "-pl", "gemp-swccg-server", "-am", "-Dtest=" + ",".join(selectors),
           "-Dsurefire.failIfNoSpecifiedTests=false", "-Dcheckstyle.skip", "test"]
    if not verbose:
        cmd.insert(1, "-q")
    print("$ (src/) " + " ".join(cmd), flush=True)
    proc = subprocess.run(cmd, cwd=c.SRC, capture_output=not verbose, text=True, errors="replace")
    results = []
    for f, _ in items:
        xml = reports / ("TEST-%s.xml" % c.test_fqcn(f))
        if not xml.is_file() or xml.stat().st_mtime < started:
            continue
        for tc in ET.parse(xml).getroot().iter("testcase"):
            status, detail = "PASS", ""
            for tag in ("failure", "error"):
                el = tc.find(tag)
                if el is not None:
                    status = "FAIL"
                    detail = (el.get("type", "") + ": " + (el.get("message") or "")).strip()
            if tc.find("skipped") is not None:
                status = "IGNORED"
            item = (status, tc.get("classname", "").rsplit(".", 1)[-1] + "." + tc.get("name", ""), detail)
            if item not in results:
                results.append(item)
    if not results:
        if proc.returncode != 0 and not verbose:
            sys.stdout.write("\n".join((proc.stdout + proc.stderr).splitlines()[-40:]) + "\n")
        print("No Surefire results found (Maven exit %d)." % proc.returncode)
        return 2
    for status, name, detail in results:
        print("%-7s %s" % (status, name))
        if detail:
            print("        " + detail.replace("\n", "\n        "))
    failed = sum(1 for s, _, _ in results if s == "FAIL")
    print("TOTAL   run %d, failed %d, ignored %d" % (sum(1 for s, _, _ in results if s != "IGNORED"), failed,
                                                    sum(1 for s, _, _ in results if s == "IGNORED")))
    return 1 if failed else 0


def main(argv):
    if not argv or argv[0] in ("-h", "--help"):
        print(__doc__.strip())
        return 0 if argv else 2
    cmd, rest = argv[0], argv[1:]
    if cmd == "setup":
        setup()
        return 0
    if cmd == "classpath":
        print(classpath())
        return 0
    if cmd != "run":
        print(__doc__.strip())
        return 2
    out_name = "default"
    if "--out" in rest:
        i = rest.index("--out")
        out_name = rest[i + 1]
        rest = rest[:i] + rest[i + 2:]
    verbose = "--verbose" in rest
    use_maven = "--maven" in rest
    words = [w for w in rest if w not in ("--verbose", "--maven")]
    items = resolve(words)
    if not items:
        print("No test files matched.")
        return 2
    return run_maven(items, verbose) if use_maven else run_fast(items, out_name, verbose)


if __name__ == "__main__":
    import signal
    if hasattr(signal, "SIGPIPE"):
        signal.signal(signal.SIGPIPE, signal.SIG_DFL)
    sys.exit(main(sys.argv[1:]))
