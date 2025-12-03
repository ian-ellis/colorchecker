package github.ianellis.colorchecker.objectdetection

import github.ianellis.colorchecker.objectdetection.ObjectDetector.MatchResult
import github.ianellis.colorchecker.opencv.containsEmptyMat
import github.ianellis.colorchecker.opencv.knnMatch
import github.ianellis.colorchecker.opencv.toGreyScale
import org.opencv.core.DMatch
import org.opencv.core.Mat
import org.opencv.core.MatOfDMatch
import org.opencv.core.MatOfKeyPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.features2d.BFMatcher
import org.opencv.features2d.ORB
import java.util.LinkedList

class ORBObjectDetector(
    override val targetObject: Mat,
    private val config: Config = defaultConfig
) : ObjectDetector {
    companion object {
        private val defaultConfig = Config(10, 0.75f)
    }

    class Config(
        val minMatchingPoints: Int,
        val distanceThreshold: Float,
    )


    override fun findInScene(scene: Mat): MatchResult {
        // Input validation
        if (listOf(targetObject, scene).containsEmptyMat()) return MatchResult.NoMatch

        // Convert to grayscale for feature detection
        val objectGreyScale = targetObject.toGreyScale()
        val sceneGreyScale = scene.toGreyScale()

        val detector = ORB.create()

        val objectDetection = detector.detect(objectGreyScale)
        val sceneDetection = detector.detect(sceneGreyScale)

        if (!canCompareDetections(objectDetection, sceneDetection)) {
            return MatchResult.NoMatch
        }

        val matcher = BFMatcher.create(BFMatcher.BRUTEFORCE)
        val knnMatches = matcher.knnMatch(objectDetection.descriptor, sceneDetection.descriptor)

        val goodMatches = knnMatches.getGoodMatches()

        return if (goodMatches.size >= config.minMatchingPoints) {
            MatchResult.Match(objectDetection, sceneDetection, goodMatches)
        } else {
            MatchResult.NoMatch
        }
    }

    private fun List<MatOfDMatch>.getGoodMatches(): List<DMatch> {
        return this.asSequence()
            .map { it.toArray() }
            .filter { it.size > 1 }
            .filter { matches ->
                matches[0].distance < config.distanceThreshold * matches[1].distance
            }
            .map { matches -> matches[0] }
            .toList()
    }

    private fun canCompareDetections(obj: ObjectDetection, scene: ObjectDetection): Boolean {
        return when {
            obj.empty || scene.empty -> false
            else -> obj.compatibleWith(scene)
        }
    }

    private fun List<DMatch>.matchedPoints(
        objectKeyPoints: MatOfKeyPoint,
        sceneKeyPoints: MatOfKeyPoint,
    ): Pair<MatOfPoint2f, MatOfPoint2f> {
        val objList = LinkedList<Point>()
        val sceneList = LinkedList<Point>()

        val keypointsObjectList = objectKeyPoints.toList()
        val keypointsSceneList = sceneKeyPoints.toList()

        for (match in this) {
            objList.add(keypointsObjectList[match.queryIdx].pt)
            sceneList.add(keypointsSceneList[match.trainIdx].pt)
        }
        return github.ianellis.colorchecker.opencv.MatOfPoint2f(objList) to github.ianellis.colorchecker.opencv.MatOfPoint2f(
            sceneList
        )
    }

    private fun ORB.detect(image: Mat, mask: Mat = Mat()): ObjectDetection {
        val keyPoints = MatOfKeyPoint()
        val descriptor = Mat()
        this.detectAndCompute(image, mask, keyPoints, descriptor)
        return ObjectDetection(keyPoints, descriptor)
    }
}