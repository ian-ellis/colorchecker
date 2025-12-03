package github.ianellis.colorchecker

import android.view.SurfaceView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import github.ianellis.colorchecker.matdecoration.MatDecorator
import github.ianellis.colorchecker.matdecoration.NoOpMatDecorator
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.core.Mat

class CameraViewManager(
    private val cameraView: CameraBridgeViewBase,
    private val decorator: MatDecorator = NoOpMatDecorator(),
) : DefaultLifecycleObserver {
    init {
        cameraView.setVisibility(SurfaceView.VISIBLE)
        cameraView.setCvCameraViewListener(object : CvCameraViewListener2 {

            override fun onCameraViewStarted(width: Int, height: Int) {}

            override fun onCameraViewStopped() {}

            override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat? {
                return decorator.decorate(inputFrame.rgba())
            }
        })
    }

    fun onPermissionsGranted() {
        cameraView.setCameraPermissionGranted()
    }

    override fun onPause(owner: LifecycleOwner) {
        cameraView.disableView()
    }

    override fun onResume(owner: LifecycleOwner) {
        cameraView.enableView()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        cameraView.disableView()
    }
}
