package github.ianellis.colorchecker.opencv

import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

fun List<Mat>.containsEmptyMat():Boolean {
    return any { it.empty() }
}

fun Mat.corners() = MatOfPoint2f(
    Point(0.0, 0.0),
    Point(this.cols().toDouble(), 0.0),
    Point(this.cols().toDouble(), this.rows().toDouble()),
    Point(0.0, this.rows().toDouble())
)

fun Mat.quadrilateral(quadrilateral: Quadrilateral, color: Scalar, thickness: Int) {
    Imgproc.line(this, quadrilateral.topLeft, quadrilateral.topRight, color, thickness)
    Imgproc.line(this, quadrilateral.topRight, quadrilateral.bottomRight, color, thickness)
    Imgproc.line(this, quadrilateral.bottomRight, quadrilateral.bottomLeft, color, thickness)
    Imgproc.line(this, quadrilateral.bottomLeft, quadrilateral.topLeft, color, thickness)
}

fun Mat.toGreyScale() = Mat().also {
    Imgproc.cvtColor(this, it, Imgproc.COLOR_BGR2GRAY)
}