package com.tolgakurucay.kotlinchatapplication

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.net.Uri.parse
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.core.widget.ImageViewCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.protobuf.Value
import com.squareup.picasso.Picasso
import com.tolgakurucay.kotlinchatapplication.databinding.ActivityUpdatePpAndDescriptBinding
import java.io.File
import java.net.URI
import java.net.URL

private lateinit var binding:ActivityUpdatePpAndDescriptBinding
private lateinit var permissionLauncher : ActivityResultLauncher<String>
private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
private lateinit var storage:FirebaseStorage
private lateinit var firestore:FirebaseFirestore
private lateinit var auth:FirebaseAuth

private var selectedPicture:Uri?=null
private lateinit var progressDialog: ProgressDialog
class UpdatePpAndDescript : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUpdatePpAndDescriptBinding.inflate(layoutInflater)
        val view=binding.root


        setContentView(view)
        registerLauncher()
        storage= FirebaseStorage.getInstance()
        firestore= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("L??tfen Bekleyiniz")
        progressDialog.setCanceledOnTouchOutside(false)
       getData()
    }
    fun selectImage(view:View)
    {

        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Snackbar.make(binding.root,"Galeriye gitmek i??in izin gerekli",Snackbar.LENGTH_INDEFINITE).setAction("??zin ver"){

                    //request permission
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }

            }
            else
            {
                //request permission

                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }

        }
        else
        {
            val intentToGallery= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }



    }
    fun update(view: View)
    {



        //G??ncelleme i??lemleri
            val telephoneNumber= auth.currentUser!!.phoneNumber
            progressDialog.setMessage("Bilgileriniz g??ncelleniyor")
            progressDialog.show()


            val reference= storage.reference
            val imageReference=reference.child("$telephoneNumber/Photos/PP")

            imageReference.putFile(selectedPicture!!).addOnSuccessListener {
                val uploadReference= storage.reference.child("$telephoneNumber").child("Photos").child("PP")

                uploadReference.downloadUrl.addOnSuccessListener {

                    val downloadUri=it.toString()
                    val postMap= hashMapOf<String,Any>()
                    postMap.put("userTel", telephoneNumber!!)
                    postMap.put("downloadUri",downloadUri)
                    postMap.put("name",binding.edittextName.text.toString())
                    postMap.put("surname", binding.editextSurname.text.toString())
                    postMap.put("description", binding.editTextDesc.text.toString())

                    firestore.collection("ID${telephoneNumber}").document("INFORMATION").set(postMap).addOnSuccessListener {
                        progressDialog.dismiss()
                        Toast.makeText(this,"Say??n ${binding.edittextName.text} ${binding.editextSurname.text}\nBilgileriniz ba??ar??yla g??ncellendi",Toast.LENGTH_LONG).show()
                        val intent=Intent(this,homePageActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()

                    }
                }


            }


        }


















    fun registerLauncher()
    {
        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode== RESULT_OK)
            {
                val intentFromIt=it.data
                if(intentFromIt!=null)
                {

                    val data =intentFromIt.data

                    if(data!=null)
                    {
                        selectedPicture=data
                        binding.imageView3.setImageURI(selectedPicture)


                    }
                    else
                    {
                        Toast.makeText(this,"Foto??raf Y??klenemedi",Toast.LENGTH_LONG).show()
                    }
                }
                else
                {
                    Toast.makeText(this,"Veri ??ekilemedi",Toast.LENGTH_LONG).show()
                }
            }

        }

        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it)//izin verildiyse
            {
                val intentToGallery= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
            else
            {
                Toast.makeText(this,"??zin vermeniz gerekli!",Toast.LENGTH_LONG).show()
            }
        }


    }

    fun getData(){
        progressDialog.setMessage("Eski veriler y??kleniyor")
        progressDialog.show()
        val telephoneNumber= auth.currentUser!!.phoneNumber
        //verileri textlere ve imageview'e yazd??r

        val documentReference= firestore.collection("ID${telephoneNumber}").document("INFORMATION")


        documentReference.get()
            .addOnSuccessListener { document->
                if(document!=null){

                    val name= document.data!!["name"].toString()
                    val surname=document.data!!["surname"].toString()
                    val description=document.data!!["description"].toString()
                    val image=document.data!!["downloadUri"].toString()



                    binding.edittextName.setText(name)
                    binding.editextSurname.setText(surname)
                    binding.editTextDesc.setText(description)




                    //Picasso.get().load(image).into(binding.imageView3)
                    selectedPicture=image.toUri()
                    Picasso.get().load(selectedPicture).into(binding.imageView3)











                }
            }
        progressDialog.dismiss()
    }
}










