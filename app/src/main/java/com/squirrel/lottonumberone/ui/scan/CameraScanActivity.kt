package com.squirrel.lottonumberone.ui.scan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.squirrel.lottonumberone.databinding.ActivityCameraScanBinding
import com.squirrel.lottonumberone.ui.dialog.ScanResultDialog
import com.squirrel.lottonumberone.utils.LottoNumberParser
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraScanActivity : AppCompatActivity(), ScanResultDialog.Listener {

    private lateinit var binding: ActivityCameraScanBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var isProcessing = false

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startCamera()
        } else {
            Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.btnClose.setOnClickListener { finish() }
        binding.btnCapture.setOnClickListener { captureAndScan() }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.previewView.surfaceProvider
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Toast.makeText(this, "카메라를 시작할 수 없습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureAndScan() {
        if (isProcessing) return

        val capture = imageCapture ?: return
        isProcessing = true
        binding.progressBar.visibility = View.VISIBLE
        binding.btnCapture.isEnabled = false

        capture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    val mediaImage = imageProxy.image
                    if (mediaImage == null) {
                        onScanFailed("이미지를 불러올 수 없습니다.")
                        imageProxy.close()
                        return
                    }

                    val inputImage = InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy.imageInfo.rotationDegrees
                    )

                    textRecognizer.process(inputImage)
                        .addOnSuccessListener { result ->
                            val numbers = LottoNumberParser.parse(result.text)
                            if (numbers != null) {
                                showResultDialog(numbers)
                            } else {
                                onScanFailed("번호 6개를 인식하지 못했습니다.\n다시 촬영해 주세요.")
                            }
                        }
                        .addOnFailureListener {
                            onScanFailed("텍스트 인식에 실패했습니다.")
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                            resetCaptureState()
                        }
                }

                override fun onError(exception: ImageCaptureException) {
                    onScanFailed("촬영에 실패했습니다.")
                    resetCaptureState()
                }
            }
        )
    }

    private fun showResultDialog(numbers: List<Int>) {
        ScanResultDialog.newInstance(numbers)
            .show(supportFragmentManager, ScanResultDialog::class.java.simpleName)
    }

    private fun onScanFailed(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        resetCaptureState()
    }

    private fun resetCaptureState() {
        isProcessing = false
        binding.progressBar.visibility = View.GONE
        binding.btnCapture.isEnabled = true
    }

    override fun onScanConfirm(numbers: List<Int>) {
        setResult(
            RESULT_OK,
            Intent().putIntegerArrayListExtra(EXTRA_NUMBERS, ArrayList(numbers))
        )
        finish()
    }

    override fun onScanRetry() {
        // 카메라 화면 유지
    }

    override fun onDestroy() {
        super.onDestroy()
        textRecognizer.close()
        cameraExecutor.shutdown()
    }

    companion object {
        const val EXTRA_NUMBERS = "extra_numbers"
    }
}
