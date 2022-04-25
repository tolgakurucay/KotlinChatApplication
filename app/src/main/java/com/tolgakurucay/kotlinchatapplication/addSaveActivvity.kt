package com.tolgakurucay.kotlinchatapplication

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

import com.tolgakurucay.kotlinchatapplication.databinding.ActivityAddSaveActivvityBinding
import java.lang.Exception
import java.util.*

private lateinit var binding:ActivityAddSaveActivvityBinding
private lateinit var permissionLauncher: ActivityResultLauncher<String>
private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
private lateinit var storage:FirebaseStorage
private lateinit var firestore:FirebaseFirestore
private lateinit var auth:FirebaseAuth


private var selectedImage:Uri?=null
var telephoneNumber:String?=null

private lateinit var progressDialog: ProgressDialog
class addSaveActivvity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddSaveActivvityBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        registerLauncher()
        telephoneNumber=intent.getStringExtra("phoneNumber")
        binding.textViewTelephone.text= telephoneNumber.toString().trim()
        storage= FirebaseStorage.getInstance()
        firestore= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()

        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Lütfen Bekleyiniz")
        progressDialog.setCanceledOnTouchOutside(false)



    }

    fun kayitOl(view: View){
        val tn= auth.currentUser!!.phoneNumber

        progressDialog.setMessage("Kayıt olunuyor")
        progressDialog.show()
        val reference= storage.reference
        val imageReference=reference.child("$tn/Photos/PP")
        imageReference.putFile(selectedImage!!).addOnSuccessListener {

            val uploadReference= storage.reference.child("$tn").child("Photos").child("PP")

            uploadReference.downloadUrl.addOnSuccessListener {


                val downloadUri=it.toString()
                val postMap= hashMapOf<String,Any>()
                postMap.put("userTel", tn!!)
                postMap.put("downloadUri",downloadUri)
                postMap.put("name",binding.editTextIsim.text.toString())
                postMap.put("surname", binding.editTextSurname.text.toString())
                postMap.put("description","I'm here to use the best app on the world")



                firestore.collection("ID${tn.toString()}").document("INFORMATION").set(postMap).addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this,"Sayın ${binding.editTextIsim.text} ${binding.editTextSurname.text}\nBilgileriniz başarıyla kaydedildi",Toast.LENGTH_LONG).show()
                    val intent=Intent(this,homePageActivity::class.java)
                    startActivity(intent)
                    finish()

                }
            }

        }


    }

     fun selectPhotoSaveActivity(view:View){

        if(ContextCompat.checkSelfPermission(this@addSaveActivvity,android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(binding.root,"Fotoğraf seçmek için galeri izni gereklidir",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver"){
                    //izin iste
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }
            else
            {
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        else
        {
            val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intent)
        }

    }
    fun registerLauncher()
    {

        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
            if(result.resultCode== RESULT_OK)
            {

                val intentFromResult=result.data
                if(intentFromResult!=null)
                {
                    val imageData=intentFromResult.data
                    //binding.imageView.setImageURI(imageData)
                    //burda uri ı bitmap'e çevirmemiz lazım çünkü sqlite veritabanına bu şekilde giriyor
                    if(imageData!=null)
                    {
                        selectedImage=imageData
                        binding.imageView.setImageURI(selectedImage)

                    }



                }
            }
        }






        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it)
            {
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
            else
            {
                Toast.makeText(this,"İzin vermeniz gerekir",Toast.LENGTH_LONG).show()
            }
        }



    }




}