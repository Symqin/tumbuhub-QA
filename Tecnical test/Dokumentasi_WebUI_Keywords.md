# Dokumentasi Custom Keyword: `Keywords/custom/WebUI.groovy`

Project: **Katalon Studio** (HRSV Dashboard)
File: `Keywords/custom/WebUI.groovy`
Bahasa: **Groovy** (berjalan di JVM)

Kelas `WebUI` meng-**extends** `WebUiBuiltInKeywords`, sehingga mewarisi semua keyword bawaan Katalon (seperti `setText`, `click`, `uploadFile`, `verifyTextPresent`, dsb.) dan menambahkan keyword custom sendiri.

```groovy
package custom

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords
import internal.GlobalVariable as GlobalVariable

public class WebUI extends WebUiBuiltInKeywords {
```

---

## Cara memakai keyword custom ini

Di dalam Test Case (file `.groovy` di folder `Scripts/`):

```groovy
import custom.WebUI

WebUI.login('user', 'pass')                 // panggil keyword custom
WebUI.verifikasiLogin('Positive')
WebUI.isiFormKaryawan('Budi', 'Jakarta', '2000-01-01', '123', '0812', 'budi@mail.com', '2024-01-01', 'Alamat KTP', '', 'ktp.png', 'selfie.png')
```

Karena semua keyword `static`, langsung dipanggil via nama class `WebUI.xxx(...)` tanpa perlu instansiasi.

---

# 1. `static openBrowser(String rawUrl)`

**Fungsi:** Membuka browser dan menuju ke URL.

```groovy
static openBrowser(String rawUrl) {
    super.openBrowser(rawUrl)
}
```

- `super.openBrowser(...)` → memanggil keyword bawaan Katalon `WebUI.openBrowser`.
- Mengapa dibungkus? Agar pemanggilan bisa `WebUI.openBrowser(...)` dan memungkinkan ditimpa/diperluas nanti.

**Contoh pakai:**
```groovy
WebUI.openBrowser('http://103.180.125.62:3880/#/login')
```

---

# 2. `static getDriver()`

**Fungsi:** Mengambil instance **WebDriver Selenium** yang sedang aktif.

```groovy
static getDriver() {
    return DriverFactory.getWebDriver()
}
```

- `DriverFactory.getWebDriver()` → object WebDriver dari Katalon.
- Berguna untuk akses Selenium langsung (advanced), mis. mengambil screenshot raw, eksekusi JS kustom, atau akses API driver.

**Contoh pakai:**
```groovy
def driver = WebUI.getDriver()
driver.manage().window().maximize()
```

---

# 3. `static TestObject cari(String locator)`

**Fungsi:** Membuat `TestObject` **dinamis tanpa Object Repository**, otomatis mendeteksi 3 format locator.

```groovy
static TestObject cari(String locator) {
    if (locator.startsWith('@')) {
        return new TestObject().addProperty('xpath', ConditionType.EQUALS, "//*[@name='${locator.substring(1)}']")
    }
    String selectorType = (locator.startsWith('/') || locator.startsWith('(')) ? 'xpath' : 'css'
    return new TestObject().addProperty(selectorType, ConditionType.EQUALS, locator)
}
```

### Per baris
- `if (locator.startsWith('@'))` → kalau dimulai `@`, dianggap atribut `name`.
  - `"//*[@name='${locator.substring(1)}']"` → `@nama` menjadi XPath `//*[@name='nama']`.
- `String selectorType = ...` → menentukan tipe selector:
  - Dimulai `/` atau `(` → **xpath**.
  - Selain itu → **css**.
- `addProperty(tipe, ConditionType.EQUALS, locator)` → mengisi properti TestObject dengan kondisi `EQUALS`.

### Format yang didukung
| Input | Jenis | Hasil TestObject |
|---|---|---|
| `'@nama'` | attribute name | `//*[@name='nama']` |
| `'//button[contains(.,"Login")]'` | XPath | XPath tsb |
| `'button.btn-primary'` | CSS | CSS selector tsb |

**Contoh pakai:**
```groovy
WebUI.cari('@nama')                              // by name
WebUI.cari('//input[@placeholder="Masukkan password anda.."]')  // xpath
WebUI.cari('button.button-gradient-blue')        // css
```

---

# 4. `static void setDate(String name, String value)`

**Fungsi:** Mengisi input HTML `type="date"` secara native via **JavaScript** + dispatch event (karena `setText` biasa gagal pada date input).

```groovy
static void setDate(String name, String value) {
    if (value == null || value.trim().isEmpty()) {
        return
    }
    executeJavaScript(
        "var el = document.getElementsByName(arguments[0])[0];" +
        "var setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
        "setter.call(el, arguments[1]);" +
        "el.dispatchEvent(new Event('input', {bubbles:true}));",
        [name, value]
    )
}
```

### Per baris
- `if (value == null || value.trim().isEmpty()) return` → kalau kosong/null, tidak lakukan apa-apa.
- `executeJavaScript(script, [name, value])` → menjalankan JS dengan argumen `name` & `value`.
  - `getElementsByName(arguments[0])[0]` → ambil elemen input date by name.
  - `Object.getOwnPropertyDescriptor(...).set` → dapatkan setter value native (biar React/JS framework kenal perubahan).
  - `setter.call(el, value)` → set value.
  - `el.dispatchEvent(new Event('input', {bubbles:true}))` → trigger event `input` agar aplikasi update.

**Contoh pakai:**
```groovy
WebUI.setDate('tanggal_lahir', '2000-01-15')
WebUI.setDate('tanggal_join', '2024-03-01')
```

---

# 5. `static void login(String username, String password)`

**Fungsi:** Alur login lengkap ke HRSV Dashboard.

```groovy
static void login(String username, String password) {
    openBrowser(GlobalVariable.BASE_URL + "/#/login")
    setText(cari("//input[@placeholder='Masukkan username atau email anda...']"), username)
    setText(cari("//input[@placeholder='Masukkan password anda..']"), password)
    takeScreenshot()
    click(cari("//button[contains(.,'Login')]"))

    for (int w = 0; w < GlobalVariable.WAIT_TIMEOUT && getUrl().contains('#/login'); w++) {
        delay(1)
    }
    takeScreenshot()
}
```

### Per baris
- `openBrowser(GlobalVariable.BASE_URL + "/#/login")` → buka halaman login.
- `setText(cari(...username...), username)` → isi kolom username (by placeholder).
- `setText(cari(...password...), password)` → isi kolom password.
- `takeScreenshot()` → dokumentasi sebelum klik.
- `click(cari("//button[contains(.,'Login')]"))` → klik tombol Login.
- `for (w = 0; w < WAIT_TIMEOUT && getUrl().contains('#/login'); w++)` → **tunggu** sampai URL keluar dari `#/login` (login sukses) atau timeout.
  - `delay(1)` → jeda 1 detik per iterasi.
- `takeScreenshot()` → dokumentasi setelah login.

**Contoh pakai:**
```groovy
WebUI.login('TestAccountKaryawan1', 'P@sswordK1')
// atau pakai GlobalVariable
WebUI.login(GlobalVariable.USERNAME, GlobalVariable.PASSWORD)
```

---

# 6. `static void verifikasiLogin(String tipe)`

**Fungsi:** Memverifikasi hasil login — Positive (harus sukses) vs Negative (harus gagal).

```groovy
static void verifikasiLogin(String tipe) {
    if (tipe.equalsIgnoreCase('Positive')) {
        verifyNotMatch(getUrl(), '.*#/login.*', true)
    } else {
        waitForElementVisible(cari("//div[contains(@class,'alert-danger')]"), GlobalVariable.WAIT_TIMEOUT)
        verifyTextPresent('Username and/or Password is invalid', false)
        verifyMatch(getUrl(), '.*#/login.*', true)
    }
}
```

### Per baris
- `if (tipe.equalsIgnoreCase('Positive'))` → cek tipe case-insensitive.
- **Positive:** `verifyNotMatch(getUrl(), '.*#/login.*', true)` → pastikan URL **TIDAK** cocok regex `#/login` (artinya sudah masuk dashboard). Argumen `true` = `expected` berupa regex.
- **Negative:**
  - `waitForElementVisible(cari("//div[contains(@class,'alert-danger')]"), WAIT_TIMEOUT)` → tunggu elemen alert merah muncul.
  - `verifyTextPresent('Username and/or Password is invalid', false)` → pastikan teks error muncul (argumen `false` = teks literal, bukan regex).
  - `verifyMatch(getUrl(), '.*#/login.*', true)` → pastikan URL **tetap** di `#/login`.

**Contoh pakai:**
```groovy
WebUI.verifikasiLogin('Positive')   // harus berhasil masuk
WebUI.verifikasiLogin('Negative')   // harus tetap di halaman login + ada alert
```

---

# 7. `static void bukaFormKaryawanBaru()`

**Fungsi:** Navigasi ke halaman Form Karyawan Baru.

```groovy
static void bukaFormKaryawanBaru() {
    navigateToUrl(GlobalVariable.BASE_URL + "/#/form-karyawan-baru")
    waitForElementVisible(cari('@nama'), GlobalVariable.WAIT_TIMEOUT)
}
```

### Per baris
- `navigateToUrl(...)` → buka URL form karyawan baru.
- `waitForElementVisible(cari('@nama'), WAIT_TIMEOUT)` → tunggu field `@name='nama'` muncul (tanda form sudah siap).

**Contoh pakai:**
```groovy
WebUI.bukaFormKaryawanBaru()
```

---

# 8. `static void isiFormKaryawan(...)`

**Fungsi:** Mengisi seluruh field Form Karyawan Baru (teks, tanggal, checkbox, upload file).

```groovy
static void isiFormKaryawan(String nama, String tempatLahir, String tglLahir, String nomorKtp,
                            String nomorHp, String email, String tglJoin, String alamatKtp,
                            String domisili, String fileKtp, String fileSelfie) {
    String filesDir = RunConfiguration.getProjectDir() + '/Include/files/'

    setText(cari('@nama'), nama)
    setText(cari('@tempat_lahir'), tempatLahir)
    setDate('tanggal_lahir', tglLahir)
    setText(cari('@nomor_ktp'), nomorKtp)
    setText(cari('@nomor_hp'), nomorHp)
    setText(cari('@email'), email)
    setDate('tanggal_join', tglJoin)
    setText(cari('@alamat'), alamatKtp)

    if (!domisili?.trim()) {
        check(cari("//span[contains(text(),'Alamat sama dengan KTP')]/preceding-sibling::input[@type='checkbox']"))
    } else {
        setText(cari('@domisili'), domisili)
    }

    uploadFile(cari('@img_ktp'), filesDir + fileKtp)
    uploadFile(cari('@img_selfie'), filesDir + fileSelfie)
    takeScreenshot()
}
```

### Parameter (11 argumen)
| # | Parameter | Isi |
|---|---|---|
| 1 | `nama` | Nama karyawan |
| 2 | `tempatLahir` | Tempat lahir |
| 3 | `tglLahir` | Tanggal lahir |
| 4 | `nomorKtp` | Nomor KTP |
| 5 | `nomorHp` | Nomor HP |
| 6 | `email` | Email |
| 7 | `tglJoin` | Tanggal join |
| 8 | `alamatKtp` | Alamat KTP |
| 9 | `domisili` | Alamat domisili (kosongkan → centang "sama dengan KTP") |
| 10 | `fileKtp` | Nama file KTP (di folder `Include/files/`) |
| 11 | `fileSelfie` | Nama file selfie |

### Per baris
- `String filesDir = RunConfiguration.getProjectDir() + '/Include/files/'` → folder file upload.
- `setText(cari('@nama'), nama)` → isi field nama (dan seterusnya untuk field teks).
- `setDate('tanggal_lahir', tglLahir)` → isi tanggal via JS.
- `if (!domisili?.trim())` → kalau `domisili` null/kosong:
  - `check(...checkbox...)` → centang "Alamat sama dengan KTP".
  - `else` → kalau terisi, `setText(cari('@domisili'), domisili)`.
- `uploadFile(cari('@img_ktp'), filesDir + fileKtp)` → upload file KTP.
- `uploadFile(cari('@img_selfie'), filesDir + fileSelfie)` → upload file selfie.
- `takeScreenshot()` → dokumentasi.

**Contoh pakai:**
```groovy
WebUI.isiFormKaryawan(
    'Budi Santoso',      // nama
    'Jakarta',           // tempat lahir
    '1995-06-12',        // tgl lahir
    '3201012345670001',  // no KTP
    '081234567890',      // no HP
    'budi@mail.com',     // email
    '2024-05-01',        // tgl join
    'Jl. Merdeka No.1',  // alamat KTP
    '',                  // domisili -> kosong = centang checkbox
    'ktp.png',           // file KTP
    'selfie.png'         // file selfie
)
```

---

# 9. `static void submitForm()`

**Fungsi:** Klik tombol submit form.

```groovy
static void submitForm() {
    click(cari('//button[contains(@class,"button-gradient-blue")]'))
}
```

- `click(cari(...))` → klik tombol dengan class `button-gradient-blue` (XPath).

**Contoh pakai:**
```groovy
WebUI.submitForm()
```

---

# 10. `static void verifikasiSubmitKaryawan(String tipe, String expected)`

**Fungsi:** Memverifikasi hasil submit — Positive (harus sukses) vs Negative (harus gagal), plus deteksi pesan validasi.

```groovy
static void verifikasiSubmitKaryawan(String tipe, String expected) {
    if (tipe.equalsIgnoreCase('Positive')) {
        for (int w = 0; w < GlobalVariable.WAIT_TIMEOUT; w++) {
            if (verifyTextPresent('Data karyawan baru berhasil dibuat', false, FailureHandling.OPTIONAL)) {
                break
            }
            delay(1)
        }
        verifyTextPresent('Data karyawan baru berhasil dibuat', false)
    } else {
        verifyTextNotPresent('Data karyawan baru berhasil dibuat', false)
        if (expected?.trim()) {
            boolean isTextFound = false
            for (int w = 0; w < GlobalVariable.WAIT_TIMEOUT; w++) {
                if (verifyTextPresent(expected, false, FailureHandling.OPTIONAL)) {
                    isTextFound = true
                    break
                }
                delay(1)
            }
            if (!isTextFound) {
                println("⚠️ Expected bug/finding: pesan validasi '${expected}' tidak muncul di UI.")
            }
        }
    }
    takeScreenshot()
}
```

### Parameter
- `tipe` → `'Positive'` = harus sukses; selain itu = harus gagal.
- `expected` → pesan validasi yang diharapkan muncul saat gagal (opsional, untuk deteksi bug).

### Blok Positive
- `for (w = 0; w < WAIT_TIMEOUT; w++)` → polling maks 15 detik.
  - `verifyTextPresent('Data karyawan baru berhasil dibuat', false, FailureHandling.OPTIONAL)` → cek teks literal muncul; `OPTIONAL` = belum muncul bukan error.
  - `break` kalau ketemu; `delay(1)` kalau belum.
- `verifyTextPresent('Data karyawan baru berhasil dibuat', false)` → cek **final** (tanpa `OPTIONAL` → default `STOP_ON_FAILURE`). Kalau tak muncul sampai timeout → **FAIL & stop**.

### Blok Negative
- `verifyTextNotPresent('Data karyawan baru berhasil dibuat', false)` → pastikan pesan sukses **TIDAK** muncul (kalau muncul → FAIL).
- `if (expected?.trim())` → lanjut hanya jika `expected` terisi.
- Polling `expected` (sama pola seperti di atas).
- `if (!isTextFound) println("⚠️ ...")` → kalau `expected` tak muncul, **cuma print finding**, tidak fail.

### Penutup
- `takeScreenshot()` → dokumentasi semua kasus.

**Contoh pakai:**
```groovy
// Positive: harus sukses
WebUI.verifikasiSubmitKaryawan('Positive', '')

// Negative + cek pesan validasi tertentu
WebUI.verifikasiSubmitKaryawan('Negative', 'Email sudah terdaftar')

// Negative tanpa cek pesan spesifik
WebUI.verifikasiSubmitKaryawan('Negative', '')
```

---

# Ringkasan Tabel Keyword

| Keyword | Fungsi | Butuh TestObject? |
|---|---|---|
| `openBrowser(url)` | Buka browser | – |
| `getDriver()` | Ambil WebDriver Selenium | – |
| `cari(locator)` | Buat TestObject dinamis | – |
| `setDate(name, value)` | Isi input date via JS | – |
| `login(user, pass)` | Login HRSV + tunggu | internal |
| `verifikasiLogin(tipe)` | Validasi hasil login | internal |
| `bukaFormKaryawanBaru()` | Navigasi form karyawan | internal |
| `isiFormKaryawan(...)` | Isi seluruh form | internal |
| `submitForm()` | Klik submit | internal |
| `verifikasiSubmitKaryawan(tipe, expected)` | Validasi hasil submit | – |

---

# Catatan Global Variables
Diambil dari `Profiles/default.glbl`:

| Variabel | Nilai | Kegunaan |
|---|---|---|
| `BASE_URL` | `http://103.180.125.62:3880` | URL aplikasi |
| `USERNAME` | `TestAccountKaryawan1` | Akun login |
| `PASSWORD` | `P@sswordK1` | Password login |
| `WAIT_TIMEOUT` | `15` | Batas tunggu elemen (detik) |

---

# Contoh Test Case Lengkap (KaryawanBaru)

`Scripts/KaryawanBaru/TC_KaryawanBaru/Script1786808959748.groovy`:

```groovy
import custom.WebUI
import com.kms.katalon.core.util.KeywordUtil
import internal.GlobalVariable as GlobalVariable

if (!Tipe?.trim()) {
    KeywordUtil.markFailedAndStop('Data binding gagal: variable Tipe kosong')
}

// Data Preparation (suffix unik utk Positive case)
String emailVal = Email
String nomorHpVal = NomorHP
if (Tipe.equalsIgnoreCase('Positive')) {
    String suffix = System.currentTimeMillis().toString().takeRight(6)
    emailVal = Email.replace('@', suffix + '@')
    nomorHpVal = NomorHP + suffix
}

// Alur test
WebUI.login(GlobalVariable.USERNAME, GlobalVariable.PASSWORD)
WebUI.bukaFormKaryawanBaru()
WebUI.isiFormKaryawan(Nama, TempatLahir, TanggalLahir, NomorKTP, nomorHpVal, emailVal, TanggalJoin, AlamatKTP, Domisili, FileKTP, FileSelfie)
WebUI.submitForm()
WebUI.verifikasiSubmitKaryawan(Tipe, Expected)
WebUI.closeBrowser()
```

Variabel `Tipe`, `Email`, `NomorHP`, dll. dibinding dari data file `Data Files/KaryawanBaru_Data.xlsx`.
