package com.vinayakgardi.weatherapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import com.intuit.sdp.BuildConfig
import com.vinayakgardi.weatherapplication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        searchCity()


    }
    private fun searchCity(){
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
               if(query!=null){
                   fetchWeatherData(query)
               }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(city : String) {
         val retrofit = Retrofit.Builder()
             .addConverterFactory(GsonConverterFactory.create())
             .baseUrl("https://api.openweathermap.org/data/2.5/").build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(city,"04607e7081f134cd5250b3055a3f1c71","metric")

        response.enqueue(object  : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
             val responseBody  = response.body()
                if(response.isSuccessful ){
                    val temperature = responseBody?.main?.temp.toString()
                    val max_temperature = responseBody?.main?.temp_max.toString()
                    val min_temperature = responseBody?.main?.temp_min.toString()
                    val humidity = responseBody?.main?.humidity.toString()
                    val windspeed = responseBody?.wind?.speed.toString()
                    val sealevel = responseBody?.main?.pressure.toString()
                    val sunset = responseBody?.sys?.sunset.toString()
                    val sunrise = responseBody?.sys?.sunrise
                    val condition = responseBody?.weather?.firstOrNull()?.main?: "unknown"



                    binding.txtTemperature.text ="$temperature °C"
                    binding.txtMaxTemperature.text = "Max - $max_temperature °C"
                    binding.txtMinTemperature.text = "Min - $min_temperature °C"
                    binding.txtConditions.text = "$condition"
                    binding.climaticCondition.text = "$condition"
                    binding.txtHumidity.text = "$humidity %"
                    binding.txtWindspeed.text = "$windspeed m/s"
                    binding.txtSunrise.text = "$sunrise"
                    binding.txtSunset.text = "$sunset"
                    binding.txtSea.text = "$sealevel hPa"
                    binding.txtCity.text = city
                    binding.txtDate.text = date(System.currentTimeMillis())
                    binding.txtDay.text = day(System.currentTimeMillis())


                    changeBackgroundCondition(condition)


                }
            }


            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }

        })
    }

    private fun changeBackgroundCondition(condition : String) {
         when(condition){
             "Clear Sky","Sunny","Clear" ->{
                 binding.root.setBackgroundResource(R.drawable.sunny_background)
                 binding.animateIconWeather.setAnimation(R.raw.sun)
             }

             "Partial Clouds","Clouds","Overcast","Mist","Foggy" ->{
                 binding.root.setBackgroundResource(R.drawable.cloud_background)
                 binding.animateIconWeather.setAnimation(R.raw.cloud)
             }

             "Showers","Light Rain","Drizzle","Moderate Rain","Heavy Rain" ->{
                 binding.root.setBackgroundResource(R.drawable.rain_background)
                 binding.animateIconWeather.setAnimation(R.raw.rain)
             }

             "Light Snow", "Heavy Snow", "Moderate Snow","Blizzard" ->{
                 binding.root.setBackgroundResource(R.drawable.snow_background)
                 binding.animateIconWeather.setAnimation(R.raw.snow)
             }

         }
        binding.animateIconWeather.playAnimation()

    }

    private fun date(currentTimeMillis: Long): String {
         val sdf = SimpleDateFormat("dd/MM/YY", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun day(currentTimeMillis: Long) : String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }


}