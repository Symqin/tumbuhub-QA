package custom

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords
import internal.GlobalVariable as GlobalVariable

public class WebUI extends WebUiBuiltInKeywords {

    static openBrowser(String rawUrl) {
        super.openBrowser(rawUrl)
    }

    static getDriver() {
        return DriverFactory.getWebDriver()
    }

    /**
     * Membuat TestObject dinamis tanpa Object Repository:
     * - '@nama'               -> Berdasarkan attribute name (@name='nama')
     * - '//button[...]'       -> XPath
     * - 'button.btn-primary'  -> CSS Selector
     */
    static TestObject cari(String locator) {
        if (locator.startsWith('@')) {
            return new TestObject().addProperty('xpath', ConditionType.EQUALS, "//*[@name='${locator.substring(1)}']")
        }
        String selectorType = (locator.startsWith('/') || locator.startsWith('(')) ? 'xpath' : 'css'
        return new TestObject().addProperty(selectorType, ConditionType.EQUALS, locator)
    }

    /**
     * Mengisi native HTML input type="date" via JavaScript Dispatch Event
     */
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

    /**
     * Login ke HRSV Dashboard
     */
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

    /**
     * Verifikasi hasil Login (Positive vs Negative)
     */
    static void verifikasiLogin(String tipe) {
        if (tipe.equalsIgnoreCase('Positive')) {
            verifyNotMatch(getUrl(), '.*#/login.*', true)
        } else {
            waitForElementVisible(cari("//div[contains(@class,'alert-danger')]"), GlobalVariable.WAIT_TIMEOUT)
            verifyTextPresent('Username and/or Password is invalid', false)
            verifyMatch(getUrl(), '.*#/login.*', true)
        }
    }

    /**
     * Navigasi ke Form Karyawan Baru
     */
    static void bukaFormKaryawanBaru() {
        navigateToUrl(GlobalVariable.BASE_URL + "/#/form-karyawan-baru")
        waitForElementVisible(cari('@nama'), GlobalVariable.WAIT_TIMEOUT)
    }

    /**
     * Mengisi seluruh field Form Karyawan Baru
     */
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

    /**
     * Submit form karyawan baru
     */
    static void submitForm() {
        click(cari('//button[contains(@class,"button-gradient-blue")]'))
    }

    /**
     * Verifikasi hasil submit form karyawan baru (Positive vs Negative)
     */
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
}
