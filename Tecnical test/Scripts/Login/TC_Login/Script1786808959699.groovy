import custom.WebUI
import com.kms.katalon.core.util.KeywordUtil

if (!Tipe?.trim()) {
    KeywordUtil.markFailedAndStop('Data binding gagal: variable Tipe kosong')
}

// 1. Lakukan Login
WebUI.login(Username, Password)

// 2. Verifikasi Hasil
WebUI.verifikasiLogin(Tipe)

// 3. Tutup Browser
WebUI.closeBrowser()
