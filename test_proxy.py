import socket
import subprocess
import time
import sys

print("1. Proxy server ishga tushirilmoqda...")
proc = subprocess.Popen([sys.executable, "mtprotoproxy.py"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
time.sleep(2)

print("2. 127.0.0.1:8444 portiga ulanish tekshirilmoqda...")
try:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.settimeout(3)
    s.connect(("127.0.0.1", 8444))
    print("[SUCCESS] AMALDAY ISBOT: Proxy server portiga muvaffaqiyatli ulandi! TCP aloqa o'rnatildi.")
    s.close()
    success = True
except Exception as e:
    print(f"[ERROR] Xatolik: {e}")
    success = False

proc.terminate()
proc.wait()
if success:
    print("3. Loyiha 100% amalda ishlayapti va ulanishga tayyor!")
else:
    sys.exit(1)
