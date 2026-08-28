# Dokumentasi Automation Testing — HRSV Dashboard

## 1. Informasi Umum

| Item | Keterangan |
|------|-----------|
| **Aplikasi** | HRSV Dashboard |
| **URL** | `http://103.180.125.62:3880/#/login` |
| **Tools** | Katalon Studio (Enterprise) |
| **Metode** | Data Driven Testing via Katalon Data Binding |
| **Sistem** | Website management kegiatan kerja HRD |
| **Akun Karyawan** | Username: `TestAccountKaryawan1` / Password: `P@sswordK1` |

---

## 2. Struktur Proyek

```
Tecnical test/
├── Data Files/
│   ├── Login_Data.xlsx          # Data binding login (3 baris)
│   └── KaryawanBaru_Data.xlsx   # Data binding form karyawan baru (14 baris)
├── Keywords/
│   └── custom/
│       └── WebUI.groovy         # Custom keyword (cari() locator dinamis)
├── Scripts/
│   ├── Login/TC_Login/Script*.groovy
│   └── KaryawanBaru/TC_KaryawanBaru/Script*.groovy
├── Test Cases/
│   ├── Login/TC_Login.tc        # Variable binding: Username, Password, Tipe
│   └── KaryawanBaru/TC_KaryawanBaru.tc # Variable binding: 13 kolom
├── Test Suites/
│   └── TS_All.ts
└── Include/files/               # Sample file upload (.jpg, .txt)
```

---

## 3. Custom Keyword

File: `Keywords/custom/WebUI.groovy`

Keyword `cari(locator)` membuat test object secara instan dan dinamis tanpa Object Repository:
- `cari("@nama")` ➔ `//*[@name='nama']`
- `cari("//input[...]")` ➔ XPath penuh
- `cari("button")` ➔ CSS Selector

---

## 4. Test Case & Data Binding

### 4.1 TC_Login
- **Data Binding:** `Data Files/Login_Data`
- **Variabel:** `Username`, `Password`, `Tipe`
- **Total Iterasi:** 3 data (1 Positive, 2 Negative)

### 4.2 TC_KaryawanBaru
- **Data Binding:** `Data Files/KaryawanBaru_Data`
- **Variabel:** `Nama`, `TempatLahir`, `TanggalLahir`, `NomorKTP`, `NomorHP`, `Email`, `TanggalJoin`, `AlamatKTP`, `Domisili`, `FileKTP`, `FileSelfie`, `Tipe`, `Expected`
- **Total Iterasi:** 14 data (4 Positive, 10 Negative)

---

## 5. Cara Menjalankan

1. Buka folder proyek `C:\Users\mutaq\Documents\code\qa\tumbuhub-QA\Tecnical test` di Katalon Studio.
2. Buka Test Suites `TS_All`.
3. Klik tombol **Run** (Chrome).
