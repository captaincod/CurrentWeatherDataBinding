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
import android.widget.EditText
import android.widget.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.FileNotFoundException

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var icon_view: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.weather = Weather("City name", "Weather", "Temperature")
        icon_view = findViewById(R.id.icon_view)
    }

    fun loadWeather() {
        val editText = findViewById<EditText>(R.id.editText)
        val city = editText.text.toString()
        val apiKey: String = getString(R.string.apikey)
        val weatherURL =
            "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric"
        lateinit var cityName: String
        lateinit var weatherMain: String
        lateinit var mainTemp: String
        lateinit var icon: String

        try{
            val stream = URL(weatherURL).content as InputStream
            val data = Scanner(stream).nextLine()
            val jsondata = data.trimIndent()
            val weatherData: WeatherJSON = Gson().fromJson<WeatherJSON>(jsondata, WeatherJSON::class.java)
            Log.d("mytag", "data: $weatherData")
            cityName = weatherData.name
            weatherMain = weatherData.weather[0].main
            mainTemp = weatherData.main.temp
            icon = weatherData.weather[0].icon
            binding.weather = Weather(cityName, weatherMain, mainTemp)
            Log.d("mytag", icon)
            this@MainActivity.runOnUiThread {
                when(icon){
                    "01d" -> icon_view.setImageResource(R.drawable.icon01d)
                    "02d" -> icon_view.setImageResource(R.drawable.icon02d)
                    "03d" -> icon_view.setImageResource(R.drawable.icon03d)
                    "04d" -> icon_view.setImageResource(R.drawable.icon04d)
                    "09d" -> icon_view.setImageResource(R.drawable.icon09d)
                    "10d" -> icon_view.setImageResource(R.drawable.icon10d)
                    "11d" -> icon_view.setImageResource(R.drawable.icon11d)
                    "13d" -> icon_view.setImageResource(R.drawable.icon13d)
                    "50d" -> icon_view.setImageResource(R.drawable.icon50d)
                    "01n" -> icon_view.setImageResource(R.drawable.icon01n)
                    "02n" -> icon_view.setImageResource(R.drawable.icon02n)
                    "03n" -> icon_view.setImageResource(R.drawable.icon03n)
                    "04n" -> icon_view.setImageResource(R.drawable.icon04n)
                    "09n" -> icon_view.setImageResource(R.drawable.icon09n)
                    "10n" -> icon_view.setImageResource(R.drawable.icon10n)
                    "11n" -> icon_view.setImageResource(R.drawable.icon11n)
                    "13n" -> icon_view.setImageResource(R.drawable.icon13n)
                    "50n" -> icon_view.setImageResource(R.drawable.icon50n)
                }
            }
        } catch (e: FileNotFoundException) {
            this@MainActivity.runOnUiThread {
                Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @DelicateCoroutinesApi
    fun onClick(view: android.view.View) {
        GlobalScope.launch (Dispatchers.IO) {
            loadWeather()
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
