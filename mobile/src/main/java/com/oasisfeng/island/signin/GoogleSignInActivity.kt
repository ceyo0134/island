package com.oasisfeng.island.signin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.oasisfeng.island.mobile.R

/**
 * Standard Google Sign-In for this app only.
 * Uses Google's official account chooser and consent screen. The result is used solely by this app;
 * nothing is handed over to, or injected into, other (cloned) apps.
 */
class GoogleSignInActivity : FragmentActivity() {

    private lateinit var client: GoogleSignInClient
    private lateinit var signInButton: Button
    private lateinit var signOutButton: Button
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_sign_in)

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestProfile().build()
        client = GoogleSignIn.getClient(this, options)

        status = findViewById(R.id.sign_in_status)
        signInButton = findViewById(R.id.button_google_sign_in)
        signOutButton = findViewById(R.id.button_google_sign_out)
        signInButton.setOnClickListener { startActivityForResult(client.signInIntent, REQUEST_SIGN_IN) }
        signOutButton.setOnClickListener { client.signOut().addOnCompleteListener(this) { render(null) } }

        render(GoogleSignIn.getLastSignedInAccount(this))
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQUEST_SIGN_IN) return
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)
            render(account)
        } catch (e: ApiException) {
            val msg = if (e.statusCode == CommonStatusCodes.DEVELOPER_ERROR || e.statusCode == 10)
                getString(R.string.google_sign_in_config_error) else getString(R.string.google_sign_in_failed, e.statusCode)
            if (resultCode != Activity.RESULT_CANCELED) Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            render(null)
        }
    }

    private fun render(account: GoogleSignInAccount?) {
        val signedIn = account != null
        signInButton.visibility = if (signedIn) View.GONE else View.VISIBLE
        signOutButton.visibility = if (signedIn) View.VISIBLE else View.GONE
        status.text = if (account != null) getString(R.string.google_sign_in_signed_in_as, account.displayName ?: "", account.email ?: "")
        else getString(R.string.google_sign_in_signed_out)
    }

    companion object { private const val REQUEST_SIGN_IN = 9001 }
}
