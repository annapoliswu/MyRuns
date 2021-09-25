package com.zw.myruns

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class PickPhotoDialog : DialogFragment(){
    private var dialogOptions = arrayOf("Open Camera", "Select from Gallery")

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Pick Profile Picture")
                .setItems(dialogOptions) { dialog, which ->
                    when (which) {
                        0 -> {
                            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        }
                        1 -> Log.d("resp", dialogOptions[1])
                    }
                }
            builder.create()

        } ?: throw IllegalStateException("Activity null!")

    }
}