package custom

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords

import groovy.json.JsonOutput
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static org.mockito.ArgumentMatchers.startsWith

public class WebUI extends WebUiBuiltInKeywords {

	static openBrowser(String rawUrl) {
		//		boolean isOpen = false
		//		try {
		//			getDriver()
		//			isOpen = true
		//		} catch (e) { isOpen = false }
		//		def args = []
		//		args << "--password-store=basic" //Menghilangkan popup "Change your password"
		//		args << "--disable-save-password-bubble" //Menghilangkan popup "Change your password"
		//		RunConfiguration.setWebDriverPreferencesProperty("args", args)
		//		def prefs = [:]
		//		prefs.'credentials_enable_service' = false //Menghilangkan popup "Change your password"
		//		prefs.'profile.password_manager_enabled' = false //Menghilangkan popup "Change your password"
		//		prefs.'profile.password_manager_leak_detection' = false //Menghilangkan popup "Change your password"
		//		RunConfiguration.setWebDriverPreferencesProperty('prefs', prefs)
		//		if (isOpen == false) {
		//			super.openBrowser(rawUrl)
		//		}
		super.openBrowser(rawUrl)
	}

	static getDriver() {
		return DriverFactory.getWebDriver()
	}

	static cari(String locator) {
		if (locator.startsWith('@')) {
			return new TestObject().addProperty('xpath', ConditionType.EQUALS, "//*[@name='${locator.substring(1)}']")
		}
		return new TestObject().addProperty(locator.startsWith('/')||locator.startsWith('(')?'xpath':'css', ConditionType.EQUALS, locator)
	}


	static clickJS(String locator1) {
		super.executeJavaScript("elemet.click()", [])
	}

	static void comment (def message) {
		super.comment(JsonOutput.prettyPrint(JsonOutput.toJson(message)))
	}
}
