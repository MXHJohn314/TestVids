package com.example.testvids

import io.appium.java_client.MobileElement
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.remote.AndroidMobileCapabilityType
import io.appium.java_client.remote.MobileCapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import org.testng.annotations.AfterTest
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import java.io.File
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AppiumTest {

    private lateinit var testTabletDriver: AndroidDriver<MobileElement>
    private lateinit var videoPhoneDriver: AndroidDriver<MobileElement>
    private val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
    private val TEST_BATCH = "YourTestBatchName"
    private val testName = "YourTestName"

    @BeforeTest
    fun setup() {
        val testTabletCapabilities = DesiredCapabilities().apply {
            setCapability(MobileCapabilityType.DEVICE_NAME, "TEST_TABLET")
            setCapability(MobileCapabilityType.PLATFORM_NAME, "Android")
            setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "com.example.yourapp")
            setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, "com.example.yourapp.MainActivity")
        }
        testTabletDriver = AndroidDriver(URL("http://localhost:4723/wd/hub"), testTabletCapabilities)

        val videoPhoneCapabilities = DesiredCapabilities().apply {
            setCapability(MobileCapabilityType.DEVICE_NAME, "VIDEO_PHONE")
            setCapability(MobileCapabilityType.PLATFORM_NAME, "Android")
            setCapability("adbExecTimeout", 90000)
        }
        videoPhoneDriver = AndroidDriver(URL("http://localhost:4724/wd/hub"), videoPhoneCapabilities)

        createFolderOnVideoPhone()
    }

    private fun createFolderOnVideoPhone() {
        val folderName = "${dateTime}_$TEST_BATCH"
        videoPhoneDriver.executeScript("mobile: shell", mapOf("command" to "mkdir", "args" to listOf("/sdcard/$folderName")))
    }

    @Test
    fun runTest() {
        val videoFileName = "${dateTime}_${TEST_BATCH}_${testName}.mp4"
        val videoFilePath = "/sdcard/${dateTime}_${TEST_BATCH}/${videoFileName}"

        startRecording(videoFileName)
        Thread.sleep(3000)
        stopRecordingAndMoveFile(videoFileName, videoFilePath)
    }

    private fun startRecording(videoFileName: String) {
        videoPhoneDriver.executeScript("mobile: startScreenRecording", mapOf("remotePath" to "/sdcard/$videoFileName"))
    }

    private fun stopRecordingAndMoveFile(videoFileName: String, videoFilePath: String) {
        videoPhoneDriver.executeScript("mobile: stopScreenRecording", emptyMap<String, Any>())
//        videoPhoneDriver.executeScript("mobile: shell", mapOf("command" to "mv", "args" to listOf("/sdcard/$videoFileName", videoFilePath)))
    }

    @AfterTest
    fun tearDown() {
        testTabletDriver.quit()
        videoPhoneDriver.quit()
    }
}
