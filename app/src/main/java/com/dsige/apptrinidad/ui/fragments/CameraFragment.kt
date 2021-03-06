package com.dsige.apptrinidad.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.*
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.*
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.*
import androidx.fragment.app.Fragment
import com.dsige.apptrinidad.R
import com.dsige.apptrinidad.helper.ImageSaver
import com.dsige.apptrinidad.helper.Util
import com.dsige.apptrinidad.ui.activities.PreviewCameraActivity
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.File
import java.lang.Long.signum
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.sqrt

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"
private const val ARG_PARAM5 = "param5"

class CameraFragment : Fragment(), View.OnClickListener, View.OnTouchListener {

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        try {
            val activity = this.activity
            val manager = activity!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val characteristics = manager.getCameraCharacteristics(cameraId)
            val maxzoom =
                (characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM))!! * 10

            val m: Rect? = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
//            val action = event.action
            val currenFingerSpacing: Float

            if (event.pointerCount > 1) {
                // Multi touch logic
                currenFingerSpacing = getFingerSpacing(event)
                if (fingerSpacing != 0.toFloat()) {
                    if (currenFingerSpacing > fingerSpacing && maxzoom > zoomLevel) {
                        zoomLevel++
                    } else if (currenFingerSpacing < fingerSpacing && zoomLevel > 1) {
                        zoomLevel--
                    }
                    if (m != null) {
                        val minW = (m.width() / maxzoom).toInt()
                        val minH = (m.height() / maxzoom).toInt()
                        val difW = m.width() - minW
                        val difH = m.height() - minH
                        var cropW = difW / 100 * zoomLevel
                        var cropH = difH / 100 * zoomLevel
                        cropW -= cropW and 3
                        cropH -= cropH and 3
                        zoom = Rect(cropW, cropH, m.width() - cropW, m.height() - cropH)
                    }
                    previewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoom)
                }
                fingerSpacing = currenFingerSpacing
            }
//            else {
//                if (action == MotionEvent.ACTION_UP) {
//                    //single touch logic
//                }
//            }

            try {
                captureSession!!
                    .setRepeatingRequest(previewRequestBuilder.build(), captureCallback, null)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            } catch (ex: NullPointerException) {
                ex.printStackTrace()
            }
        } catch (e: CameraAccessException) {
            throw RuntimeException("can not access camera.", e)
        }
        return true
    }

    private fun getFingerSpacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.imageViewPicture -> lockFocus()
            R.id.imageViewFlash -> {
                when (flashType) {
                    1 -> {
                        flashType = 2
                        imageViewFlash.setImageResource(R.drawable.ic_flash_on)
                    }
                    2 -> {
                        flashType = 3
                        imageViewFlash.setImageResource(R.drawable.ic_flash_off)
                    }
                    3 -> {
                        flashType = 1
                        imageViewFlash.setImageResource(R.drawable.ic_flash_auto)
                    }
                }
            }
//            R.id.imageViewChange -> switchCamera()
        }
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
            openCamera(width, height)
        }

        override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) {
            configureTransform(width, height)
        }

        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture) = true

        override fun onSurfaceTextureUpdated(texture: SurfaceTexture) = Unit

    }

//    protected var captureSession: CameraCaptureSession? = null
//    protected var previewRequestBuilder: CaptureRequest.Builder? = null

    //Zooming
    private var fingerSpacing: Float = 0.toFloat()
    private var zoomLevel = 1
    private var zoom: Rect? = null

    private var CAMERA_FRONT = "1"

    private var CAMERA_BACK = "0"

    /**
     * ID of the current [CameraDevice].
     */
    private var cameraId: String = CAMERA_BACK

    /**
     * A [CameraCaptureSession] for camera preview.
     */
    private var captureSession: CameraCaptureSession? = null

    /**
     * A reference to the opened [CameraDevice].
     */
    private var cameraDevice: CameraDevice? = null

    /**
     * The [android.util.Size] of camera preview.
     */
    private lateinit var previewSize: Size

    /**
     * [CameraDevice.StateCallback] is called when [CameraDevice] changes its state.
     */
    private val stateCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(cameraDevice: CameraDevice) {
            cameraOpenCloseLock.release()
            this@CameraFragment.cameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            cameraOpenCloseLock.release()
            cameraDevice.close()
            this@CameraFragment.cameraDevice = null
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            onDisconnected(cameraDevice)
            this@CameraFragment.activity?.finish()
        }

    }

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private var backgroundThread: HandlerThread? = null

    /**
     * A [Handler] for running tasks in the background.
     */
    private var backgroundHandler: Handler? = null

    /**
     * An [ImageReader] that handles still image capture.
     */
    private var imageReader: ImageReader? = null

    /**
     * This is the output file for our picture.
     */
    private lateinit var file: File

    /**
     * Flash Type
     */
    private var flashType: Int = 1

    /**
     * This a callback object for the [ImageReader]. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private val onImageAvailableListener = ImageReader.OnImageAvailableListener {
        backgroundHandler?.post(ImageSaver(it.acquireLatestImage(), file))
    }

    /**
     * [CaptureRequest.Builder] for the camera preview
     */
    private lateinit var previewRequestBuilder: CaptureRequest.Builder

    /**
     * [CaptureRequest] generated by [.previewRequestBuilder]
     */
    private lateinit var previewRequest: CaptureRequest

    /**
     * The current state of camera state for taking pictures.
     *
     * @see .captureCallback
     */
    private var state = STATE_PREVIEW

    /**
     * A [Semaphore] to prevent the app from exiting before closing the camera.
     */
    private val cameraOpenCloseLock = Semaphore(1)

    /**
     * Whether the current camera device supports Flash or not.
     */
//    private var flashSupported = false

    /**
     * Orientation of the camera sensor
     */
    private var sensorOrientation = 0

    /**
     * A [CameraCaptureSession.CaptureCallback] that handles events related to JPEG capture.
     */
    private val captureCallback = object : CameraCaptureSession.CaptureCallback() {

        private fun process(result: CaptureResult) {
            when (state) {
                STATE_PREVIEW -> Unit // Do nothing when the camera preview is working normally.
                STATE_WAITING_LOCK -> capturePicture(result)
                STATE_WAITING_PRECAPTURE -> {
                    // CONTROL_AE_STATE can be null on some devices
                    val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                    if (aeState == null ||
                        aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                        aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED
                    ) {
                        state = STATE_WAITING_NON_PRECAPTURE
                    }
                }
                STATE_WAITING_NON_PRECAPTURE -> {
                    // CONTROL_AE_STATE can be null on some devices
                    val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        state = STATE_PICTURE_TAKEN
                        captureStillPicture()
                    }
                }
            }
        }

        private fun capturePicture(result: CaptureResult) {
            val afState = result.get(CaptureResult.CONTROL_AF_STATE)
            if (afState == null) {
                captureStillPicture()
            } else if (afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED
                || afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED
            ) {
                // CONTROL_AE_STATE can be null on some devices
                val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                    state = STATE_PICTURE_TAKEN
                    captureStillPicture()
                } else {
                    runPrecaptureSequence()
                }
            }
        }

        override fun onCaptureProgressed(
            session: CameraCaptureSession, request: CaptureRequest, partialResult: CaptureResult
        ) {
            process(partialResult)
        }

        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            process(result)
        }
    }

    private var tipo: Int = 0
    private var usuarioId: String = ""
    private var nameImg: String = ""
    private var registroId: Int = 0
    private var tipoDetalle: Int = 0
    private var detalleId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tipo = it.getInt(ARG_PARAM1)
            usuarioId = it.getString(ARG_PARAM2)!!
            registroId = it.getInt(ARG_PARAM3)
            tipoDetalle = it.getInt(ARG_PARAM4)
            detalleId = it.getInt(ARG_PARAM5)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_camera, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imageViewPicture.setOnClickListener(this)
        imageViewFlash.setOnClickListener(this)
//        imageViewChange.setOnClickListener(this)
        textureView.setOnTouchListener(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        nameImg = Util.getFechaActualForPhoto(usuarioId)
        file = File(Util.getFolder(context!!), nameImg)
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        if (textureView.isAvailable) {
            openCamera(textureView.width, textureView.height)
        } else {
            textureView.surfaceTextureListener = surfaceTextureListener
        }
    }

    override fun onPause() {
        closeCamera()
        stopBackgroundThread()
        super.onPause()
    }

    /**
     * Sets up member variables related to camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    private fun setUpCameraOutputs(width: Int, height: Int) {
        val manager = activity!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in manager.cameraIdList) {

                val characteristics = manager.getCameraCharacteristics(cameraId)
                // We don't use a front facing camera in this sample.
                val cameraDirection = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (cameraDirection != null &&
                    cameraDirection == CameraCharacteristics.LENS_FACING_FRONT
                ) {
                    continue
                }

                val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    ?: continue

                // For still image captures, we use the largest available size.
                val largest = Collections.max(
                    listOf(*map.getOutputSizes(ImageFormat.JPEG)),
                    CompareSizesByArea()
                )
                imageReader = ImageReader.newInstance(
                    640,
                    480,
                    ImageFormat.JPEG,
                    1
                ).apply {
                    setOnImageAvailableListener(onImageAvailableListener, backgroundHandler)
                }
                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                val displayRotation = activity!!.windowManager.defaultDisplay.rotation

                sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
                val swappedDimensions = areDimensionsSwapped(displayRotation)

                val displaySize = Point()
                activity!!.windowManager.defaultDisplay.getSize(displaySize)
                val rotatedPreviewWidth = if (swappedDimensions) height else width
                val rotatedPreviewHeight = if (swappedDimensions) width else height
                var maxPreviewWidth = if (swappedDimensions) displaySize.y else displaySize.x
                var maxPreviewHeight = if (swappedDimensions) displaySize.x else displaySize.y

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) maxPreviewWidth = MAX_PREVIEW_WIDTH
                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) maxPreviewHeight = MAX_PREVIEW_HEIGHT

                // Danger, W.R.! Attempting to use too large a preview size could exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                previewSize = chooseOptimalSize(
                    map.getOutputSizes(SurfaceTexture::class.java),
                    rotatedPreviewWidth, rotatedPreviewHeight,
                    maxPreviewWidth, maxPreviewHeight,
                    largest
                )

                // We fit the aspect ratio of TextureView to the size of preview we picked.
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    textureView.setAspectRatio(previewSize.width, previewSize.height)
                } else {
                    textureView.setAspectRatio(previewSize.height, previewSize.width)
                }

                // Check if the flash is supported.
//                flashSupported =
//                    characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true

                this.cameraId = cameraId

                // We've found a viable camera and finished setting up member variables,
                // so we don't need to iterate through other available cameras.
                return
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        } catch (e: NullPointerException) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            Util.toastMensaje(context!!, "CAMERA ERROR")
//            ErrorDialog.newInstance(getString(R.string.camera_error))
//                .show(childFragmentManager, FRAGMENT_DIALOG)
        }

    }

    /**
     * Determines if the dimensions are swapped given the phone's current rotation.
     *
     * @param displayRotation The current rotation of the display
     *
     * @return true if the dimensions are swapped, false otherwise.
     */
    private fun areDimensionsSwapped(displayRotation: Int): Boolean {
        var swappedDimensions = false
        when (displayRotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> {
                if (sensorOrientation == 90 || sensorOrientation == 270) {
                    swappedDimensions = true
                }
            }
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                if (sensorOrientation == 0 || sensorOrientation == 180) {
                    swappedDimensions = true
                }
            }
            else -> {
                Log.e(TAG, "Display rotation is invalid: $displayRotation")
            }
        }
        return swappedDimensions
    }


    @SuppressLint("MissingPermission")
    private fun openCamera(width: Int, height: Int) {
        setUpCameraOutputs(width, height)
        configureTransform(width, height)
        val manager = activity!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            // Wait for camera to open - 2.5 seconds is sufficient
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }
            manager.openCamera(cameraId, stateCallback, backgroundHandler)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera opening.", e)
        }
    }

    /**
     * Closes the current [CameraDevice].
     */
    private fun closeCamera() {
        try {
            cameraOpenCloseLock.acquire()
            captureSession?.close()
            captureSession = null
            cameraDevice?.close()
            cameraDevice = null
            imageReader?.close()
            imageReader = null
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {
            cameraOpenCloseLock.release()
        }
    }

    /**
     * Starts a background thread and its [Handler].
     */
    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground").also { it.start() }
        backgroundHandler = Handler(backgroundThread!!.looper)
    }

    /**
     * Stops the background thread and its [Handler].
     */
    private fun stopBackgroundThread() {
        backgroundThread?.quitSafely()
        try {
            backgroundThread?.join()
            backgroundThread = null
            backgroundHandler = null
        } catch (e: InterruptedException) {
            Log.e(TAG, e.toString())
        }

    }

    /**
     * Creates a new [CameraCaptureSession] for camera preview.
     */
    private fun createCameraPreviewSession() {
        try {
            val texture = textureView.surfaceTexture

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(previewSize.width, previewSize.height)

            // This is the output Surface we need to start preview.
            val surface = Surface(texture)

            // We set up a CaptureRequest.Builder with the output Surface.
            previewRequestBuilder = cameraDevice!!.createCaptureRequest(
                CameraDevice.TEMPLATE_PREVIEW
            )
            previewRequestBuilder.addTarget(surface)

            // Here, we create a CameraCaptureSession for camera preview.
            cameraDevice?.createCaptureSession(
                listOf(surface, imageReader?.surface),
                object : CameraCaptureSession.StateCallback() {

                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        // The camera is already closed
                        if (cameraDevice == null) return

                        // When the session is ready, we start displaying the preview.
                        captureSession = cameraCaptureSession
                        try {
                            // Auto focus should be continuous for camera preview.
                            previewRequestBuilder.set(
                                CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                            )
                            // Flash is automatically enabled when necessary.
                            setAutoFlash(previewRequestBuilder)

                            // Finally, we start displaying the camera preview.
                            previewRequest = previewRequestBuilder.build()
                            captureSession?.setRepeatingRequest(
                                previewRequest,
                                captureCallback, backgroundHandler
                            )
                        } catch (e: CameraAccessException) {
                            Log.e(TAG, e.toString())
                        }

                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Util.toastMensaje(context!!, "Failed")
                    }
                }, null
            )
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }

    }

    /**
     * Configures the necessary [android.graphics.Matrix] transformation to `textureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `textureView` is fixed.
     *
     * @param viewWidth  The width of `textureView`
     * @param viewHeight The height of `textureView`
     */
    private fun configureTransform(viewWidth: Int, viewHeight: Int) {
        activity ?: return
        val rotation = activity!!.windowManager.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(0f, 0f, previewSize.height.toFloat(), previewSize.width.toFloat())
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            val scale =
                (viewHeight.toFloat() / previewSize.height).coerceAtLeast(viewWidth.toFloat() / previewSize.width)
            with(matrix) {
                setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
                postScale(scale, scale, centerX, centerY)
                postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
            }
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180f, centerX, centerY)
        }
        textureView.setTransform(matrix)
    }

    /**
     * Lock the focus as the first step for a still image capture.
     */
    private fun lockFocus() {
        try {
            // This is how to tell the camera to lock focus.
            previewRequestBuilder.set(
                CaptureRequest.CONTROL_AF_TRIGGER,
                CameraMetadata.CONTROL_AF_TRIGGER_START
            )
            // Tell #captureCallback to wait for the lock.
            state = STATE_WAITING_LOCK

            captureSession?.capture(
                previewRequestBuilder.build(), captureCallback,
                backgroundHandler
            )
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }
    }

    /**
     * Run the precapture sequence for capturing a still image. This method should be called when
     * we get a response in [.captureCallback] from [.lockFocus].
     */
    private fun runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            previewRequestBuilder.set(
                CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START
            )
            // Tell #captureCallback to wait for the precapture sequence to be set.
            state = STATE_WAITING_PRECAPTURE

            captureSession?.capture(
                previewRequestBuilder.build(), captureCallback,
                backgroundHandler
            )
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     * [.captureCallback] from both [.lockFocus].
     */
    private fun captureStillPicture() {
        try {
            if (activity == null || cameraDevice == null) return
            val rotation = activity!!.windowManager.defaultDisplay.rotation


            // This is the CaptureRequest.Builder that we use to take a picture.
            val captureBuilder = cameraDevice?.createCaptureRequest(
                CameraDevice.TEMPLATE_STILL_CAPTURE
            )?.apply {
                addTarget(imageReader!!.surface)

                // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
                // We have to take that into account and rotate JPEG properly.
                // For devices with orientation of 90, we return our mapping from ORIENTATIONS.
                // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
                if (zoom != null) {
                    set(CaptureRequest.SCALER_CROP_REGION, zoom)
                }

                set(
                    CaptureRequest.JPEG_ORIENTATION,
                    (ORIENTATIONS.get(rotation) + sensorOrientation + 270) % 360
                )

                // Use the same AE and AF modes as the preview.
                set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                )
            }?.also { setAutoFlash(it) }

            val captureCallback = object : CameraCaptureSession.CaptureCallback() {

                override fun onCaptureCompleted(
                    s: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult
                ) {
                    unlockFocus()
                    Handler().postDelayed({
                        if (tipo == 0) {
                            startActivityForResult(
                                Intent(context!!, PreviewCameraActivity::class.java)
                                    .putExtra("nameImg", nameImg)
                                    .putExtra("tipo", tipo)
                                    .putExtra("usuarioId", usuarioId)
                                    .putExtra("id", registroId)
                                    .putExtra("detalleId", detalleId)
                                    .putExtra("tipoDetalle", tipoDetalle)
                                    .putExtra("galery", false), 1
                            )
                        } else {
                            startActivity(
                                Intent(context!!, PreviewCameraActivity::class.java)
                                    .putExtra("nameImg", nameImg)
                                    .putExtra("tipo", tipo)
                                    .putExtra("usuarioId", usuarioId)
                                    .putExtra("id", registroId)
                                    .putExtra("detalleId", detalleId)
                                    .putExtra("tipoDetalle", tipoDetalle)
                                    .putExtra("galery", false)
                            )
                            activity!!.finish()
                        }
                    }, 200)
                }
            }

            captureSession?.apply {
                stopRepeating()
//                abortCaptures()
                capture(captureBuilder!!.build(), captureCallback, null)
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }

    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private fun unlockFocus() {
        try {
            // Reset the auto-focus trigger
            previewRequestBuilder.set(
                CaptureRequest.CONTROL_AF_TRIGGER,
                CameraMetadata.CONTROL_AF_TRIGGER_CANCEL
            )
            setAutoFlash(previewRequestBuilder)
            captureSession?.capture(
                previewRequestBuilder.build(), captureCallback,
                backgroundHandler
            )
            // After this, the camera will go back to the normal state of preview.
            state = STATE_PREVIEW
            captureSession?.setRepeatingRequest(
                previewRequest, captureCallback,
                backgroundHandler
            )
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }
    }

    private fun setAutoFlash(requestBuilder: CaptureRequest.Builder) {
//        if (flashSupported) {
//            requestBuilder.set(
//                CaptureRequest.CONTROL_AE_MODE,
//                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
//            )
//        }

        when (flashType) {
            1 -> {
                requestBuilder.set(
                    CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
                )
            }
            2 -> {
                requestBuilder.set(
                    CaptureRequest.CONTROL_AE_MODE,
                    CameraMetadata.CONTROL_AE_MODE_ON_ALWAYS_FLASH
                )
            }
            3 -> {
                requestBuilder.set(
                    CaptureRequest.CONTROL_AE_MODE,
                    CameraMetadata.CONTROL_AE_MODE_ON
                )
            }
        }
    }

    companion object {
        /**
         * Conversion from screen rotation to JPEG orientation.
         */
        private val ORIENTATIONS = SparseIntArray()
        private val FRAGMENT_DIALOG = "dialog"

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }

        /**
         * Tag for the [Log].
         */
        private val TAG = "Camera2BasicFragment"

        /**
         * Camera state: Showing camera preview.
         */
        private val STATE_PREVIEW = 0

        /**
         * Camera state: Waiting for the focus to be locked.
         */
        private val STATE_WAITING_LOCK = 1

        /**
         * Camera state: Waiting for the exposure to be precapture state.
         */
        private val STATE_WAITING_PRECAPTURE = 2

        /**
         * Camera state: Waiting for the exposure state to be something other than precapture.
         */
        private val STATE_WAITING_NON_PRECAPTURE = 3

        /**
         * Camera state: Picture was taken.
         */
        private val STATE_PICTURE_TAKEN = 4

        /**
         * Max preview width that is guaranteed by Camera2 API
         */
        private val MAX_PREVIEW_WIDTH = 1640 //1920

        /**
         * Max preview height that is guaranteed by Camera2 API
         */
        private val MAX_PREVIEW_HEIGHT = 1080 //1180 //1080

        /**
         * Given `choices` of `Size`s supported by a camera, choose the smallest one that
         * is at least as large as the respective texture view size, and that is at most as large as
         * the respective max size, and whose aspect ratio matches with the specified value. If such
         * size doesn't exist, choose the largest one that is at most as large as the respective max
         * size, and whose aspect ratio matches with the specified value.
         *
         * @param choices           The list of sizes that the camera supports for the intended
         *                          output class
         * @param textureViewWidth  The width of the texture view relative to sensor coordinate
         * @param textureViewHeight The height of the texture view relative to sensor coordinate
         * @param maxWidth          The maximum width that can be chosen
         * @param maxHeight         The maximum height that can be chosen
         * @param aspectRatio       The aspect ratio
         * @return The optimal `Size`, or an arbitrary one if none were big enough
         */
        @JvmStatic
        private fun chooseOptimalSize(
            choices: Array<Size>,
            textureViewWidth: Int,
            textureViewHeight: Int,
            maxWidth: Int,
            maxHeight: Int,
            aspectRatio: Size
        ): Size {
            // Collect the supported resolutions that are at least as big as the preview Surface
            val bigEnough = ArrayList<Size>()
            // Collect the supported resolutions that are smaller than the preview Surface
            val notBigEnough = ArrayList<Size>()
            val w = aspectRatio.width
            val h = aspectRatio.height
            for (option in choices) {
                if (option.width <= maxWidth && option.height <= maxHeight &&
                    option.height == option.width * h / w
                ) {
                    if (option.width >= textureViewWidth && option.height >= textureViewHeight) {
                        bigEnough.add(option)
                    } else {
                        notBigEnough.add(option)
                    }
                }
            }

            // Pick the smallest of those big enough. If there is no one big enough, pick the
            // largest of those not big enough.
            return when {
                bigEnough.size > 0 -> Collections.min(bigEnough, CompareSizesByArea())
                notBigEnough.size > 0 -> Collections.max(notBigEnough, CompareSizesByArea())
                else -> {
                    Log.e(TAG, "Couldn't find any suitable preview size")
                    choices[0]
                }
            }
        }

        @JvmStatic
        fun newInstance(param1: Int, param2: String, param3: Int, param4: Int, param5: Int) =
            CameraFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putInt(ARG_PARAM3, param3)
                    putInt(ARG_PARAM4, param4)
                    putInt(ARG_PARAM5, param5)
                }
            }
    }

    internal class CompareSizesByArea : Comparator<Size> {
        // We cast here to ensure the multiplications won't overflow
        override fun compare(lhs: Size, rhs: Size) =
            signum(lhs.width.toLong() * lhs.height - rhs.width.toLong() * rhs.height)

    }

    private fun switchCamera() {
        if (cameraId == CAMERA_FRONT) {
            cameraId = CAMERA_BACK
            closeCamera()
            reopenCamera()
        } else if (cameraId == CAMERA_BACK) {
            cameraId = CAMERA_FRONT
            closeCamera()
            reopenCamera()
        }
    }

    private fun reopenCamera() {
        if (textureView.isAvailable) {
            openCamera(textureView.width, textureView.height)
        } else {
            textureView.surfaceTextureListener = surfaceTextureListener
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getStringExtra("img")!!
                if (result.isNotEmpty()) {
                    activity!!.setResult(Activity.RESULT_OK, Intent().putExtra("img", result))
                    activity!!.finish()
                }
            }
        }
    }
}