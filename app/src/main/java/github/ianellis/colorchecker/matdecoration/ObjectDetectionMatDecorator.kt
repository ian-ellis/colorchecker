package github.ianellis.colorchecker.matdecoration

import github.ianellis.colorchecker.objectdetection.ObjectDetector
import github.ianellis.colorchecker.objectdetection.ObjectDetector.MatchResult
import github.ianellis.colorchecker.opencv.MatOfPoint2f
import github.ianellis.colorchecker.opencv.corners
import github.ianellis.colorchecker.opencv.perspectiveTransform
import github.ianellis.colorchecker.opencv.quadrilateral
import github.ianellis.colorchecker.opencv.toQuadrilateral
import org.opencv.calib3d.Calib3d
import org.opencv.core.DMatch
import org.opencv.core.Mat
import org.opencv.core.MatOfKeyPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Scalar
import java.util.LinkedList

class ObjectDetectionMatDecorator(
    private val objectDetector: ObjectDetector,
    private val lineColor: Scalar = Scalar(0.0, 255.0, 0.0),
    private val lineThickness: Int = 5
) : MatDecorator {

    override fun decorate(scene: Mat): Mat {
        val result = objectDetector.findInScene(scene)
        return when (result) {
            is MatchResult.Match -> result.drawDetectedObjectBox(scene)
            MatchResult.NoMatch -> scene
        }
    }

    private fun MatchResult.Match.drawDetectedObjectBox(liveScene: Mat): Mat {
        val (obj, scene) = goodMatches.matchedPoints(this.obj.keyPoints, this.scene.keyPoints)

        val homographyMatrix = Calib3d.findHomography(obj, scene, Calib3d.RANSAC, 3.0).also {
            if (it.empty()) return liveScene
        }
        val sceneCorners = objectDetector.targetObject
            .corners()
            .perspectiveTransform(homographyMatrix)
        liveScene.quadrilateral(sceneCorners.toQuadrilateral(), lineColor, lineThickness)
        return liveScene
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
        return MatOfPoint2f(objList) to MatOfPoint2f(sceneList)
    }
}
