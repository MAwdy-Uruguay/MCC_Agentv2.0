package com.example.mccagent

import com.example.mccagent.config.ApiConfig
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiConfigHttpsPolicyTest {

    @Test
    fun `las URLs por defecto usan HTTPS en todos los entornos`() {
        assertTrue("DEV debe usar HTTPS", ApiConfig.defaultDevUrl.startsWith("https://"))
        assertTrue("PREPROD debe usar HTTPS", ApiConfig.defaultPreprodUrl.startsWith("https://"))
        assertTrue("PROD debe usar HTTPS", ApiConfig.defaultProdUrl.startsWith("https://"))
    }
}
