import custom.WebUI
import static custom.WebUI.cari
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testdata.TestData

TestData data = TestDataFactory.findTestData('Data Files/Login_Data')

for (int i = 1; i <= data.getRowNumbers(); i++) {

    WebUI.openBrowser("http://103.180.125.62:3880/#/login")

    WebUI.takeScreenshot()

    WebUI.setText(cari("//input[@placeholder='Masukkan username atau email anda...']"), data.getValue('Username', i))

    WebUI.setText(cari("//input[@placeholder='Masukkan password anda..']"), data.getValue('Password', i))

    WebUI.takeScreenshot()

    WebUI.click(cari("//button[contains(.,'Login')]"))

    WebUI.takeScreenshot()

    WebUI.closeBrowser()
}
