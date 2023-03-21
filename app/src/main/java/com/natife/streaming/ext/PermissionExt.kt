package com.natife.streaming.ext

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

fun Context.withAllPermissions(vararg permissions: String, granted: () -> Unit) {
    Dexter.withContext(this)
        .withPermissions(permissions.toList())
        .withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                report?.areAllPermissionsGranted()?.also {
                    if (it) { granted() }
                }
            }

            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                token?.continuePermissionRequest()
            }
        })
        .check()
}
