import os

# Railway avtomatik beradigan portni oladi
PORT = int(os.environ.get("PORT", 8443))

# 32 ta belgidan iborat maxfiy kalit (hex formatda)
# Buni o'zingiz xohlagan 32 ta 0-9 va a-f belgilariga o'zgartiring
USERS = {
    "tg": "0123456789abcdef0123456789abcdef"
}

# Fake-TLS rejimi (blokirovkalarni chetlab o'tish uchun eng yaxshi usul)
MODES = {
    "classic": False,
    "secure": False,
    "tls": True
}

# Fake-TLS uchun niqoblanadigan sayt domeni
TLS_DOMAIN = "www.google.com"
