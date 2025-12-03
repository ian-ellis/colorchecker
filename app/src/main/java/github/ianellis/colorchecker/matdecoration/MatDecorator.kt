package github.ianellis.colorchecker.matdecoration

import org.opencv.core.Mat

interface MatDecorator {
    fun decorate(scene: Mat): Mat
}
