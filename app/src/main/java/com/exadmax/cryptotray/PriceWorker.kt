package com.exadmax.cryptotray

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.Call

class PriceWorker(
  appContext: Context,
  params: WorkerParameters
) : Worker(appContext, params) {

  override fun doWork(): Result {
    // 1) monta o Retrofit
    val retrofit = Retrofit.Builder()
      .baseUrl("https://api.coingecko.com/api/v3/")
      .addConverterFactory(MoshiConverterFactory.create())
      .build()

    val service = retrofit.create(CryptoService::class.java)

    return try {
      // 2) executa a chamada síncrona
      val call: Call<Map<String, Map<String, Double>>> =
        service.getPrices("bitcoin,ethereum", "brl")
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
      // se falhar ou não vier preço, tenta de novo mais tarde
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

    // cria canal no Android O+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        channelId,
        "Preços de Cripto",
        NotificationManager.IMPORTANCE_DEFAULT
      )
      nm.createNotificationChannel(channel)
    }

    // formata em real com 2 casas
    val text = "BTC: R$ ${"%,.2f".format(btc)} • ETH: R$ ${"%,.2f".format(eth)}"

    val notif = NotificationCompat.Builder(applicationContext, channelId)
    .setContentTitle("Crypto Tray")
    .setContentText(text)
    .setSmallIcon(android.R.drawable.ic_dialog_alert) // <-- ícone padrão do Android
    .build()

    nm.notify(1, notif)
  }
}
