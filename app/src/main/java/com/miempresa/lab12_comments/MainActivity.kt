package com.miempresa.lab12_comments


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
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

class MainActivity : AppCompatActivity() {

    val urlBase = "https://jsonplaceholder.typicode.com/"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Crear un cliente de Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(urlBase)
            .client(getUnsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create()).build()

        // Crear un servicio de la API
        val service = retrofit.create(PostApiService::class.java)

        lifecycleScope.launch {
            val response = service.getCommentPost()

            response.forEach {
                println(it)
            }

            runOnUiThread {
                val postId = findViewById<TextView>(R.id.post_id)
                val id = findViewById<TextView>(R.id.id)
                val name = findViewById<TextView>(R.id.name)
                val email = findViewById<TextView>(R.id.email)
                val body = findViewById<TextView>(R.id.body)

                postId.text = response.first().postId.toString()
                id.text = response.first().id.toString()
                name.text = response.first().name.toString()
                email.text = response.first().email.toString()
                body.text = response.first().body.toString()

            }
        }

        val btn2: Button = findViewById(R.id.btnConsultarPost)

        btn2.setOnClickListener{
            val intent:Intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
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
                trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

}