package com.carto.advanced.kotlin.sections.base

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.Toast
import com.carto.advanced.kotlin.main.MainActivity
import android.support.v4.app.ActivityCompat


/**
 * Created by aareundo on 03/07/2017.
 */

open class BaseActivity : AppCompatActivity() {

    val instance: BaseActivity
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = intent.getStringExtra(MainActivity.TITLE)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun requestPermission(permission: String) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
    }

    fun requestPermissions(permissions: Array<String>) {
        ActivityCompat.requestPermissions(this, permissions, 1)
    }

    open fun onPermissionsGranted(granted: Boolean) {

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionsGranted(true)
                } else {
                    onPermissionsGranted(false)
                }
                return
            }
        }
    }

    fun alert(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}