package com.example.currentweatherdatabinding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.example.currentweatherdatabinding.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.InputStream
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var icon_view: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.weather = Weather("City name", "Weather", "Temperature")
        icon_view = findViewById(R.id.icon_imageview)

        /*
        TODO: реализовать отображение погоды в текстовых полях и картинках
        картинками отобразить облачность и направление ветра
        использовать data binding, библиотеки уже подключены
         */

    }
    suspend fun loadWeather(): WeatherJSON {
        val editText = findViewById<EditText>(R.id.editText)
        val city = editText.text.toString()
        val apiKey: String = getString(R.string.apikey)
        val weatherURL =
            "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric"
        val stream = URL(weatherURL).content as InputStream
        // JSON отдаётся одной строкой,
        val data = Scanner(stream).nextLine()
        // TODO: предусмотреть обработку ошибок (нет сети, пустой ответ)
        val jsondata = data.trimIndent()
        val newdata: WeatherJSON = Gson().fromJson<WeatherJSON>(jsondata, WeatherJSON::class.java)
        Log.d("mytag", "data: $newdata")
        return newdata
    }

    @DelicateCoroutinesApi
    fun onClick(view: android.view.View) {

        // Используем IO-диспетчер вместо Main (основного потока)
        GlobalScope.launch (Dispatchers.IO) {
            val weatherData: WeatherJSON = loadWeather()
            val cityName: String = weatherData.name
            val weatherMain: String = weatherData.weather[0].main
            val mainTemp: String = weatherData.main.temp
            Log.d("mytag", "cityName: $cityName, weatherMain: $weatherMain, mainTemp: $mainTemp")
            binding.weather = Weather(cityName, weatherMain, mainTemp)
            val icon: String = weatherData.weather[0].icon
            val iconURL = "http://openweathermap.org/img/wn/$icon@2x.png"
            Log.d("mytag", "iconURL: $iconURL")
        }
    }

}

data class Weather(val city_name: String, val weather_main: String, var main_temp: String)

data class WeatherJSON(val coord: Coord, val weather: Array<WeatherArray>, val base: String,
                       val main: WeatherMain, val visibility: Long, val wind: WeatherWind,
                       val clouds: WeatherClouds, val dt: Long, val sys: WeatherSys,
                       val timezone: Long, val id: Long, val name: String, val cod: Int)

data class Coord(val lon: Double, val lat: Double)
data class WeatherArray(val id: Int, val main: String, val description: String, val icon: String)
data class WeatherMain(val temp: String, val feels_like: Double,
                       val temp_min: Double, val temp_max: Double,
                       val pressure: Int, val humidity: Int,
                       val gust: Double = 0.0, val sea_level: Int = 0, val grnd_level:Int = 0)
data class WeatherWind(val speed: String, val deg: Int,
                       val gust: Double = 0.0)
data class WeatherClouds(val all: Int)
data class WeatherSys(val type: Int, val id: Int, val country: String, val sunrise: Long, val sunset: Long)
