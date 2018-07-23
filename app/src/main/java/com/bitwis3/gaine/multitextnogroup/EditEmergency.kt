package com.bitwis3.gaine.multitextnogroup

import android.app.Activity
import android.arch.persistence.room.Room
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bitwis3.gaine.multitextnogroup.*
import kotlinx.android.synthetic.main.activity_edit_emergency.*
import kotlinx.android.synthetic.main.content_edit_emergency.*
import org.michaelbel.bottomsheet.BottomSheet
import org.michaelbel.bottomsheet.BottomSheetCallback
import spencerstudios.com.bungeelib.Bungee
import spencerstudios.com.fab_toast.FabToast

class EditEmergency : AppCompatActivity(){





    /*
    name will be used for label
    type entry (string) will be edit_emergency
    type (int) will be the num 1 2 or 3
    code1 will be used for location setting 0 off, 1 on
    code2 will be image number from levels list
     */

    lateinit var db: DBRoom
    var contact: Contact? = null
    var num = 0;
    lateinit var helper2: AllPermissionsHelper
    lateinit var list: MutableList<String>
    lateinit var adapterForSpinner: ArrayAdapter<String>
    lateinit var context: Context
    var imageNum = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_emergency)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Preset Text Set-Up"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        context = this
        fab.setOnClickListener { view ->
            showDialogForConfirm()
        }
        helper2 = AllPermissionsHelper(PermissionsHelper2())
        editiv.setOnClickListener{showSheet()}
        db = Room.databaseBuilder(applicationContext,DBRoom::class.java, "_database_multi_master")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build()

        //get intent num to fetch data from db and setup
        num = intent.getIntExtra("which", 0)

        contact = db.multiDOA().getEmergencyContact(num)

        if(contact!= null) Log.i("JOSHeditE", contact?.allInfo)

        list = db.multiDOA().distinctGroupsForEmergency
        list.add(0,"Please select group..")


        adapterForSpinner = ArrayAdapter<String>(this@EditEmergency,
                R.layout.spinnerz, list)
        editspinner.adapter = adapterForSpinner
        if(contact!= null){
            inputExistingSettings()
        }else{
            selectImage(imageNum)
        }

        editswitch.setOnClickListener{ helper2.requestPermissions()}
    }



    private fun showSheet() {
        val builder: BottomSheet.Builder
        builder = BottomSheet.Builder(Home@this)


        builder.setWindowDimming(80)
                .setTitleMultiline(true)
                .setBackgroundColor(Color.parseColor("#313131"))
                .setTitleTextColor(Color.parseColor("#ffffff"))


        builder.setOnShowListener { }
        builder.setOnDismissListener { }

        builder.setCallback(object : BottomSheetCallback {
            override fun onShown() {}

            override fun onDismissed() {}
        })

        builder.setTitle("Enabling this location setting will also send a google maps weblink as well as the latitude and longitude of your current location in plain text(granted that location is enabled on your device) in plain text about 8 to 20 seconds following the first message")
        builder.show()
    }

    private fun inputExistingSettings() {

        editet1.setText(contact?.name)

        if(list.contains(contact?.group)){
            Log.i("JOSHeditE", "helper1")

            editspinner.setSelection(adapterForSpinner.getPosition(contact?.group))
        }else{
            Log.i("JOSHeditE", "helper2")
            editspinner.setSelection(0)

        }

        if(contact?.code1 == 1){
            editswitch.isChecked = true
        }else{
            editswitch.isChecked = false
        }

        editet2.setText(contact?.message)
        imageNum = contact?.code2!!
        selectImage(imageNum)
    }

    private fun selectImage(code2: Int?) {
        imagell1.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        imagell2.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        imagell3.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        imagell4.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        imagell5.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        when(code2){
            1-> imagell1.setBackgroundColor(resources.getColor(R.color.colorAccent))
            2-> imagell2.setBackgroundColor(resources.getColor(R.color.colorAccent))
            3-> imagell3.setBackgroundColor(resources.getColor(R.color.colorAccent))
            4-> imagell4.setBackgroundColor(resources.getColor(R.color.colorAccent))
            5-> imagell5.setBackgroundColor(resources.getColor(R.color.colorAccent))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        helper2.handleResult(requestCode, permissions, grantResults)
    }

    private fun showDialogForConfirm() {
        val group = editspinner.selectedItem.toString()
        val onOffLocation = if(editswitch.isChecked) 1 else 0
        val onOffLocationBool = if(editswitch.isChecked) true else false
        val messageBody = editet2.text.toString()
        val name = editet1.text.toString()
        val joinedString = "Name:\n$name\n\nGroup:\n" +
                "$group\n\n" +
                "Message:\n$messageBody\n\nInclude Location:\n" +
                "$onOffLocationBool"

        Log.i("JOSHeditE", joinedString)
        val builder: AlertDialog.Builder

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = AlertDialog.Builder(this@EditEmergency, android.R.style.Theme_Material_Dialog_Presentation)
        } else {
            builder = AlertDialog.Builder(this@EditEmergency)

        }
        builder.setTitle("SAVE THIS PRESET PROFILE?")
                .setMessage(joinedString)
                .setPositiveButton("YES", DialogInterface.OnClickListener { dialog, which ->

                    if(contact != null){
                        if(messageBody.length>0 && !group.equals("Please select group..")&& name.length>0){
                            db.multiDOA().updateEmergencySetup(contact?.id, name, group, messageBody, onOffLocation)
                            FabToast.makeText(this@EditEmergency, "Successfully Updated", Toast.LENGTH_SHORT,
                                    FabToast.SUCCESS, FabToast.POSITION_DEFAULT).show()
                            finish()

                        }else{
                            db.multiDOA().deleteWithId(contact?.id!!)
                            FabToast.makeText(this@EditEmergency, "Not enough information, profile " +
                                    "was deleted", Toast.LENGTH_SHORT,
                                    FabToast.WARNING, FabToast.POSITION_DEFAULT).show()

                            finish()
                        }

                    }else{

                        if(messageBody.length>0 && !group.equals("Please select group..")&& name.length>0){
                            val c = Contact()
                            c.code1 = onOffLocation
                            c.name = name
                            c.message = messageBody
                            c.group = group
                            c.type = num
                            c.code2 = imageNum
                            c.timeInMillis = System.currentTimeMillis()
                            c.typeEntry =  "edit_emergency"
                            db.multiDOA().insertAll(c)
                            Log.i("JOSHeditE", "saved in")
                            FabToast.makeText(this@EditEmergency, "Success", Toast.LENGTH_SHORT,
                                    FabToast.SUCCESS, FabToast.POSITION_DEFAULT).show()
                            finish()
                        }else{
                            FabToast.makeText(this@EditEmergency, "All criteria must be filled out", Toast.LENGTH_SHORT,
                                    FabToast.ERROR, FabToast.POSITION_DEFAULT).show()
                        }


                    }
                })
                .setNegativeButton("NO", DialogInterface.OnClickListener { dialog, which ->
                    FabToast.makeText(this@EditEmergency, "Canceled", Toast.LENGTH_SHORT,
                            FabToast.INFORMATION, FabToast.POSITION_DEFAULT).show()
                })
                .setIcon(android.R.drawable.ic_dialog_info)
        if(messageBody.length>0 && !group.equals("Please select group..")&& name.length>0){
            builder.show()
        }else{
            FabToast.makeText(this@EditEmergency, "All criteria must be filled out", Toast.LENGTH_SHORT,
                    FabToast.ERROR, FabToast.POSITION_DEFAULT).show()
        }



    }



    inner class PermissionsHelper2 : PermissionsDirective{
        override fun permissionsToRequest(): Array<String?> {
            var array =  arrayOfNulls<String>(1)
            array[0] = "android.permission.ACCESS_FINE_LOCATION"
            return array
        }
        override val requestCode: Int
            get() = 103
        override val activity: Activity
            get() = Outer@ this@EditEmergency
        override fun executeOnPermissionGranted() {

        }
        override fun executeOnPermissionDenied() {
            editswitch.isChecked = false
            FabToast.makeText(Outer@ this@EditEmergency, "Not available without permission.", Toast.LENGTH_SHORT,
                    FabToast.WARNING, FabToast.POSITION_DEFAULT).show()}
    }
    fun deleteAll(v: View){
        db.multiDOA().deleteWithId(contact?.id!!)
        FabToast.makeText(this@EditEmergency, "DELETED!", Toast.LENGTH_SHORT,
                FabToast.INFORMATION, FabToast.POSITION_DEFAULT).show()
        Log.i("JOSHtester", "done")
        finish()
    }

    fun createNew(c: View){
        startActivity(Intent(this@EditEmergency, CreateContactList::class.java))
        Bungee.slideRight(this@EditEmergency)
        finish()
    }
    fun imageSelected(view: View){
        when(view.id){
            R.id.imagell1 -> {
                imageNum = 1
                selectImage(imageNum)
            }
            R.id.imagell2 -> {
                imageNum = 2
                selectImage(imageNum)
            }
            R.id.imagell3 -> {
                imageNum = 3
                selectImage(imageNum)
            }
            R.id.imagell4 -> {
                imageNum = 4
                selectImage(imageNum)
            }
            R.id.imagell5 -> {
                imageNum = 5
                selectImage(imageNum)
            }
        }
    }

}
