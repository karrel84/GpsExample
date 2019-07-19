package com.example.gpsexample

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker.PermissionResult

object PermissionChecker {
    /**
     * first : requestCode
     * second : permission array
     */
    private val checkPermissionList = arrayListOf<PermissionItem>()

    fun init() {
        checkPermissionList.clear()
    }

    fun release() {
        checkPermissionList.clear()
    }

    @PermissionResult
    fun isGrantedPermission(activity: Activity, permission: String): Boolean {
        val permissionStatus = ContextCompat.checkSelfPermission(activity, permission)
        return permissionStatus == PackageManager.PERMISSION_GRANTED
    }

    fun checkPermission(activity: Activity, permission: String, requestCode: Int, onGranted: (() -> Unit)? = null, onDenied: (() -> Unit)? = null) {
        if (isGrantedPermission(activity, permission)) {
            onGranted?.invoke()
        } else {
            PermissionItem(requestCode, permission, onGranted, onDenied)
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                // 한번 거부당했던 상태
                requestPermission(activity, permission, requestCode, onGranted, onDenied)
            } else {
                requestPermission(activity, permission, requestCode, onGranted, onDenied)
            }
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val item = checkPermissionList.find { it.requestCode == requestCode }
        checkPermissionList.remove(item)

        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            item?.onGranted?.invoke()
        } else {
            item?.onDenied?.invoke()
        }
    }

    private fun requestPermission(activity: Activity, permission: String, requestCode: Int, onGranted: (() -> Unit)?, onDenied: (() -> Unit)?) {
        val item = PermissionItem(requestCode, permission, onGranted, onDenied)
        checkPermissionList.add(item)

        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }

    data class PermissionItem(
            val requestCode: Int,
            val permission: String,
            val onGranted: (() -> Unit)?,
            val onDenied: (() -> Unit)?
    )
}