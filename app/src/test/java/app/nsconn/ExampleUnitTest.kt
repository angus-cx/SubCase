package app.nsconn

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun regex() {
        val regex0 = Regex("^https?://sub.store")
        val testUrl = "https://sub.store/api/utils/env"

        val isMatch = regex0.containsMatchIn(testUrl)
        println("Does the URL match regex0? $isMatch") // This should print: true
    }
}
