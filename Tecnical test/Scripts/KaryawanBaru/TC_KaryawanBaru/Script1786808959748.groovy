import custom.WebUI
import static custom.WebUI.cari
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testdata.TestData

// =====================================================================
// TC_KaryawanBaru - Data Driven Testing (data binding Katalon + perulangan FOR)
// Data dibaca dari Data Files/KaryawanBaru_Data.xlsx via findTestData()
// =====================================================================

String projectDir = RunConfiguration.getProjectDir()
String baseDir = projectDir + '/Include/files/'

// 1. Login sebagai Karyawan
WebUI.openBrowser("http://103.180.125.62:3880/#/login")

WebUI.setText(cari("//input[@placeholder='Masukkan username atau email anda...']"), "TestAccountKaryawan1")

WebUI.setText(cari("//input[@placeholder='Masukkan password anda..']"), "P@sswordK1")

WebUI.click(cari("//button[contains(.,'Login')]"))

WebUI.delay(2)

// 2. Buka halaman Form Karyawan Baru
WebUI.navigateToUrl("http://103.180.125.62:3880/#/form-karyawan-baru")

WebUI.waitForElementVisible(cari("@nama"), 15)

// 3. Baca data dari Data Files, lalu loop seluruh baris
TestData data = TestDataFactory.findTestData('Data Files/KaryawanBaru_Data')
int totalRows = data.getRowNumbers()
println('>>> Total data karyawan baru: ' + totalRows)

for (int i = 1; i <= totalRows; i++) {
    String nama        = data.getValue('Nama', i)
    String tempatLahir = data.getValue('TempatLahir', i)
    String tglLahir    = data.getValue('TanggalLahir', i)
    String nomorKtp    = data.getValue('NomorKTP', i)
    String nomorHp     = data.getValue('NomorHP', i)
    String email       = data.getValue('Email', i)
    String tglJoin     = data.getValue('TanggalJoin', i)
    String alamatKtp   = data.getValue('AlamatKTP', i)
    String domisili    = data.getValue('Domisili', i)
    String fileKtp     = data.getValue('FileKTP', i)
    String fileSelfie  = data.getValue('FileSelfie', i)
    String tipe        = data.getValue('Tipe', i)

    println('=== Iterasi ke-' + i + ' | ' + nama + ' | Tipe: ' + tipe + ' ===')

    WebUI.setText(cari("@nama"), nama)
    WebUI.setText(cari("@tempat_lahir"), tempatLahir)
    WebUI.setText(cari("@tanggal_lahir"), tglLahir)
    WebUI.setText(cari("@nomor_ktp"), nomorKtp)
    WebUI.setText(cari("@nomor_hp"), nomorHp)
    WebUI.setText(cari("@email"), email)
    WebUI.setText(cari("@tanggal_join"), tglJoin)
    WebUI.setText(cari("@alamat"), alamatKtp)

    // if else: jika domisili kosong -> centang checkbox
    if (domisili.isEmpty()) {
        WebUI.click(cari("//input[@type='checkbox']"))
    } else {
        WebUI.setText(cari("@domisili"), domisili)
    }

    WebUI.uploadFile(cari("@img_ktp"), baseDir + fileKtp)
    WebUI.uploadFile(cari("@img_selfie"), baseDir + fileSelfie)

    WebUI.takeScreenshot()

    WebUI.click(cari("//button[contains(@class,'button-gradient-blue')]"))

    WebUI.delay(2)

    // Reset form untuk iterasi berikutnya
    WebUI.refresh()
    WebUI.waitForElementVisible(cari("@nama"), 15)
}

WebUI.closeBrowser()
