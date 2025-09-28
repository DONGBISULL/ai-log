#!/bin/sh
echo "[ENTRYPOINT] Fixing filebeat.yml permission..."
chmod go-w /usr/share/filebeat/filebeat.yml
exec filebeat -e -c /usr/share/filebeat/filebeat.yml
