import os

PORT = int(os.environ.get("PORT", 8444))
MY_DOMAIN = os.environ.get("MY_DOMAIN", "127.0.0.1")

# Doimiy static secret
secret = os.environ.get("SECRET", "8a592c9e619e176fdfec261ed7d56eab")

USERS = {
    "myuser": secret
}

# Barcha rejimlarni yoqamiz
MODES = {
    "classic": True,
    "secure": True,
    "tls": True
}

TLS_DOMAIN = "www.google.com"
