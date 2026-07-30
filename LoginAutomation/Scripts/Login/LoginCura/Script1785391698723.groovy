import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject


// 1. Buka browser dan buka URL CURA Healthcare
WebUI.openBrowser('')
WebUI.navigateToUrl('https://katalon-demo-cura.herokuapp.com/')
WebUI.maximizeWindow()

// 2. Klik "Make Appointment" di homepage -> diarahkan ke form login
WebUI.click(findTestObject('Login/CURA/Page_Home/btn_MakeAppointment'))

// 3. Isi form login
WebUI.setText(findTestObject('Login/CURA/Page_Login/input_Username'), 'John Doe')
WebUI.setText(findTestObject('Login/CURA/Page_Login/input_Password'), 'ThisIsNotAPassword')
WebUI.click(findTestObject('Login/CURA/Page_Login/btn_Login'))

// 4. Verifikasi berhasil login & sudah masuk ke halaman/menu utama
WebUI.verifyElementPresent(findTestObject('Login/CURA/Page_MakeAppointment/header_MakeAppointment'), 10)
WebUI.verifyElementText(findTestObject('Login/CURA/Page_MakeAppointment/header_MakeAppointment'), 'Make Appointment')

// 5. Buka menu navigasi (ikon hamburger) untuk memastikan menu muncul
WebUI.click(findTestObject('Login/CURA/Page_MakeAppointment/icon_Menu'))
WebUI.verifyElementPresent(findTestObject('Login/CURA/Page_MakeAppointment/link_Logout'), 5)

WebUI.closeBrowser()