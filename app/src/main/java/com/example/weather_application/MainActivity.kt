package com.example.weather_application

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    var City: String = "Chernihiv, UA"
    val API = BuildConfig.WEATHER_API

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.search_button).setOnClickListener {
            City = findViewById<EditText>(R.id.location).text.toString()
            weatherTask().execute()
        }
        weatherTask().execute()
    }

    inner class weatherTask():AsyncTask<String, Void, String>()
    {

        override fun doInBackground(vararg p0: String?): String? {
            var response:String?
            try {
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$City&units=metric&appid=$API").readText(
                    Charsets.UTF_8
                )
            }
            catch (e: Exception){
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val JSONObj = JSONObject(result)
                val main = JSONObj.getJSONObject("main")
                val sys = JSONObj.getJSONObject("sys")

                val wind = JSONObj.getJSONObject("wind")
                val weather = JSONObj.getJSONArray("weather").getJSONObject(0)
                val updatedAt:Long = JSONObj.getLong("dt")
                val updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(Date(updatedAt*1000))
                val temperature = main.getString("temp")+"°C"
                val temperatureMin = "Min Temp: " + main.getString("temp_min")+"°C"
                val temperatureMax = "Max Temp: " + main.getString("temp_max")+"°C"
                val pressure = main.getString("pressure") + " hPa"
                val humidity = main.getString("humidity") + '%'
                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed") + " m/s"

                val weatherDescription = weather.getString("description")
                val address = JSONObj.getString("name")+", "+sys.getString("country")

                // Filling the information
                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.lastUpdate).text =  updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temperature).text = temperature
                findViewById<TextView>(R.id.temperature_min).text = temperatureMin
                findViewById<TextView>(R.id.temperature_max).text = temperatureMax
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(sunrise*1000))
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(sunset*1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity

                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
            }
            catch (e: Exception) {
                val toast: Toast = Toast.makeText(applicationContext, "Something went wrong...", 5000)
                toast.show()
            }
        }
    }
}