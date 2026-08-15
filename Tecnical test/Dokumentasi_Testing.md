# Dokumentasi Automation Testing — HRSV Dashboard

## 1. Informasi Umum

| Item | Keterangan |
|------|-----------|
| **Aplikasi** | HRSV Dashboard |
| **URL** | `http://103.180.125.62:3880/#/login` |
| **Tools** | Katalon Studio (Enterprise) |
| **Metode** | Data Driven Testing (data binding + perulangan `for`) |
| **Sistem** | Website management kegiatan kerja HRD (cuti, lembur, slip gaji, administrasi karyawan) |
| **Akun Karyawan** | Username: `TestAccountKaryawan1` / Password: `P@sswordK1` |

---

## 2. Struktur Proyek

```
Tecnical test/
├── Data Files/
│   ├── Login_Data.xlsx          # Data binding login (positive & negative)
│   └── KaryawanBaru_Data.xlsx   # Data binding form karyawan baru
├── Keywords/
│   └── custom/
│       └── WebUI.groovy         # Custom keyword (cari() dll.)
├── Scripts/
│   ├── Login/TC_Login/Script*.groovy
│   └── KaryawanBaru/TC_KaryawanBaru/Script*.groovy
├── Test Cases/
│   ├── Login/TC_Login.tc
│   └── KaryawanBaru/TC_KaryawanBaru.tc
├── Test Suites/
│   ├── TS_All.ts
│   └── TS_All.groovy
├── Profiles/
│   └── default.glbl             # Global Variable
└── Include/files/               # Sample file upload (.jpg, .txt)
```

---

## 3. Custom Keyword

File: `Keywords/custom/WebUI.groovy`

Keyword `cari(locator)` membuat objek elemen dinamis **tanpa Object Repository**:

| Format locator | Hasil |
|---|---|
| `cari("@nama")` | `//*[@name='nama']` (berdasar atribut `name`) |
| `cari("//input[@placeholder=...]")` | XPath penuh |
| `cari("button")` | CSS selector (tag `button`) |

Keyword lain: `openBrowser()`, `getDriver()`, `comment()`, `clickJS()`.

---

## 4. Global Variable (Profiles/default.glbl)

| Variabel | Nilai |
|----------|-------|
| `BASE_URL` | `http://103.180.125.62:3880` |
| `USERNAME` | `TestAccountKaryawan1` |
| `PASSWORD` | `P@sswordK1` |
| `WAIT_TIMEOUT` | `15` |

---

## 5. Test Case

### 5.1 TC_Login (Login Karyawan)
- **Data binding**: `Data Files/Login_Data`
- **Alur**: perulangan `for` membaca seluruh baris data → buka halaman login → isi username & password → klik Login → screenshot.
- **Data uji**:

| Username | Password | Tipe |
|----------|----------|------|
| TestAccountKaryawan1 | P@sswordK1 | Positive |
| TestAccountKaryawan1 | passwordSalah123 | Negative |
| *(kosong)* | *(kosong)* | Negative |

### 5.2 TC_KaryawanBaru (Form Karyawan Baru)
- **Data binding**: `Data Files/KaryawanBaru_Data`
- **Alur**: login → buka `/#/form-karyawan-baru` → perulangan `for` seluruh baris → isi form → upload foto → submit → screenshot → refresh untuk iterasi berikut.
- **Field**: Nama, Tempat Lahir, Tanggal Lahir, Nomor KTP, Nomor HP, Email, Tanggal Join, Alamat KTP, Domisili, Foto KTP, Foto Selfie.
- **Struktur kontrol**: `for` (perulangan data) + `if/else` (cek domisili kosong → centang checkbox).

---

## 6. Cara Menjalankan

1. Buka proyek **Tecnical test** di Katalon Studio.
2. Pastikan Data Files (`Login_Data`, `KaryawanBaru_Data`) sudah tampil di **Data Files** tree (daftarkan lewat UI jika belum).
3. Jalankan **Test Suites → TS_All**.
4. Hasil screenshot & report otomatis tersimpan di folder `Reports/`.

---

## 7. Hasil Eksekusi

*(diisi setelah menjalankan test)*

| Test Case | Iterasi | Hasil | Keterangan |
|-----------|---------|-------|------------|
| TC_Login | 1 | | |
| TC_Login | 2 | | |
| TC_Login | 3 | | |
| TC_KaryawanBaru | 1–12 | | |

---

## 8. Catatan

- Field login HRSV **tidak memiliki atribut `name`**, sehingga memakai XPath berbasis `placeholder`.
- Field form karyawan baru memakai atribut `name` (contoh `@nama`, `@email`, `@img_ktp`).
- Data binding memakai `TestDataFactory.findTestData()` (fitur native Katalon).
