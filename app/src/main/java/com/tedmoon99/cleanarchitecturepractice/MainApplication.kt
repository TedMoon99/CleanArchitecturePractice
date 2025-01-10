package com.tedmoon99.cleanarchitecturepractice

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


/**
 * @HiltAndroidApp 어노테이션을 지정함으로써 애플리케이션은 하나의 Singleton Component가 된다
 */
@HiltAndroidApp
class MainApplication: Application() {

}