import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Runs JUnit 4 test classes (as cardtest.py's fast path does) and prints one line per test:
 * PASS / FAIL / IGNORED, then the failure messages and a trimmed stack trace.
 * Lines are prefixed with "@@" so cardtest.py can tell them from what the game prints.
 * An argument Class#method runs just that method.
 */
public class CardTestRunner {
    public static void main(String[] args) throws Exception {
        JUnitCore core = new JUnitCore();
        final Set<Description> failed = new HashSet<>();
        core.addListener(new RunListener() {
            @Override
            public void testFailure(Failure failure) {
                failed.add(failure.getDescription());
                String msg = String.valueOf(failure.getMessage());
                StringBuilder sb = new StringBuilder();
                for (StackTraceElement e : failure.getException().getStackTrace()) {
                    String c = e.getClassName();
                    if (c.startsWith("org.junit") || c.startsWith("java.") || c.startsWith("jdk.") || c.startsWith("sun."))
                        continue;
                    sb.append("\n@@        at ").append(e);
                    if (c.equals(failure.getDescription().getClassName()))
                        break;
                }
                System.out.println("@@ FAIL    " + name(failure.getDescription()) + "\n@@        "
                        + failure.getException().getClass().getSimpleName() + ": " + msg.replace("\n", "\n@@        ") + sb);
            }

            @Override
            public void testFinished(Description d) {
                if (!failed.contains(d))
                    System.out.println("@@ PASS    " + name(d));
            }

            @Override
            public void testIgnored(Description d) {
                System.out.println("@@ IGNORED " + name(d));
            }
        });
        int fails = 0, runs = 0, ignored = 0;
        for (String arg : args) {
            int hash = arg.indexOf('#');
            Class<?> cls = Class.forName(hash < 0 ? arg : arg.substring(0, hash));
            Request req = hash < 0 ? Request.aClass(cls) : Request.method(cls, arg.substring(hash + 1));
            Result r = core.run(req);
            fails += r.getFailureCount();
            runs += r.getRunCount();
            ignored += r.getIgnoreCount();
        }
        System.out.println("@@ TOTAL   run " + runs + ", failed " + fails + ", ignored " + ignored);
        System.exit(fails == 0 ? 0 : 1);
    }

    private static String name(Description d) {
        String cls = d.getClassName();
        return cls.substring(cls.lastIndexOf('.') + 1) + "." + d.getMethodName();
    }
}
