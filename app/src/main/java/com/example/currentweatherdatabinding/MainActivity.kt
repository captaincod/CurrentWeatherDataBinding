package com.example.currentweatherdatabinding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.currentweatherdatabinding.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.io.InputStream
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.weather = Weather("Irkutsk", 0.0)

        /*
        TODO: реализовать отображение погоды в текстовых полях и картинках
        картинками отобразить облачность и направление ветра
        использовать data binding, библиотеки уже подключены
         */
    }
    suspend fun loadWeather() {
        val apiKey: String = getString(R.string.apikey)
        val city: String = "Karluk"
        val weatherURL =
            "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric"
        val stream = URL(weatherURL).content as InputStream
        // JSON отдаётся одной строкой,
        val data = Scanner(stream).nextLine()
        // TODO: предусмотреть обработку ошибок (нет сети, пустой ответ)
        Log.d("mytag", data)
    }

    @DelicateCoroutinesApi
    fun onClick(view: android.view.View) {
        // Используем IO-диспетчер вместо Main (основного потока)
        GlobalScope.launch (Dispatchers.IO) {
            loadWeather()
        }

        binding.weather = Weather("Karluk", 10.0) // change to random value
        Log.d("mytag", "weather changed to:")
    }

}

data class Weather(val weather_main: String, var main_temp: Double)