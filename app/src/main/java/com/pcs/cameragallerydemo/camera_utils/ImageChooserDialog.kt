package com.pcs.cameragallerydemo.camera_utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.pcs.cameragallerydemo.R
import kotlinx.android.synthetic.main.dialog_choose_image.*

class ImageChooserDialog(private val chooserInterface: ChooserInterface) : DialogFragment() {
    private var myInterface: ChooserInterface? = null

    init {
        this.myInterface = chooserInterface
    }

    interface ChooserInterface {
        fun onOptionClicked(isCamera: Boolean)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_choose_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        llCamera.setOnClickListener {
            myInterface!!.onOptionClicked(true)
            dismiss()
        }
        llGallery.setOnClickListener {
            myInterface!!.onOptionClicked(false)
            dismiss()
        }
        llClose.setOnClickListener {
            dismiss()
        }
    }
}