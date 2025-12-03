package github.ianellis.colorchecker.opencv

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point

//mock constructor
fun MatOfPoint2f(list: List<Point>) = MatOfPoint2f().also {
    it.fromList(list)
}

fun MatOfPoint2f.perspectiveTransform(homographyMatrix: Mat): MatOfPoint2f {
    val transformed = MatOfPoint2f()
    Core.perspectiveTransform(this, transformed, homographyMatrix)
    return transformed
}

fun MatOfPoint2f.toQuadrilateral(): Quadrilateral {
    val points = this.toArray()
    require(points.size >= 4) {
        "MatOfPoint2f have atleast 4 points to make a box"
    }
    return Quadrilateral(points[0], points[1], points[2], points[3])
}

data class Quadrilateral(
    val topLeft: Point,
    val topRight: Point,
    val bottomRight: Point,
    val bottomLeft: Point
)
