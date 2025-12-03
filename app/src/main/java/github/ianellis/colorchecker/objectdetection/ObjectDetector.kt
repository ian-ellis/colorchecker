package github.ianellis.colorchecker.objectdetection

import org.opencv.core.DMatch
import org.opencv.core.Mat
import org.opencv.core.MatOfKeyPoint

interface ObjectDetector {
    sealed interface MatchResult {
        data object NoMatch : MatchResult
        data class Match(
            val obj: ObjectDetection,
            val scene: ObjectDetection,
            val goodMatches: List<DMatch>
        ) : MatchResult
    }
    val targetObject: Mat
    fun findInScene(scene: Mat): MatchResult
}

data class ObjectDetection(
    val keyPoints: MatOfKeyPoint,
    val descriptor: Mat,
) {
    val empty = descriptor.empty()

    fun compatibleWith(other: ObjectDetection): Boolean {
        return this.descriptor.cols() == other.descriptor.cols() &&
                this.descriptor.type() == other.descriptor.type()
    }
}