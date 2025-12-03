package github.ianellis.colorchecker.opencv

import org.opencv.core.Mat
import org.opencv.core.MatOfDMatch
import org.opencv.features2d.DescriptorMatcher

fun DescriptorMatcher.knnMatch(obj: Mat, scene: Mat, k: Int = 2): List<MatOfDMatch> {
    val knnMatches = mutableListOf<MatOfDMatch>()
    this.knnMatch(obj, scene, knnMatches, k)
    return knnMatches
}