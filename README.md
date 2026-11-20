Bu Loyhani asosiy vazafasi telegram birir bir davlatga bloklansa cheklab utish kamchiliklari bor ishlabman ustida
# Telegram MTProto Proxy (Fake-TLS) - Railway uchun

Bu loyiha Telegram uchun shaxsiy **MTProto Proxy** (Fake-TLS rejimi bilan) yaratish va uni **Railway** platformasida bepul (yoki hamyonbop tarifda) ishga tushirish uchun tayyorlandi.

## 📁 Fayllar tarkibi:
- `mtprotoproxy.py` - Proxyning asosiy dasturiy qismi.
- `config.py` - Sozlamalar fayli (parol, port, fake-tls domeni).
- `requirements.txt` - Kerakli kutubxonalar (`cryptography`).
- `Dockerfile` - Railway uchun konteyner sozlamasi.

---

## 🚀 Railway-ga joylash bo'yicha qo'llanma (Qadam-baqadam):

### 1. GitHub-ga yuklash:
1. GitHub-da yangi **Public** yoki **Private** repository (omborxona) yarating.
2. Ushbu papkadagi barcha fayllarni (`mtprotoproxy.py`, `config.py`, `requirements.txt`, `Dockerfile`, `railway.json`) o'sha repositoryga yuklang.

### 2. Railway-da ishga tushirish:
1. [railway.app](https://railway.app) saytiga kiring va GitHub profilingiz orqali kiring.
2. **New Project** tugmasini bosing va **Deploy from GitHub repo** ni tanlang.
3. Yangi yaratgan repositoryni tanlang va **Deploy** qiling.

### 3. Domen (Domain) ochish:
1. Loyihangiz ochilgandan so'ng, uning **Settings** (Sozlamalar) bo'limiga o'ting.
2. **Networking** qismidan **Generate Domain** tugmasini bosing.
3. Railway sizga domen taqdim etadi (masalan: `my-proxy-production.up.railway.app`).

---

## 🔗 Telegramga ulash (Proxy Link):

Proxy ishlashi uchun quyidagi ma'lumotlar kerak bo'ladi:
- **Server (Host):** Railway bergan domen (masalan: `my-proxy-production.up.railway.app`)
- **Port:** 443 (yoki Railway avtomatik ochgan tashqi port)
- **Secret (Maxfiy kalit):** `0123456789abcdef0123456789abcdef` (buni `config.py` faylidan o'zgartirishingiz mumkin)
- **Domain (Fake-TLS):** `www.google.com`

### Telegramda ulanish havolasi (link):
Ushbu havolani Telegramda oching (domen o'rniga o'zingizning Railway domeningizni yozing):
```
tg://proxy?server=SIZNING_DOMAIN&port=443&secret=ee0123456789abcdef0123456789abcdefwww.google.com
```
*(Eslatma: Fake-TLS sekret `ee` prefiksi va oxiriga domen hex kodiga ega bo'ladi, yoki oddiygina Telegram sozlamalaridan server, port va hex-secret ni kiritib ulanishingiz mumkin).*

## Windowsda fon va avtomatik ishga tushirish

Windows foydalanuvchilari `release\TelegramProxy.exe` yoki tayyor Windows ZIP paketidan foydalanishi mumkin; ishga tushirish uchun Python o'rnatish talab qilinmaydi. ZIP'ni to'liq chiqarib, `start.bat` ni oching.

`start.bat` proxy'ni yashirin fonda ishga tushiradi; ishga tushiruvchi oynani yopish proxy'ni to'xtatmaydi. Avtomatik ishga tushirish uchun `install_autostart.bat` ni bir marta ishga tushiring; proxy joriy foydalanuvchi Windows'ga kirganda boshlanadi. Uni o'chirish uchun `Win+R` orqali `shell:startup` papkasini ochib, `Telegram Proxy.lnk` yorlig'ini o'chiring.

`MY_DOMAIN = "127.0.0.1"` bo'lsa, havola faqat proxy ishlayotgan shu kompyuterda ishlaydi. Telefon yoki boshqa qurilmadan ulanish uchun o'sha qurilmadan ko'rinadigan LAN yoki public IP manzil kerak.
