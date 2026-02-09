package com.ml.shubham0204.facenet_android.presentation.screens.add_face.caputre

import android.graphics.Bitmap
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ml.shubham0204.facenet_android.data.RecognitionMetrics
import com.ml.shubham0204.facenet_android.data.SettingsStore
import com.ml.shubham0204.facenet_android.domain.AppException
import com.ml.shubham0204.facenet_android.domain.ImageVectorUseCase
import com.ml.shubham0204.facenet_android.domain.PersonUseCase
import com.ml.shubham0204.facenet_android.presentation.components.setProgressDialogText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class RegisterFaceScreenViewModel(
    val personUseCase: PersonUseCase,
    val imageVectorUseCase: ImageVectorUseCase,
    val settingsStore: SettingsStore
) : ViewModel() {
    private val KEY_SETTINGS_CAMERA_FACING = "camera_facing"
    private val CAMERA_FACING_VALUE_BACK = "back"
    private val CAMERA_FACING_VALUE_FRONT = "front"

    val faceDetectionMetricsState = mutableStateOf<RecognitionMetrics?>(null)
    val cameraFacing = mutableIntStateOf(getCameraFacing())

    val personNameState: MutableState<String> = mutableStateOf("")
    val selectedImageURIs: MutableState<List<Uri>> = mutableStateOf(emptyList())

    val isProcessingImages: MutableState<Boolean> = mutableStateOf(false)
    val numImagesProcessed: MutableState<Int> = mutableIntStateOf(0)

    // get list of bitmap from camera and proceed to add to the database with person name
    fun addImages(bitmapList: List<Bitmap>) {
        isProcessingImages.value = true
        CoroutineScope(Dispatchers.Default).launch {
            val id =
                personUseCase.addPerson(
                    personNameState.value,
                    selectedImageURIs.value.size.toLong(),
                )

            bitmapList.forEach { bitmap ->
                imageVectorUseCase
                    .addImage(id, personNameState.value, bitmap)
                    .onFailure {
                        val errorMessage = (it as AppException).errorCode.message
                        setProgressDialogText(errorMessage)
                    }.onSuccess {
                        numImagesProcessed.value += 1
                        setProgressDialogText("Processed ${numImagesProcessed.value} image(s)")
                    }
            }

            isProcessingImages.value = false
        }
    }

    fun getNumPeople(): Long = personUseCase.getCount()

    private fun getCameraFacing(): Int {
        val cameraFacing = settingsStore.get(KEY_SETTINGS_CAMERA_FACING)
        return if (cameraFacing == CAMERA_FACING_VALUE_FRONT) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
    }

    private fun saveCameraFacingSetting(cameraFacing: Int) {
        settingsStore.save(
            KEY_SETTINGS_CAMERA_FACING,
            if (cameraFacing == CameraSelector.LENS_FACING_FRONT) {
                CAMERA_FACING_VALUE_FRONT
            } else {
                CAMERA_FACING_VALUE_BACK
            }
        )
    }

    fun changeCameraFacing() {
        if (cameraFacing.intValue == CameraSelector.LENS_FACING_FRONT) {
            cameraFacing.intValue = CameraSelector.LENS_FACING_BACK
        } else {
            cameraFacing.intValue = CameraSelector.LENS_FACING_FRONT
        }
        saveCameraFacingSetting(cameraFacing.intValue)
    }
}
