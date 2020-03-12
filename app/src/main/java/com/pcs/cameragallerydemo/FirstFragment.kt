package com.pcs.cameragallerydemo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.pcs.cameragallerydemo.camera_utils.CameraUtils
import com.pcs.cameragallerydemo.camera_utils.ImageChooserDialog
import com.pcs.cameragallerydemo.camera_utils.ImageFilePath
import kotlinx.android.synthetic.main.fragment_first.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class FirstFragment : Fragment(R.layout.fragment_first), ImageChooserDialog.ChooserInterface {

    private var TAG: String = "OkHttp"
    private val MEDIA_TYPE_IMAGE = 1
    private val REQUEST_CAMERA = 101
    private val REQUEST_GALLERY = 102
    private var cameraFile: File? = null
    private var galleryFile: File? = null
    private var cameraBitmap: Bitmap? = null
    private var galleryBitmap: Bitmap? = null
    private var cameraImageStoragePath: String = "1"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnPicImage.setOnClickListener {
            checkPermissions()
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    /** Check Permission first */
    private fun checkPermissions() = runWithPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
        showDialog()
    }

    /** Image picker choice dialog ( from camera or gallery )*/
    private fun showDialog() {
        var mDialog: DialogFragment? = null
        mDialog =
            ImageChooserDialog(this@FirstFragment)
        if (!mDialog.isVisible) {
            mDialog.show(
                this@FirstFragment.activity!!.supportFragmentManager,
                "Image chooser dialog"
            )
        }
    }

    /** Interface : Your choice result from image choice picker dialog */
    override fun onOptionClicked(isCamera: Boolean) {
        if (isCamera) cameraIntent()
        else galleryIntent()
    }

    /** Camera intent with file path setup */
    private fun cameraIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraFile = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE)
        if (cameraFile != null) {
            cameraImageStoragePath = cameraFile!!.absolutePath
        }
        Log.i(TAG, "storage path :$cameraImageStoragePath")
        var fileUri: Uri = CameraUtils.getOutputMediaFileUri(requireContext().applicationContext, cameraFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    /** Gallery intent */
    private fun galleryIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_GALLERY)
        }
    }

    /** onActivityResult where result image available */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(TAG, "Request code:$requestCode  Result code: $resultCode  Data: $data")
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CAMERA) {
            Log.i(TAG, "Camera file path : ${cameraFile!!.path}")
            cameraBitmap = CameraUtils.optimizeBitmap(10,cameraFile!!.path)
            imgCamera.setImageBitmap(cameraBitmap!!)
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY && data != null && data.data != null) {
            val uri: Uri = data.data!!
            try {
                galleryBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                var path = ImageFilePath.getPath(this@FirstFragment.activity!!, uri)
                Log.i(TAG, "gallery image path : $path")
                imgGallery.setImageBitmap(galleryBitmap)
                galleryFile = File(ImageFilePath.getPath(this@FirstFragment.activity!!, uri))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(requireActivity(), "Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    /** Encode to base64 String */
    fun encodeToBase64(image: Bitmap): String {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }
}
