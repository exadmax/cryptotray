package com.exadmax.cryptotray

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import retrofit2.Call

class PriceWorker(
    appContext: Context,
    params: WorkerParameters
) : Worker(appContext, params) {

    override fun doWork(): Result {
        val service = RetrofitClient.instance
        val call: Call<Map<String, Map<String, Double>>> =
            service.getPrices("bitcoin,ethereum", "brl")

        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val body = response.body()
                val btc = body?.get("bitcoin")?.get("brl")
                val eth = body?.get("ethereum")?.get("brl")

                if (btc != null && eth != null) {
                    showNotification(btc, eth)
                    return Result.success()
                }
            }
            Result.retry()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private fun showNotification(btc: Double, eth: Double) {
        val channelId = "crypto_prices"
        val nm = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Preços de Cripto",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            nm.createNotificationChannel(channel)
        }

        val text = "BTC: R$ ${"%,.2f".format(btc)} • ETH: R$ ${"%,.2f".format(eth)}"

        val notif = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Crypto Tray")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .build()

        nm.notify(1, notif)
    }
}
