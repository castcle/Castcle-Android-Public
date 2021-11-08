package com.castcle.ui.webview

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapted from https://github.com/mgks/Os-FileUp/blob/master/app/src/main/java/mgks/os/fileup/MainActivity.java
 */
class CustomWebChromeClient(
    private val fragment: Fragment,
    private val doOnProgressChanged: ((WebView, Int) -> Unit),
    private val doOnError: ((Throwable) -> Unit)

) : WebChromeClient() {
    private val activity = fragment.activity
    private var fileData: ValueCallback<Uri?>? = null
    private var filePath: ValueCallback<Array<Uri>>? = null
    private var camFileData: String? = null

    private fun checkFilePermission(): Boolean {
        return activity?.let {
            if (
                Build.VERSION.SDK_INT > Build.VERSION_CODES.M &&
                (
                    ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(
                            it,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    )
            ) {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ),
                    FILE_PERMISSION_REQUEST_CODE
                )
                false
            } else {
                true
            }
        } == true
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SimpleDateFormat")
    private fun createFile(prefix: String, extension: String): File {
        val dateTime = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "${prefix}$dateTime$extension"
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File(dir, fileName)
    }

    private fun createImageFile(): File {
        return createFile("img_", ".jpeg")
    }

    private fun createVideoFile(): File {
        return createFile("video_", ".3gp")
    }

    private fun createTakePictureIntent(): Intent? {
        return activity?.let {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(it.packageManager) != null) {
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                    intent.putExtra(EXTRA_PHOTO_PATH, camFileData)
                } catch (e: IOException) {
                    doOnError(e)
                }
                if (photoFile != null) {
                    camFileData = "file:${photoFile.absolutePath}"
                    intent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile)
                    )
                } else {
                    camFileData = null
                    return null
                }
            }
            intent
        }
    }

    private fun createTakeVideoIntent(): Intent? {
        return activity?.let {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            if (intent.resolveActivity(it.packageManager) != null) {
                var videoFile: File? = null
                try {
                    videoFile = createVideoFile()
                } catch (e: IOException) {
                    doOnError(e)
                }
                if (videoFile != null) {
                    camFileData = "file:${videoFile.absolutePath}"
                    intent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(videoFile)
                    )
                } else {
                    camFileData = null
                    return null
                }
            }
            intent
        }
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams
    ): Boolean {
        return if (checkFilePermission() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            filePath = filePathCallback
            var takePictureIntent: Intent? = null
            var takeVideoIntent: Intent? = null
            var includeVideo = false
            var includePhoto = false

            paramCheck@ for (acceptTypes in fileChooserParams.acceptTypes) {
                val splitTypes = acceptTypes.split(", ?+".toRegex()).toTypedArray()
                for (acceptType in splitTypes) {
                    when (acceptType) {
                        "*/*" -> {
                            includePhoto = true
                            includeVideo = true
                            break@paramCheck
                        }
                        "image/*" -> includePhoto = true
                        "video/*" -> includeVideo = true
                    }
                }
            }
            if (fileChooserParams.acceptTypes.isEmpty()) {
                includePhoto = true
                includeVideo = true
            }
            if (includePhoto) {
                takePictureIntent = createTakePictureIntent()
            }
            if (includeVideo) {
                takeVideoIntent = createTakeVideoIntent()
            }

            val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
            contentSelectionIntent.type = FILE_TYPE
            contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, MULTIPLE_FILES)

            val intentArray: Array<Intent?> =
                if (takePictureIntent != null && takeVideoIntent != null) {
                    arrayOf(takePictureIntent, takeVideoIntent)
                } else takePictureIntent?.let { arrayOf(it) }
                    ?: (takeVideoIntent?.let { arrayOf(it) } ?: arrayOfNulls(0))

            val chooserIntent = Intent(Intent.ACTION_CHOOSER)
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
            fragment.startActivityForResult(chooserIntent, FILE_PERMISSION_REQUEST_CODE)
            true
        } else {
            false
        }
    }

    fun doOnActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            var results: Array<Uri?>? = null

            if (resultCode == Activity.RESULT_CANCELED) {
                filePath?.onReceiveValue(null)
                return
            }

            if (resultCode == Activity.RESULT_OK) {
                if (filePath == null) {
                    return
                }
                val clipData: ClipData? = intent?.clipData
                var stringData: String? = intent?.dataString

                if (clipData == null && stringData == null && camFileData != null) {
                    results = arrayOf(Uri.parse(camFileData))
                } else {
                    if (clipData != null) {
                        val numSelectedFiles = clipData.itemCount
                        results = arrayOfNulls(numSelectedFiles)
                        for (i in 0 until clipData.itemCount) {
                            results[i] = clipData.getItemAt(i).uri
                        }
                    } else {
                        intent?.let {
                            it.extras?.let { extras ->
                                val camPhoto = extras["data"] as Bitmap?
                                val bytes = ByteArrayOutputStream()
                                camPhoto?.compress(
                                    Bitmap.CompressFormat.JPEG,
                                    JPEG_IMAGE_QUALITY,
                                    bytes
                                )
                                stringData = MediaStore.Images.Media.insertImage(
                                    activity?.contentResolver,
                                    camPhoto,
                                    null,
                                    null
                                )
                            }
                        }
                        results = arrayOf(Uri.parse(stringData))
                    }
                }
            }
            val noNullResults = results?.filterNotNull()?.toTypedArray()
            filePath?.onReceiveValue(noNullResults)
            filePath = null
        } else {
            if (requestCode == FILE_PERMISSION_REQUEST_CODE) {
                if (null == fileData) {
                    return
                }
                val result = if (intent == null || resultCode != Activity.RESULT_OK) {
                    null
                } else {
                    intent.data
                }
                fileData?.onReceiveValue(result)
                fileData = null
            }
        }
    }

    override fun onProgressChanged(
        view: WebView,
        newProgress: Int
    ) {
        super.onProgressChanged(view, newProgress)
        doOnProgressChanged(view, newProgress)
    }
}

const val FILE_PERMISSION_REQUEST_CODE = 1
private const val FILE_TYPE = "*/*"
private const val MULTIPLE_FILES = true
private const val JPEG_IMAGE_QUALITY = 100
const val EXTRA_PHOTO_PATH = "PhotoPath"
