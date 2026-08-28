# Penjelasan Teknis & Arsitektur Automation Testing (HRSV Dashboard)

Dokumen ini merangkum penjelasan teknis mengenai arsitektur kode, cara kerja **Data Binding**, alur script, dan fungsi kustom yang digunakan dalam proyek Automation Testing **Katalon Studio** untuk website **HRSV Dashboard**.

---

## 1. Arsitektur Proyek & Clean Code

Proyek ini dibangun menggunakan konsep **Modular & Reusable Framework**:
- **Script Test Case (`TC_Login` & `TC_KaryawanBaru`)**: Dibuat sangat ringkas, deklaratif, dan mudah dipahami bahkan oleh orang awam. Script hanya berisi alur pengujian tingkat tinggi (*High-Level Flow*).
- **Custom Keyword (`custom.WebUI`)**: Bertindak sebagai engine pusat yang mengenkapsulasi logika teknis interaksi web (dynamic locator, JavaScript datepicker injection, otentikasi, pengisian form, dan assertions).

---

## 2. Cara Kerja Katalon Data Binding (Dari Excel ke Script)

### Alur Kerja:
1. **File Excel (`.xlsx`)**: Berisi kumpulan data uji (kolom seperti `Nama`, `Email`, `NomorHP`, `Tipe`, `Expected`).
2. **Data Files**: Mendaftarkan file Excel ke Katalon Studio.
3. **Tab Variables & Data Binding di Test Case (`.tc`)**: Memetakan kolom di Excel agar terhubung ke variabel di dalam Katalon Studio (`DATA_COLUMN`).
4. **Engine Runner Katalon**: Saat test suite dijalankan dengan konfigurasi `Data Iteration: ALL`, engine Katalon secara otomatis mengeksekusi script berulang kali (*looping otomatis*) sebanyak baris yang ada di Excel tanpa perlu menulis perulangan `for` manual di dalam script.

---

## 3. Penjelasan Potongan Logika Script

### A. Guard Clause (Validasi Data Binding)
```groovy
if (!Tipe?.trim()) {
    KeywordUtil.markFailedAndStop('Data binding gagal: variable Tipe kosong')
}
```
- **Tujuan**: Memastikan data dari Excel berhasil masuk ke script. Jika data binding belum terpasang atau variabel kosong, pengujian akan langsung berhenti dengan pesan error yang jelas.

### B. Anti-Duplikasi Data Positif (Data Preparation)
```groovy
String emailVal = Email
String nomorHpVal = NomorHP
if (Tipe.equalsIgnoreCase('Positive')) {
    String suffix = System.currentTimeMillis().toString().takeRight(6)
    emailVal = Email.replace('@', suffix + '@')
    nomorHpVal = NomorHP + suffix
}
```
- **Tujuan**: Backend website HRSV menolak registrasi jika email atau nomor HP sudah pernah terdaftar di database (*already exist*).
- **Solusi**: Menggunakan 6 digit terakhir dari waktu milidetik saat ini (`System.currentTimeMillis()`) untuk menghasilkan email dan nomor HP unik setiap kali automation dijalankan, sehingga pengujian data positif selalu berhasil (*PASS*) tanpa konflik data duplikat.

---

## 4. Bedah Fungsi `Keywords/custom/WebUI.groovy`

File ini meng-inherit class `WebUiBuiltInKeywords` dari Katalon Studio dan menambahkan beberapa fungsi utilitas khusus:

### 1. `cari(String locator)` — Dynamic Element Finder
- **Fungsi**: Mengubah teks string menjadi objek `TestObject` Katalon secara instan saat runtime tanpa perlu membuat ratusan objek manual di *Object Repository*.
- **Aturan Selector**:
  - `@nama` ➔ Dikonversi otomatis ke XPath atribut: `//*[@name='nama']`
  - Diawali `/` atau `(` ➔ Dikonversi ke **XPath penuh**
  - Lainnya ➔ Dikonversi ke **CSS Selector**

### 2. `setDate(String name, String value)` — HTML5 Datepicker Handler
- **Fungsi**: Mengisi field `<input type="date">` (seperti Tanggal Lahir dan Tanggal Join).
- **Alasan**: Input tanggal tipe HTML5 sering memblokir perintah `setText` biasa karena memunculkan widget kalender browser. Fungsi ini menginjeksi nilai tanggal langsung ke prototipe DOM elemen via JavaScript dan memicu `input event`.

### 3. `login(String username, String password)`
- **Fungsi**: Membuka URL login, mengisi username dan password, mengambil screenshot form, mengklik tombol Login, dan menunggu hingga halaman berpindah.

### 4. `verifikasiLogin(String tipe)`
- **Positive Case**: Memverifikasi bahwa URL sudah berpindah dari halaman login (berhasil masuk).
- **Negative Case**: Memverifikasi bahwa kotak error merah (`alert-danger`) muncul dengan teks *"Username and/or Password is invalid"* dan URL tetap berada di halaman login.

### 5. `bukaFormKaryawanBaru()`
- **Fungsi**: Melakukan navigasi langsung ke URL `/#/form-karyawan-baru` dan menunggu hingga field Nama Lengkap terlihat di layar.

### 6. `isiFormKaryawan(...)`
- **Fungsi**: Mengisi 11 field form registrasi secara berurutan:
  - Input teks biasa (`Nama`, `Tempat Lahir`, `Nomor KTP`, `Email`, `Alamat KTP`).
  - Input tanggal via `setDate`.
  - Pengecekan kondisi Domisili: jika data domisili kosong, otomatis mencentang checkbox *"Alamat sama dengan KTP"*.
  - Upload file foto KTP & foto Selfie dari folder `Include/files/`.
  - Mengambil screenshot form setelah lengkap terisi.

### 7. `submitForm()`
- **Fungsi**: Mengklik tombol gradien biru (*Submit*) untuk mengirim data karyawan baru ke server.

### 8. `verifikasiSubmitKaryawan(String tipe, String expected)`
- **Positive Case**: Menunggu dan memvalidasi munculnya pop-up SweetAlert *"Data karyawan baru berhasil dibuat"*.
- **Negative Case**:
  1. Memastikan pop-up sukses **TIDAK MUNCUL** (`verifyTextNotPresent`).
  2. Memeriksa apakah pesan validasi spesifik sesuai kolom `Expected` di Excel muncul di layar.
  3. Mencatat finding/bug di log jika ada input tidak valid yang diterima oleh backend tanpa menggagalkan status test suite.
  4. Mengambil screenshot hasil akhir notifikasi.
