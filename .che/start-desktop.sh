#!/usr/bin/env bash
set -euo pipefail

mkdir -p ~/.vnc

# Generate and print a random password (>=6 chars)
VNC_PASSWORD=$(tr -dc 'A-Za-z0-9' </dev/urandom | head -c 6)
echo "[CHE] VNC password: ${VNC_PASSWORD}"

# Configure VNC password
vncpasswd -f <<<"${VNC_PASSWORD}" > ~/.vnc/passwd
chmod 600 ~/.vnc/passwd

# Ensure xstartup exists so the VNC session launches a desktop
cat > ~/.vnc/xstartup <<'EOF'
#!/usr/bin/env bash
unset SESSION_MANAGER
unset DBUS_SESSION_BUS_ADDRESS
export XDG_RUNTIME_DIR=/tmp/xdg_runtime_$USER
mkdir -p "$XDG_RUNTIME_DIR"
chmod 700 "$XDG_RUNTIME_DIR"

if command -v startxfce4 >/dev/null 2>&1; then
  exec startxfce4
elif command -v xfce4-session >/dev/null 2>&1; then
  exec xfce4-session
else
  exec xterm
fi
EOF
chmod +x ~/.vnc/xstartup

# Restart VNC server on :1 and bind to 0.0.0.0
vncserver -kill :1 >/dev/null 2>&1 || true
vncserver :1 -localhost no -geometry 1280x800 || true

# Start noVNC proxy on 0.0.0.0:6080 to 127.0.0.1:5901 in background
cd /opt/novnc
echo "[CHE] Starting noVNC on 0.0.0.0:6080..."
./utils/novnc_proxy --vnc 127.0.0.1:5901 --listen 0.0.0.0:6080 >/tmp/novnc.log 2>&1 &
NOVNC_PID=$!

echo "[CHE] VNC server started on :1 (0.0.0.0:5901)"
echo "[CHE] noVNC started (PID: ${NOVNC_PID}, logs: /tmp/novnc.log)"
echo "[CHE] Open the 6080 endpoint with /vnc.html and use the printed password."
