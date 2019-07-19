package com.example.gpsexample

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST = 1001
    }


    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initLocManager()

        getLocation()
        setupButtonEvents()
    }

    private fun setupButtonEvents() {
        location.setOnClickListener {
            val permission = Manifest.permission.ACCESS_FINE_LOCATION
            PermissionChecker.checkPermission(this, permission, PERMISSION_REQUEST, onGranted = {
                val loc = getLocation()
                println("MainActivity > location clicked > onGranted : $loc")
            }, onDenied = {
                println("MainActivity > location clicked > onDenied")
            })
        }

        locale.setOnClickListener {
            println("MainActivity > clicked locale")
            println("MainActivity > locale : ${getLocale(this)}")
        }
    }

    private fun initLocManager() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(): Location? {
        if (PermissionChecker.isGrantedPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }

        return null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        PermissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun getLocale(context: Context): List<String> {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val languages = hashSetOf<String>()
        val methodInfos = imm.enabledInputMethodList
        for (methodInfo in methodInfos) {
            val subtypes = imm.getEnabledInputMethodSubtypeList(methodInfo, true)
            for (subtype in subtypes) {
                var language: String
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    language = subtype.languageTag
                    if (TextUtils.isEmpty(language)) {
                        language = subtype.locale
                    }
                } else {
                    language = subtype.locale
                }

                if (!TextUtils.isEmpty(language)) {
                    languages.add(language)
                }
            }
        }
        return languages.toList()
    }
}
