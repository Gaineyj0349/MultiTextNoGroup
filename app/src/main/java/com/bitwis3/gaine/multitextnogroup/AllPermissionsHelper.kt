package com.bitwis3.gaine.multitextnogroup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast

class AllPermissionsHelper(var permissionsDirective: PermissionsDirective) : AppCompatActivity() {

    /*
    FROM THE PERMISSION CALLING ACTIVITY
    1 - Make 2 methods - one you want to be called if permission is denied and another if permission is granted
    2 - Create inner class that implements PermissionsDirective, fill in all implemented members (you will link the methods from step 1 in this inner class's implemented members)
    3 - Create a class-wide helper variable of type AllPermissionsHelper, pass that inner class from step 2 to the constructor
    4 - After onCreate method - Override the onRequestPermissionsResult and replace super. with helper.handleResult (the super method is called from within the handleResult)
    5 - Finally, the helper's requestPermissions method will start the permission asking
     */

     var activity: Activity
     var permission: Array<String?>
     var requestCode: Int = 0

    init {
        this.activity = permissionsDirective.activity
        this.permission = permissionsDirective.permissionsToRequest()
        this.requestCode = permissionsDirective.requestCode
    }

    // all permissions are asked for
    fun requestPermissions(){
        for (i in permission.indices) {
            if (ContextCompat.checkSelfPermission(activity, permission[i]!!) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, permission, requestCode)
            }
        }
    }

     //this is where the results are handled - actions are based upon the passed in PermissionsDirective object

    fun handleResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        var allGranted = true
        Log.i("JOSHperm", "handle started")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (i in permissions.indices) {
            if (grantResults.size > 0 && ActivityCompat.checkSelfPermission(activity,
                            permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false
                break;
                Log.i("JOSHperm", "past break")
            }
        }
        if (allGranted) {
            Log.i("JOSHperm", "helper1")
            // all permissions are granted
            permissionsDirective.executeOnPermissionGranted()
        } else {
            Log.i("JOSHperm", "helper2")
            // if one was denied this will be called
            permissionsDirective.executeOnPermissionDenied()

        }
    }

      fun sendToSettings(context: Context) {
         val intent = Intent()
         intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
         val uri = Uri.fromParts("package", context.packageName, null)
         intent.data = uri
         context.startActivity(intent)
         Toast.makeText(context, "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show()
    }

    fun needPermissions(permission: Array<String?>): Boolean {
        var need = false
        for (i in permission.indices) {
            if (ContextCompat.checkSelfPermission(permissionsDirective.activity, permission[i]!!) != PackageManager.PERMISSION_GRANTED) {
                need = true
            }
        }
        return need
    }
}

interface PermissionsDirective{
    fun permissionsToRequest(): Array<String?>
    val requestCode: Int
    val activity: Activity
    fun executeOnPermissionGranted()
    fun executeOnPermissionDenied()

}