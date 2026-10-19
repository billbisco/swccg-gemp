#!/usr/bin/env python3
"""Upload this run's age-encrypted backup files to B2 via the native API.
Uses only b2_authorize_account, b2_get_upload_url, b2_upload_file (the key has writeFiles+listBuckets,
no readFiles). Bucket default retention (COMPLIANCE 30d) applies server-side.
Usage: swccg-b2-upload.py ENV_FILE BACKUP_ROOT STAMP   -> prints one JSON line per uploaded object.
Never prints credentials."""
import base64, hashlib, json, os, sys, urllib.request, urllib.parse

def load_env(path):
    env = {}
    for line in open(path):
        line = line.strip()
        if not line or line.startswith('#') or '=' not in line: continue
        k, v = line.split('=', 1)
        env[k.strip().removeprefix('export ').strip()] = v.strip().strip('"').strip("'")
    return env

def call(url, headers, data=None):
    req = urllib.request.Request(url, data=data, headers=headers, method='POST' if data is not None else 'GET')
    try:
        with urllib.request.urlopen(req, timeout=3600) as r:
            return json.load(r)
    except urllib.error.HTTPError as e:
        body = e.read().decode(errors='replace')[:300]
        raise SystemExit(f"B2 error {e.code} on {url.split('?')[0].rsplit('/',1)[-1]}: {body}")

def main():
    envf, root, stamp = sys.argv[1:4]
    env = load_env(envf)
    basic = base64.b64encode(f"{env['B2_KEY_ID']}:{env['B2_APP_KEY']}".encode()).decode()
    auth = call('https://api.backblazeb2.com/b2api/v3/b2_authorize_account', {'Authorization': 'Basic ' + basic})
    api = auth['apiInfo']['storageApi']; api_url = api['apiUrl']; tok = auth['authorizationToken']
    bucket_id = env.get('B2_BUCKET_ID') or api.get('bucketId')
    files = []
    for prefix in ('gemp', 'wiki', 'config', 'forum'):
        d = os.path.join(root, prefix)
        if not os.path.isdir(d): continue
        for n in sorted(os.listdir(d)):
            if stamp in n and n.endswith('.age'):
                files.append((prefix, os.path.join(d, n)))
    if not files: raise SystemExit('nothing to upload for stamp ' + stamp)
    up = call(f"{api_url}/b2api/v3/b2_get_upload_url", {'Authorization': tok, 'Content-Type': 'application/json'},
              json.dumps({'bucketId': bucket_id}).encode())
    bad = 0
    for prefix, path in files:
        size = os.path.getsize(path); h = hashlib.sha1()
        with open(path, 'rb') as f:
            for chunk in iter(lambda: f.read(1 << 20), b''): h.update(chunk)
        sha1 = h.hexdigest()
        name = f"{prefix}/{os.path.basename(path)}"
        r = call(up['uploadUrl'], {'Authorization': up['authorizationToken'],
                 'X-Bz-File-Name': urllib.parse.quote(name, safe='/'),
                 'Content-Type': 'application/octet-stream', 'Content-Length': str(size),
                 'X-Bz-Content-Sha1': sha1}, open(path, 'rb'))
        ok = r.get('contentLength') == size and r.get('contentSha1') == sha1
        bad += 0 if ok else 1
        fr = r.get('fileRetention') or {}
        print(json.dumps({'name': r.get('fileName'), 'fileId': r.get('fileId'), 'size': r.get('contentLength'),
                          'localSize': size, 'sha1': r.get('contentSha1'), 'match': ok,
                          'retention': (fr.get('value') if fr.get('isClientAuthorizedToRead') else 'not-readable-by-key')}))
    sys.exit(1 if bad else 0)

main()
