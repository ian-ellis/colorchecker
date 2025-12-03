package github.ianellis.colorchecker

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import github.ianellis.colorchecker.objectdetection.ORBObjectDetector
import github.ianellis.colorchecker.objectdetection.ORBObjectDetector.Config
import github.ianellis.colorchecker.objectdetection.ObjectDetector
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import github.ianellis.colorchecker.test.R as TestR

@RunWith(AndroidJUnit4::class)
class ORBObjectDetectorTest {

    private val config = Config(50, 0.6f)

    @Test
    fun ORBObjectDetector_Matches_PassImage() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val testContext = InstrumentationRegistry.getInstrumentation().context
        assertTrue(OpenCVLoader.initLocal())
        val reference = Utils.loadResource(appContext, R.raw.reference)
        val input = Utils.loadResource(testContext, TestR.raw.pass)
        val result = ORBObjectDetector(reference).findInScene(input)
        assertTrue(result is ObjectDetector.MatchResult.Match)
    }

    @Test
    fun ORBObjectDetector_DoesNotMatch_FailImage() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val testContext = InstrumentationRegistry.getInstrumentation().context
        assertTrue(OpenCVLoader.initLocal())
        val reference = Utils.loadResource(appContext, R.raw.reference)
        val input = Utils.loadResource(testContext, TestR.raw.fail)

        val result = ORBObjectDetector(reference, config).findInScene(input)
        assertTrue(result is ObjectDetector.MatchResult.NoMatch)
    }
}