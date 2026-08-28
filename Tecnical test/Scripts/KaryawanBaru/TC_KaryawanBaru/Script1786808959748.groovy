import custom.WebUI
import com.kms.katalon.core.util.KeywordUtil
import internal.GlobalVariable as GlobalVariable

if (!Tipe?.trim()) {
    KeywordUtil.markFailedAndStop('Data binding gagal: variable Tipe kosong')
}

// 1. Data Preparation (Suffix unik untuk Positive Case agar data tidak duplikat)
String emailVal = Email
String nomorHpVal = NomorHP
if (Tipe.equalsIgnoreCase('Positive')) {
    String suffix = System.currentTimeMillis().toString().takeRight(6)
    emailVal = Email.replace('@', suffix + '@')
    nomorHpVal = NomorHP + suffix
}

// 2. Login & Navigasi ke Form Karyawan Baru
WebUI.login(GlobalVariable.USERNAME, GlobalVariable.PASSWORD)
WebUI.bukaFormKaryawanBaru()

// 3. Pengisian Form
WebUI.isiFormKaryawan(Nama, TempatLahir, TanggalLahir, NomorKTP, nomorHpVal, emailVal, TanggalJoin, AlamatKTP, Domisili, FileKTP, FileSelfie)

// 4. Submit & Verifikasi
WebUI.submitForm()
WebUI.verifikasiSubmitKaryawan(Tipe, Expected)

// 5. Tutup Browser
WebUI.closeBrowser()
