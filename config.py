import os

PORT = int(os.environ.get("PORT", 8444))
LISTEN_ADDR_IPV4 = os.environ.get("LISTEN_ADDR_IPV4", "0.0.0.0")
LISTEN_ADDR_IPV6 = os.environ.get("LISTEN_ADDR_IPV6", "::")

# Localhost uchun MY_DOMAIN ni 127.0.0.1 qilib belgilaymiz
MY_DOMAIN = "127.0.0.1"

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
