#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PID_FILE="$ROOT_DIR/.app.pid"
LOG_FILE="$ROOT_DIR/app.log"
JAVA_HOME_DEFAULT="/scratch/voggu/softwares/jdk-25.0.1"

setup_java() {
  local selected_java_home="${JAVA_HOME:-$JAVA_HOME_DEFAULT}"
  if [[ ! -x "$selected_java_home/bin/javac" ]]; then
    echo "JDK not found or invalid: $selected_java_home"
    echo "Set JAVA_HOME to a valid JDK path and retry."
    return 1
  fi
  export JAVA_HOME="$selected_java_home"
  export PATH="$JAVA_HOME/bin:$PATH"
}

usage() {
  cat <<'EOF'
Usage: ./appctl.sh {start|stop|restart|status|logs}

Commands:
  start    Start Spring Boot app in background
  stop     Stop running app
  restart  Restart app
  status   Show app status
  logs     Tail app log
EOF
}

is_running() {
  [[ -f "$PID_FILE" ]] || return 1
  local pid
  pid="$(cat "$PID_FILE")"
  [[ -n "$pid" ]] || return 1
  kill -0 "$pid" 2>/dev/null
}

start_app() {
  setup_java

  if is_running; then
    echo "App already running (pid $(cat "$PID_FILE"))."
    return 0
  fi

  cd "$ROOT_DIR"
  echo "Pulling latest changes..."
  git pull

  echo "Starting app..."
  nohup ./mvnw spring-boot:run >"$LOG_FILE" 2>&1 &
  echo $! >"$PID_FILE"
  sleep 2

  if is_running; then
    echo "App started (pid $(cat "$PID_FILE")). Log: $LOG_FILE"
  else
    echo "App failed to start. Check log: $LOG_FILE"
    rm -f "$PID_FILE"
    return 1
  fi
}

stop_app() {
  if ! is_running; then
    echo "App is not running."
    rm -f "$PID_FILE"
    return 0
  fi

  local pid
  pid="$(cat "$PID_FILE")"
  echo "Stopping app (pid $pid)..."
  kill "$pid" 2>/dev/null || true

  for _ in {1..20}; do
    if kill -0 "$pid" 2>/dev/null; then
      sleep 1
    else
      rm -f "$PID_FILE"
      echo "App stopped."
      return 0
    fi
  done

  echo "Force killing app (pid $pid)..."
  kill -9 "$pid" 2>/dev/null || true
  rm -f "$PID_FILE"
  echo "App stopped."
}

status_app() {
  setup_java >/dev/null 2>&1 || true
  if is_running; then
    echo "App is running (pid $(cat "$PID_FILE")). JAVA_HOME=${JAVA_HOME:-unset}"
  else
    echo "App is not running. JAVA_HOME=${JAVA_HOME:-unset}"
  fi
}

show_logs() {
  if [[ ! -f "$LOG_FILE" ]]; then
    echo "Log file not found: $LOG_FILE"
    return 1
  fi
  tail -f "$LOG_FILE"
}

cmd="${1:-}"
case "$cmd" in
  start) start_app ;;
  stop) stop_app ;;
  restart) stop_app; start_app ;;
  status) status_app ;;
  logs) show_logs ;;
  *) usage; exit 1 ;;
esac
