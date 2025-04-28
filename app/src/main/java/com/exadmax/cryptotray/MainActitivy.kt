package com.exadmax.cryptotray

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.exadmax.cryptotray.databinding.ActivityMainBinding
import androidx.work.*

import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btRefresh.setOnClickListener {
            // TODO: disparar imediatamente o fetch + notificação
        }

        setupPeriodicWork()
    }

    private fun setupPeriodicWork() {
        val work = PeriodicWorkRequestBuilder<PriceWorker>(1, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "priceWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            work
        )
    }
}
