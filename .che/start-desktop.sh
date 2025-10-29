#!/usr/bin/env bash
set -euo pipefail

mkdir -p ~/.vnc

# Generate and print a random password (>=6 chars)
VNC_PASSWORD=$(tr -dc 'A-Za-z0-9' </dev/urandom | head -c 6)
echo "[CHE] VNC password: ${VNC_PASSWORD}"

# Configure VNC password
vncpasswd -f <<<"${VNC_PASSWORD}" > ~/.vnc/passwd
chmod 600 ~/.vnc/passwd

# Restart VNC server on :1 and bind to 0.0.0.0
vncserver -kill :1 >/dev/null 2>&1 || true
vncserver :1 -localhost no || true

# Start noVNC proxy on 0.0.0.0:6080 to 127.0.0.1:5901
cd /opt/novnc
nohup ./utils/novnc_proxy --vnc 127.0.0.1:5901 --listen 0.0.0.0:6080 >/tmp/novnc.log 2>&1 &
echo "[CHE] noVNC started on 0.0.0.0:6080 (logs: /tmp/novnc.log)"
