package com.tolgakurucay.kotlinchatapplication

import android.app.ProgressDialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.graphics.ColorSpace
import android.net.wifi.hotspot2.pps.Credential
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract
import android.telephony.PhoneNumberUtils
import android.text.method.HideReturnsTransformationMethod
import android.view.View
import com.tolgakurucay.kotlinchatapplication.databinding.ActivityMainBinding
import android.widget.CompoundButton
import android.widget.Toast
import android.text.method.PasswordTransformationMethod
import android.view.animation.Transformation
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore


private lateinit var binding: ActivityMainBinding
private lateinit var firestore: FirebaseFirestore
private lateinit var auth:FirebaseAuth
//if code sending failed, will used to resend
private var forceResendingToken: com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken?=null

private var mCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks?=null
private var mVerificationId:String?=null




//progress dialog
private lateinit var progressDialog: ProgressDialog

private var credential:Credential?=null
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)


        firestore= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        credential=null
        if(auth.currentUser!=null)
        {
            val intent=Intent(this@MainActivity,homePageActivity::class.java)
            startActivity(intent)
            finish()
        }











    }


    fun uyeOl(view:View)
    {
        val intent=Intent(this,PhoneVerificationActivity::class.java)
        intent.putExtra("info","register")
        startActivity(intent)
    }
    fun girisYap(view:View)
    {
        if(binding.editTextPhone.text.length!=0){

            if(binding.editTextPhone.text.length<13)
            {
                Toast.makeText(this,"Lütfen telefon numaranızı tam olarak giriniz",Toast.LENGTH_LONG).show()
            }
            else
            {
                val intent=Intent(this@MainActivity,PhoneVerificationActivity::class.java)
                intent.putExtra("info","signin")
                intent.putExtra("tel", binding.editTextPhone.text.toString().trim())

                startActivity(intent)
                finish()
            }



        }
        else
        {
            Toast.makeText(this,"Lütfen telefon numaranızı giriniz",Toast.LENGTH_LONG).show()
        }




    }





    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuinflater=menuInflater
        menuinflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.yapimci)
        {
            val alert=AlertDialog.Builder(this)
            alert.setTitle("Yapımcı")
            alert.setMessage("Tolga Kuruçay\nBilgisayar mühendisliği 4.ncü sınıf öğrencisi")
            alert.setIcon(R.drawable.tolga)
            alert.show()

        }
        else if(item.itemId==R.id.iletisim)
        {
            val alert=AlertDialog.Builder(this)
            alert.setTitle("İletişim Bilgileri")
            alert.setMessage("E-posta : tolgakurucay1446@gmail.com")
            alert.setIcon(R.drawable.iletisim)

            alert.show()
        }
        return super.onOptionsItemSelected(item)
    }
}