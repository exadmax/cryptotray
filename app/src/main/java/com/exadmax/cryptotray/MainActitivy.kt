package com.exadmax.cryptotray

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.exadmax.cryptotray.databinding.ActivityMainBinding
import com.exadmax.cryptotray.RetrofitClient
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btRefresh.setOnClickListener {
            fetchCryptoPrices()
        }

        setupPeriodicWork()
    }

    private fun fetchCryptoPrices() {
        Thread {
            val service = RetrofitClient.instance
            val call = service.getPrices("bitcoin,ethereum", "brl")

            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    val body = response.body()
                    val btc = body?.get("bitcoin")?.get("brl")
                    val eth = body?.get("ethereum")?.get("brl")

                    if (btc != null && eth != null) {
                        runOnUiThread {
                            binding.helloText.text = """
                                💰 Preços Atualizados:
                                • BTC: R$ ${"%,.2f".format(btc)}
                                • ETH: R$ ${"%,.2f".format(eth)}
                            """.trimIndent()
                        }
                    }
                } else {
                    showError()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showError()
            }
        }.start()
    }

    private fun showError() {
        runOnUiThread {
            binding.helloText.text = "❌ Falha ao obter preços. Tente novamente."
        }
    }

    private fun setupPeriodicWork() {
        val work = PeriodicWorkRequestBuilder<PriceWorker>(1, TimeUnit.HOURS).build()
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "priceWork",
                ExistingPeriodicWorkPolicy.REPLACE,
                work
            )
    }
}
