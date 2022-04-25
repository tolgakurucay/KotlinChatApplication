package com.tolgakurucay.kotlinchatapplication

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.tolgakurucay.kotlinchatapplication.databinding.ActivityPhoneVerificationBinding
import java.util.concurrent.TimeUnit

//view binding
private lateinit var binding: ActivityPhoneVerificationBinding

//if code sending failed, will used to resend
private var forceResendingToken: com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken?=null

private var mCallBacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks?=null
private var mCallBacks1:PhoneAuthProvider.OnVerificationStateChangedCallbacks?=null
private var mVerificationId:String?=null
private lateinit var firebaseAuth:FirebaseAuth




//progress dialog
private lateinit var progressDialog:ProgressDialog


private var usingTel:String?=null
class PhoneVerificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPhoneVerificationBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        val info = intent.getStringExtra("info")


        firebaseAuth= FirebaseAuth.getInstance()
        firebaseAuth.setLanguageCode("tr")

        progressDialog= ProgressDialog(this@PhoneVerificationActivity)
        progressDialog.setTitle("Lütfen Bekleyiniz")
        progressDialog.setCanceledOnTouchOutside(false)


        if(info=="register")//üye ol
        {
            binding.phoneLl.visibility=View.VISIBLE
            binding.codeLl.visibility=View.GONE
            mCallBacks1=object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

                    signInWithPhoneAuthCredential(phoneAuthCredential)
                }


                override fun onVerificationFailed(ex: FirebaseException) {
                    progressDialog.dismiss()

                    Toast.makeText(this@PhoneVerificationActivity, "Doğrulama bir nedenden dolayı başarısız!\nBir süre beklemeniz gerekecek", Toast.LENGTH_LONG).show()
                }



                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {

                    mVerificationId=verificationId
                    forceResendingToken=token
                    progressDialog.dismiss()


                    //hide phone layout show code layout

                    binding.phoneLl.visibility=View.GONE
                    binding.codeLl.visibility=View.VISIBLE
                    Toast.makeText(this@PhoneVerificationActivity,"Doğrulama kodu gönderildi",Toast.LENGTH_LONG).show()
                    binding.codeSentDescriptionTv.text="Lütfen telefonunuza gelen doğrulama kodunu giriniz"
                }

            }

            binding.phoneContinueBtn.setOnClickListener {
                //input phone number
                usingTel=binding.phoneEt.text.toString().trim()
                //validate phone number
                if(TextUtils.isEmpty(usingTel)){
                    Toast.makeText(this@PhoneVerificationActivity,"Lütfen telefon numaranızı giriniz",Toast.LENGTH_LONG).show()

                }
                else
                {
                    if(usingTel!!.length!=13)
                    {
                        Toast.makeText(this@PhoneVerificationActivity,"Lütfen düzgün bir şekilde telefon numaranızı giriniz",Toast.LENGTH_LONG).show()

                    }
                    else
                    {
                        startPhoneNumberVerification()
                    }

                }

            }




            binding.codeSubmitBtn.setOnClickListener {
                //input verification code
                val code=binding.codeEt.text.toString().trim()

                if(TextUtils.isEmpty(code)){
                    Toast.makeText(this@PhoneVerificationActivity,"Lütfen kodu giriniz",Toast.LENGTH_LONG).show()

                }
                else
                {
                    verifyPhoneNumberWithCode(mVerificationId!!,code)
                }

            }






            binding.resendCodeTv.setOnClickListener{
                usingTel=binding.phoneEt.text.toString().trim()
                //validate phone number
                if(TextUtils.isEmpty(usingTel)){
                    Toast.makeText(this@PhoneVerificationActivity,"Lütfen telefon numaranızı giriniz",Toast.LENGTH_LONG).show()

                }
                else
                {
                    resendVerificationCode(usingTel!!, forceResendingToken!!)
                }

            }
        }

        else if(info=="signin")//giriş yap
        {


            usingTel=intent.getStringExtra("tel").toString().trim()

            println(usingTel.toString())

            binding.codeLl.visibility=View.VISIBLE
            binding.phoneLl.visibility=View.GONE




            mCallBacks=object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

                    signInWithPhoneAuthCredential(phoneAuthCredential)
                }


                override fun onVerificationFailed(ex: FirebaseException) {
                    progressDialog.dismiss()

                    Toast.makeText(this@PhoneVerificationActivity, "Çok fazla kod gönderildi", Toast.LENGTH_LONG).show()
                }



                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {

                    mVerificationId=verificationId
                    forceResendingToken=token
                    progressDialog.dismiss()






                    Toast.makeText(this@PhoneVerificationActivity,"Doğrulama kodu gönderildi",Toast.LENGTH_LONG).show()
                    binding.codeSentDescriptionTv.text="Lütfen telefonunuza gelen doğrulama kodunu giriniz"
                }

            }
            startPhoneNumberVerification()






            binding.codeSubmitBtn.setOnClickListener {
                //input verification code
                val code=binding.codeEt.text.toString().trim()

                if(TextUtils.isEmpty(code)){
                    Toast.makeText(this@PhoneVerificationActivity,"Lütfen kodu giriniz",Toast.LENGTH_LONG).show()

                }
                else
                {
                    verifyPhoneNumberWithCode(mVerificationId!!,code)
                }

            }






            binding.resendCodeTv.setOnClickListener{

                //validate phone number
                if(TextUtils.isEmpty(usingTel)){
                    Toast.makeText(this@PhoneVerificationActivity,"Lütfen telefon numaranızı giriniz",Toast.LENGTH_LONG).show()

                }
                else
                {
                    resendVerificationCode(usingTel!!, forceResendingToken!!)
                }

            }

        }







    }
    private fun startPhoneNumberVerification(){

        progressDialog.setMessage("Telefon numaranız doğrulanıyor...")
        progressDialog.show()
        if(intent.getStringExtra("info")=="register")
        {
            val options=PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(usingTel.toString())
                .setTimeout(60L,TimeUnit.SECONDS)
                .setActivity(this)

                .setCallbacks(mCallBacks1!!)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }
        else
        {
            val options=PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(usingTel.toString())
                .setTimeout(60L,TimeUnit.SECONDS)
                .setActivity(this)

                .setCallbacks(mCallBacks!!)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }

    }


    private fun resendVerificationCode(phoneNumber:String,token:PhoneAuthProvider.ForceResendingToken){
        progressDialog.setMessage("Kod tekrardan gönderiliyor...")
        progressDialog.show()

        val options=PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(usingTel!!)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallBacks!!)
            .setForceResendingToken(token)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(verificationId:String,code:String){
        progressDialog.setMessage("Kod doğrulanıyor...")
        progressDialog.show()

        val credential=PhoneAuthProvider.getCredential(verificationId,code)
        signInWithPhoneAuthCredential(credential)

    }


    private fun signInWithPhoneAuthCredential(credential:PhoneAuthCredential){//giriş yapma
        progressDialog.setMessage("Giriş Yapılıyor")

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                val info=intent.getStringExtra("info")
                if(info=="register"){
                    progressDialog.dismiss()
                    val phone= firebaseAuth.currentUser!!.phoneNumber
                    //Toast.makeText(this,"$phone numarasıyla giriş yapıldı",Toast.LENGTH_LONG).show()

                    //start profile activity
                    val intent=Intent(this,addSaveActivvity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra("phoneNumber",phone)
                    startActivity(intent)
                }
                else
                {
                    val intentt=Intent(this@PhoneVerificationActivity,homePageActivity::class.java)


                    startActivity(intentt)
                    Toast.makeText(this@PhoneVerificationActivity,"Giriş Yapıldı\nHoşgeldin $usingTel",Toast.LENGTH_SHORT).show()
                }




            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Kod doğrulanamadı",Toast.LENGTH_LONG).show()
                val intent= Intent(this@PhoneVerificationActivity,PhoneVerificationActivity::class.java)
                finish()
                 startActivity(intent)


            }
    }



}