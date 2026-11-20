# Telegram MTProto Proxy (Fake-TLS)

## Xaridor uchun qisqacha ma'lumot

Bu loyiha Telegram uchun MTProto proxy xizmatini o'zingizning kompyuteringizda yoki serveringizda ishga tushirishga mo'ljallangan. Fake-TLS rejimi ayrim tarmoqlarda proxy trafikini oddiy TLS trafikiga o'xshatishga yordam berishi mumkin.

Loyiha quyidagilarni o'z ichiga oladi:

- **Windows dasturi:** tayyor `TelegramProxy.exe`; foydalanuvchi kompyuteriga Python o'rnatish talab qilinmaydi.
- **Android ilovasi:** Python muhiti ichiga joylangan APK; proxy'ni telefonda fon xizmatida ishga tushiradi va Telegramga ulanish havolasini beradi.
- **Server manbasi:** Dockerfile va Python manba kodi orqali Linux serverda joylashtirish imkoniyati.

Bu VPN emas: u butun qurilmaning internet trafikini yo'naltirmaydi, faqat Telegram ilovasida sozlangan proxy ulanishiga xizmat qiladi.

## Muhim: proxy qayerda ishlaydi?

Windows dasturi va Android APK standart holatda proxy'ni faqat o'z qurilmasida, `127.0.0.1:8444` manzilida ishga tushiradi. `127.0.0.1` har doim aynan o'sha qurilmani anglatadi.

Telegram serverlariga chiqish ham proxy ishlayotgan qurilmaning interneti orqali amalga oshadi. Agar shu tarmoq Telegram serverlariga chiqishni bloklasa, lokal dastur blokni chetlab o'tishni kafolatlamaydi. Bunday holatda proxy'ni Telegram serverlariga ulana oladigan tashqi VPS/serverda ishga tushirib, Telegramni o'sha serverning ochiq manzili va portiga ulash kerak.

Fake-TLS hamma operator yoki tarmoqda ishlashiga kafolat yo'q. Natija serverning internetga ulanishi, portning ochiqligi va tarmoqdagi bloklash usuliga bog'liq.

## Windows

`release\TelegramProxy.exe` faylini bevosita ochsangiz konsol oynasi ko'rinadi. Yashirin fonda qo'lda ishga tushirish uchun loyiha papkasidan PowerShell'da `.\start_proxy.ps1` ni bajaring. Dastur Python talab qilmaydi; odatiy sozlamada proxy `8444` portida tinglaydi.

Joriy `MY_DOMAIN = "127.0.0.1"` sozlamasi bilan havola faqat shu kompyuterdagi Telegram uchun ishlaydi. Boshqa qurilmadan ulash uchun kompyuterga yetib boradigan LAN/public IP yoki domenni sozlash, Windows Firewall va kerak bo'lsa routerda TCP portni ochish zarur.

Windows'ga kirganda avtomatik va yashirin ishga tushirish uchun `install_autostart.bat` faylini bir marta ishga tushiring. Bu faqat joriy foydalanuvchining Startup papkasiga yorliq qo'shadi; administrator huquqi yoki Windows servisi talab qilinmaydi. Avtomatik ishga tushirishni o'chirish uchun `Win+R` orqali `shell:startup` papkasini ochib, `Telegram Proxy.lnk` yorlig'ini olib tashlang.

## Android

APK build natijasi: `android\app\build\outputs\apk\debug\app-debug.apk`. U Python 3.10 muhitini ichiga oladi, shuning uchun telefonga Python o'rnatish shart emas. Hozirgi build ARM64 va x86_64 qurilmalarni, Android 7.0 (API 24) va undan yangilarini qo'llaydi.

1. APK'ni telefonga ko'chirib o'rnating. Android so'rasa, ushbu manbadan ilova o'rnatishga ruxsat bering.
2. Ilovani ochib, **Proksini ishga tushirish** tugmasini bosing va bildirishnomaga ruxsat bering.
3. **Telegramga ulash** tugmasini bosing. Telegram proxy manzilini `127.0.0.1:8444` sifatida ko'rishi kerak.

Proxy ishlayotganida doimiy bildirishnoma ko'rinadi. To'xtatish uchun ilovadagi **Proksini to'xtatish** tugmasini bosing. Android APK faqat shu telefondagi Telegram uchun lokal proxy yaratadi; boshqa qurilmalarga proxy tarqatmaydi. Hozirgi `app-debug.apk` sinov/sideload uchun, Google Play'ga chiqariladigan release build emas.

## Linux server yoki Railway'ga joylashtirish

Loyihada `Dockerfile` mavjud. Tashqi serverda ishga tushirganda:

1. Python manbalari, `requirements.txt` va `Dockerfile`ni Git repozitoriyga joylang.
2. Dockerfile orqali deploy qiling va serverdagi `PORT` sozlamasini hosting platformasi talabiga moslang.
3. Hosting platformasi MTProto uchun zarur **kiruvchi TCP** ulanishni qo'llashi va tashqi portni server portiga yo'naltirishi kerak.
4. Telegramda platforma bergan ishlaydigan public host va tashqi TCP portdan foydalaning; `127.0.0.1` yoki faqat HTTP domenini masofaviy proxy manzili sifatida ishlatmang.

Railway'da oddiy HTTP domen yaratishning o'zi MTProto TCP trafikini yo'naltirishni anglatmaydi. Deploydan oldin tanlangan tarif va networking sozlamalarida tashqi TCP port/proxy imkoniyati borligini tekshiring.

## Sozlamalar va xavfsizlik

- `config.py` port, maxfiy kalit va TLS domenini belgilaydi. `SECRET` muhit o'zgaruvchisi bilan kalit berish mumkin.
- Ommaga ochiq serverga joylashtirishdan oldin standart/maxfiy kalitni albatta o'zingizga tegishli noyob kalitga almashtiring. Kalitni GitHub, skrinshot yoki ochiq chatga joylamang.
- `TLS_DOMAIN` hozir `www.google.com` qiymatida. Dastur bu standart qiymat haqida ogohlantiradi; sozlashda o'zingiz tanlagan mavjud domenni ishlating.
- Server porti internetdan yetib boriladigan bo'lishi va Telegram serverlariga chiquvchi ulanishga ruxsat berilishi kerak.

## Android APK'ni qayta yig'ish

Build kompyuteriga JDK 17, Android SDK va Python 3.10 kerak. `ANDROID_HOME` muhit o'zgaruvchisini Android SDK papkasiga sozlab, Windows'da quyidagini bajaring:

```powershell
android\gradlew.bat assembleDebug
```

APK `android\app\build\outputs\apk\debug\app-debug.apk` manzilida yaratiladi. APK foydalanuvchilarga yangilanish sifatida tarqatilganda, release buildni bir xil imzolash kaliti bilan yig'ish va `versionCode` qiymatini oshirish kerak.

## Aloqa va qo'llab-quvvatlash

Savollar va yordam uchun Telegram: [@UzbPython212](https://t.me/UzbPython212).
