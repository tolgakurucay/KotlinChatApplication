package com.tolgakurucay.kotlinchatapplication

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.tolgakurucay.kotlinchatapplication.databinding.ActivityHomePageBinding
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.android.synthetic.main.dialog_add_number.*
import kotlinx.android.synthetic.main.dialog_add_number.view.*
import com.tolgakurucay.kotlinchatapplication.homePageActivity as homePageActivity

private lateinit var auth:FirebaseAuth
private lateinit var binding:ActivityHomePageBinding
private lateinit var firestore: FirebaseFirestore
class homePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHomePageBinding.inflate(layoutInflater)
        val view= binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuinflater=menuInflater
        menuinflater.inflate(R.menu.home_page_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.signOut)
        {
            auth.signOut()
            val intent=Intent(this@homePageActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        if(item.itemId==R.id.updatePp)
        {
            val intent=Intent(this,UpdatePpAndDescript::class.java)
            startActivity(intent)
        }
        if(item.itemId==R.id.addFriend)
        {
            showDialog()
        }

        return super.onOptionsItemSelected(item)

    }
    private fun showDialog(){
        var telephoneNumber=""
        val builder=AlertDialog.Builder(this)
        val inflater=layoutInflater
        val dialogLayout=inflater.inflate(R.layout.dialog_add_number,null)
        val editText=dialogLayout.et_editText

        with(builder){
            setTitle("Arkadaş Ekle")
            setPositiveButton("Ekle"){dialog,which->

                 telephoneNumber=editText.text.toString()
                if(telephoneNumber!= auth.currentUser!!.phoneNumber)
                {
                    val ex= firestore.collection("ID${telephoneNumber}").get()
                    ex.addOnSuccessListener {
                        if(it!=null){

                        }
                        else
                        {
                            Toast.makeText(this@homePageActivity, "Telefon numarası bulınamadı", Toast.LENGTH_SHORT).show()
                        }
                    }



                }
                else
                {
                    Toast.makeText(this@homePageActivity, "Kendi telefon numaranızı girdiniz", Toast.LENGTH_SHORT).show()
                }

            }
            setView(dialogLayout)
            show()
        }





    }



}