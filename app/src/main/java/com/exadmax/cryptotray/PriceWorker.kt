package com.exadmax.cryptotray

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class PriceWorker(appContext: Context, params: WorkerParameters) : Worker(appContext, params) {
    override fun doWork(): Result {
        // TODO: usar Retrofit para buscar preços em BRL e exibir notificação
        return Result.success()
    }
}
