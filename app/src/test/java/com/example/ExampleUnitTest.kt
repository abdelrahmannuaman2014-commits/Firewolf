package com.example

import com.example.util.AppLanguage
import com.example.util.Strings
import com.example.util.SystemTelemetry
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testAntiFraudHashGeneration() {
        val testCode = "PUBG-120FPS-HDR-ULTRA-2026"
        val hash = SystemTelemetry.generateAntiFraudHash(testCode)
        assertNotNull(hash)
        assertTrue("Hash should start with SHA256:", hash.startsWith("SHA256:"))
        assertEquals(hash, SystemTelemetry.generateAntiFraudHash(testCode))
    }

    @Test
    fun testLocalizationDictionary() {
        val appTitleAr = Strings.get("app_title", AppLanguage.ARABIC)
        val appTitleEn = Strings.get("app_title", AppLanguage.ENGLISH)
        assertEquals("فاير ولف", appTitleAr)
        assertEquals("Fire Wolf", appTitleEn)

        val quickBoostAr = Strings.get("dash_quick_boost", AppLanguage.ARABIC)
        val quickBoostEn = Strings.get("dash_quick_boost", AppLanguage.ENGLISH)
        assertEquals("تعزيز فائق الآن", quickBoostAr)
        assertEquals("Quick Turbo Boost", quickBoostEn)
    }
}
