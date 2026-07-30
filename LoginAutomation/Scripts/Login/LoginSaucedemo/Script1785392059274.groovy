import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
 
 
// 1. Buka browser dan buka URL SauceDemo
WebUI.openBrowser('')
WebUI.navigateToUrl('https://www.saucedemo.com/')
WebUI.maximizeWindow()
 
// 2. Isi form login
WebUI.setText(findTestObject('Login/SauceDemo/Page_Login/input_Username'), 'standard_user')
WebUI.setText(findTestObject('Login/SauceDemo/Page_Login/input_Password'), 'secret_sauce')
WebUI.click(findTestObject('Login/SauceDemo/Page_Login/btn_Login'))
 
// 3. Verifikasi berhasil login & sudah masuk ke halaman Products (Inventory)
WebUI.verifyElementPresent(findTestObject('Login/SauceDemo/Page_Inventory/title_Products'), 10)
WebUI.verifyElementText(findTestObject('Login/SauceDemo/Page_Inventory/title_Products'), 'Products')
 
// 4. Buka menu (hamburger icon) untuk memastikan menu navigasi muncul
WebUI.click(findTestObject('Login/SauceDemo/Page_Inventory/btn_BurgerMenu'))
WebUI.verifyElementPresent(findTestObject('Login/SauceDemo/Page_Inventory/link_Logout'), 5)
 
WebUI.closeBrowser()
 