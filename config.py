import os

# Railway avtomatik beradigan portni oladi
PORT = int(os.environ.get("PORT", 8443))

# 32 ta belgidan iborat maxfiy kalit
USERS = {
    "tg": "0123456789abcdef0123456789abcdef"
}

# Barcha rejimlarni yoqamiz (oddiy va Fake-TLS havolalarini birga chiqarish uchun)
MODES = {
    "classic": True,
    "secure": True,
    "tls": True
}

TLS_DOMAIN = "www.google.com"
