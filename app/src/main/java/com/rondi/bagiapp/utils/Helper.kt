package com.rondi.bagiapp.utils

import android.content.Context
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.rondi.bagiapp.R

fun ImageView.setImageFromUrl(url: String) {
    Glide
        .with(context)
        .load(url)
        .centerCrop()
        .into(this)
}

fun Boolean?.isTrue() = this != null && this

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}


fun Context.showOKDialog(title: String, message: String) {
    AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton("OK"){ p0, _ ->
            p0.dismiss()
        }
    }.create().show()
}

fun Fragment.showOKDialog(title: String, message: String) {
    AlertDialog.Builder(requireContext()).apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton("OK"){ p0, _ ->
            p0.dismiss()
        }
    }.create().show()
}

fun Fragment.showConfirmationDialog(title: String, message: String, onYesClicked: () -> Unit) {
    AlertDialog.Builder(requireContext()).apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton(getString(R.string.action_logout)) { _, _ ->
            onYesClicked.invoke()
        }
        setNegativeButton(getString(R.string.action_cancel)) { dialog, _ ->
            dialog.dismiss()
        }
    }.create().show()
}
