package github.ianellis.colorchecker.matdecoration

import org.opencv.core.Mat

class NoOpMatDecorator : MatDecorator {
    override fun decorate(scene: Mat): Mat {
        return scene
    }
}
