package com.miempresa.lab12_comments


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)

        val btnBuscarPost = findViewById<Button>(R.id.btnBuscarComment)

        btnBuscarPost.setOnClickListener{
            val urslBase = "https://jsonplaceholder.typicode.com/"
            val xid = findViewById<EditText>(R.id.txtID2)

            val retrofit = Retrofit.Builder().baseUrl(urslBase)
                .client(getUnsafeOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(PostApiService::class.java)
            val context : Context = this

            lifecycleScope.launch {
                val response = service.getCommentPostById(xid.text.toString())
                if (response.isSuccessful) {
                    runOnUiThread {
                        val postId = findViewById<TextView>(R.id.post_id2)
                        val name = findViewById<TextView>(R.id.name2)
                        val email = findViewById<TextView>(R.id.email2)
                        val body = findViewById<TextView>(R.id.body2)

                        postId.text = response.body()?.postId.toString()
                        name.text = response.body()?.name
                        email.text = response.body()?.email
                        body.text = response.body()?.body
                    }
                }
                else {
                    runOnUiThread {
                        Toast.makeText(context, "Error GET Retrofit al buscar registro ${xid.text}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun getUnsafeOkHttpClient(): OkHttpClient {
        // Crear un gestor confiable que no valide cadenas de certificados
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}

            override fun getAcceptedIssuers():
                    Array<java.security.cert.X509Certificate> = arrayOf()
        })

        // Instalar el gestor de confianza
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory,
                trustAllCerts[0] as X509TrustManager
            )
            .hostnameVerifier { _, _ -> true }
            .build()
    }
}