package github.ianellis.colorchecker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import github.ianellis.colorchecker.matdecoration.ObjectDetectionMatDecorator
import github.ianellis.colorchecker.objectdetection.ORBObjectDetector
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils

class MainActivity : ComponentActivity() {

    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE: Int = 200
    }

    private lateinit var cameraManager: CameraViewManager

    private lateinit var grantPermissionsView: View
    private lateinit var grantPermissionsButton: Button
    private lateinit var cameraView: CameraBridgeViewBase
    private var permissionsGranted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!OpenCVLoader.initLocal()) {
            (Toast.makeText(this, "OpenCV initialization failed!", Toast.LENGTH_LONG)).show();
            return;
        }
        initView()
        initCameraManager()
    }

    private fun initView() {
        setContentView(R.layout.cameraview_layout)
        grantPermissionsView = findViewById(R.id.grant_permissions_view)
        grantPermissionsButton = findViewById(R.id.grant_permissions_button)
        grantPermissionsButton.setOnClickListener { openPermissionSettings() }
        cameraView = findViewById<View>(R.id.camera_view) as CameraBridgeViewBase
    }

    private fun initCameraManager() {
        // load target image
        val colorCheckerImage = Utils.loadResource(this, R.raw.reference)
        //create detector
        val objectDetector = ORBObjectDetector(colorCheckerImage)
        // create decorator to draw box when matches found
        val objectDetectionMatDecorator = ObjectDetectionMatDecorator(objectDetector)
        // create camera manager to draw overlays
        cameraManager = CameraViewManager(cameraView, objectDetectionMatDecorator)
        // set lifecycle observer to manage view state
        lifecycle.addObserver(cameraManager)
    }

    override fun onStart() {
        super.onStart()
        grantPermissions()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            onCameraPermissionGranted()
        } else {
            grantPermissionsView.visibility = View.VISIBLE
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
    }

    private fun onCameraPermissionGranted() {
        grantPermissionsView.visibility = View.GONE
        cameraManager.onPermissionsGranted()
    }

    private fun grantPermissions() {
        permissionsGranted =
            checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (!permissionsGranted) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )

        } else {
            onCameraPermissionGranted()
        }
    }

    private fun openPermissionSettings() {
        startActivity(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.fromParts("package", packageName, null)
        })
    }
}
