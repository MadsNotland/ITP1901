#!/usr/bin/env bash
set -euo pipefail

LOG_VNC=/tmp/vncserver.log
LOG_INIT=/tmp/desktop-init.log
LOG_NOVNC=/tmp/novnc.log

echo "[CHE] Starting desktop bootstrap..." | tee "$LOG_INIT"
export LC_ALL=C

mkdir -p ~/.vnc

# Generate and print a random password (>=6 chars)
# Use robust generation that won't fail on locale issues
if command -v openssl >/dev/null 2>&1; then
  VNC_PASSWORD=$(openssl rand -base64 12 | tr -dc 'A-Za-z0-9' | head -c 8)
else
  VNC_PASSWORD=$(dd if=/dev/urandom bs=16 count=1 2>/dev/null | base64 | tr -dc 'A-Za-z0-9' | head -c 8)
fi
echo "[CHE] VNC password: ${VNC_PASSWORD}" | tee -a "$LOG_INIT"

# Configure VNC password
vncpasswd -f <<<"${VNC_PASSWORD}" > ~/.vnc/passwd 2>>"$LOG_VNC"
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
echo "[CHE] (re)starting VNC server on :1 ..." | tee -a "$LOG_INIT"
vncserver -kill :1 >>"$LOG_VNC" 2>&1 || true
if ! vncserver :1 -localhost no -geometry 1280x800 >>"$LOG_VNC" 2>&1; then
  echo "[CHE][ERROR] Failed to start vncserver. See $LOG_VNC" | tee -a "$LOG_INIT"
  exit 1
fi

sleep 1
echo "[CHE] Checking VNC port (5901)..." | tee -a "$LOG_INIT"
if ! netstat -tln 2>/dev/null | grep -q ":5901"; then
  echo "[CHE][ERROR] VNC not listening on 5901. See $LOG_VNC" | tee -a "$LOG_INIT"
  exit 1
fi

# Start noVNC proxy on 0.0.0.0:6080 to 127.0.0.1:5901 in background
cd /opt/novnc
echo "[CHE] Starting noVNC on 0.0.0.0:6080..." | tee -a "$LOG_INIT"
./utils/novnc_proxy --vnc 127.0.0.1:5901 --listen 0.0.0.0:6080 >"$LOG_NOVNC" 2>&1 &
NOVNC_PID=$!
sleep 1
if ! netstat -tln 2>/dev/null | grep -q ":6080"; then
  echo "[CHE][ERROR] noVNC not listening on 6080. See $LOG_NOVNC" | tee -a "$LOG_INIT"
  exit 1
fi

echo "[CHE] VNC server started on :1 (0.0.0.0:5901)" | tee -a "$LOG_INIT"
echo "[CHE] noVNC started (PID: ${NOVNC_PID}, logs: $LOG_NOVNC)" | tee -a "$LOG_INIT"
echo "[CHE] Open the 6080 endpoint with /vnc.html and use the printed password." | tee -a "$LOG_INIT"
