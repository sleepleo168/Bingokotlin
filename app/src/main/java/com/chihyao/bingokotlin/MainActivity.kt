package com.chihyao.bingokotlin

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {
    companion object{
        val TAG = MainActivity::class.java.simpleName
        val RC_SIGN_IN = 100
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_menu_signout -> {
                FirebaseAuth.getInstance().signOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        auth.currentUser?.also {
            Log.d(TAG, "onAuthStateChanged: ${it.email}/${it.uid}")
            it.displayName?.run {
                FirebaseDatabase.getInstance("https://bingo-e33cc-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("users")
                    .child(it.uid)
                    .child("displayName")
                    .setValue(this) 
                    .addOnCompleteListener { Log.d(TAG, "onAuthStateChanged: db setvalue done")}
            }

        } ?: signUp()
/*        if (auth.currentUser == null) {
            signUp()
        } else {
            Log.d(TAG, "${auth.currentUser!!.email}/${auth.currentUser!!.uid}" )
        }*/
    }

    private fun signUp() {

        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(
                    Arrays.asList(
                        EmailBuilder().build(),
                        GoogleBuilder().build()
                    )
                )
                .setIsSmartLockEnabled(false)
                .build(), RC_SIGN_IN
        )
    }
}