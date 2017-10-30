package com.example.fanxh.simpleweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fanxh.simpleweather.gson.Daily_forecast;
import com.example.fanxh.simpleweather.gson.Weather;
import com.example.fanxh.simpleweather.util.HttpUtil;
import com.example.fanxh.simpleweather.util.Utility;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ImageView mNowWeather;
    private Button mChooseArea;


//    private ScrollView mWeatherLayout;
    private TextView mTitleCity;

    private TextView mTitleNowCond;
    private TextView mTitleNowDegree;


    private TextView mTitleUpdateTime;
    private TextView mNowDegreeTem;
    private TextView mNowCondTxt;
    private TextView mAqiCityAqi;
    private TextView mAqiCitypm25;

    private LinearLayout mDailyForecast;
    private TextView mSuggestionAirTxt;
    private TextView mSuggestionComfTxt;
    private TextView mSuggestionCwTxt;
    private TextView mSuggestionSportTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        mChooseArea = (Button) findViewById(R.id.choose_area);
        mChooseArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this,ChangeArea.class);
                startActivity(intent);
            }
        });


//        mWeatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        mTitleNowCond = (TextView)findViewById(R.id.title_now_cond);
        mTitleNowDegree = (TextView)findViewById(R.id.title_now_degree);

        mNowWeather = (ImageView) findViewById(R.id.now_weather);
        mTitleCity = (TextView)findViewById(R.id.title_city);
 //       mTitleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        mNowDegreeTem = (TextView)findViewById(R.id.now_degree_tmp);
        mNowCondTxt = (TextView)findViewById(R.id.now_cond_txt);
        mAqiCityAqi = (TextView)findViewById(R.id.aqi_city_aqi);
        mAqiCitypm25 = (TextView)findViewById(R.id.aqi_city_pm25);

        mDailyForecast = (LinearLayout) findViewById(R.id.daily_forecast_item);
        mSuggestionAirTxt = (TextView) findViewById(R.id.suggestion_air_txt);
        mSuggestionComfTxt = (TextView)findViewById(R.id.suggestion_comf_txt);
        mSuggestionCwTxt = (TextView)findViewById(R.id.suggestion_cw_txt);
        mSuggestionSportTxt = (TextView)findViewById(R.id.suggestion_sport_txt);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        String weatherIdString = prefs.getString("weatherId",null);
        String weatherId = getIntent().getStringExtra("weather_id");
        if (TextUtils.isEmpty(weatherId) && weatherString != null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
           showWeatherInformation(weather);
        }else if (!TextUtils.isEmpty(weatherId) && weatherString !=null && weatherId.equals(weatherIdString)){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInformation(weather);
        } else {
//            String weatherId = getIntent().getStringExtra("weather_id");
//            mWeatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
    }

    public void requestWeather(final String weatherId){
        String weatherUrl = "https://free-api.heweather.com/v5/weather?city="+
                weatherId+"&key=168d59faf85840c0b262b671067367e1";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.clear();
                            editor.putString("weather",responseText);
                            editor.putString("weatherId",weatherId);
                            editor.apply();
                            showWeatherInformation(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
           }
        });
    }
    public void showWeatherInformation(Weather weather){

         mTitleCity.setText(weather.basic.city);

         mTitleNowCond.setText(weather.now.cond.txt);
         mTitleNowDegree.setText(weather.now.tmp+"°");

 //        mTitleUpdateTime.setText(weather.basic.update.loc);
         mNowDegreeTem.setText(weather.now.tmp+"℃");
         mNowCondTxt.setText(weather.now.cond.txt);
         switch (weather.now.cond.txt){

             case "晴":
                 mNowWeather.setImageResource(R.drawable.i_sun);
                 break;
             case "阴" :
                 mNowWeather.setImageResource(R.drawable.i_overcast);
                 break;
             case "多云" :
                 mNowWeather.setImageResource(R.drawable.i_cloudy);
                 break;
             case "小雨":
                 mNowWeather.setImageResource(R.drawable.i_light_rain);
                 break;
             case "中雨":
                 mNowWeather.setImageResource(R.drawable.i_moderate_rain);
                 break;
             case "大雨":
                 mNowWeather.setImageResource(R.drawable.i_heavy_rain);
                 break;
             case "阵雨":
                 mNowWeather.setImageResource(R.drawable.i_shower_rain);
                 break;
             case "雷阵雨":
                 mNowWeather.setImageResource(R.drawable.i_thundershower);
                 break;
             case "小雪":
                 mNowWeather.setImageResource(R.drawable.i_light_snow);
                 break;
                 default:
         }
         if (!TextUtils.isEmpty(weather.aqi.city.aqi) && !TextUtils.isEmpty(weather.aqi.city.pm25)){
         mAqiCityAqi.setText("AQI:" + weather.aqi.city.aqi);
         mAqiCitypm25.setText("PM2.5:" + weather.aqi.city.pm25);
         }

         mDailyForecast.removeAllViews();
         for (Daily_forecast mDaily_forecast : weather.daily_forecast){
             View view = LayoutInflater.from(this).inflate(R.layout.daily_forecast_item,mDailyForecast,false);
             TextView mDailyDate = (TextView)view.findViewById(R.id.daily_date);
             TextView mDailyCondTxtD = (TextView)view.findViewById(R.id.daily_cond_txt_d);
             TextView mDailyWindDir = (TextView)view.findViewById(R.id.daily_wind_dir);
             TextView mDailyTmp = (TextView)view.findViewById(R.id.daily_tmp_min_max);
             String mDailyCondTxtN = mDaily_forecast.cond.txt_n;
             String mDailyTmpMax = mDaily_forecast.tmp.max;
             mDailyDate.setText(mDaily_forecast.date);
             if (mDailyCondTxtN.equals(mDaily_forecast.cond.txt_d)){
                 mDailyCondTxtD.setText(mDaily_forecast.cond.txt_d);
             }else {
                 mDailyCondTxtD.setText(mDaily_forecast.cond.txt_d+" 转 "+mDailyCondTxtN);
             }
             mDailyWindDir.setText(mDaily_forecast.wind.dir);
             mDailyTmp.setText(mDaily_forecast.tmp.min+"℃"+"～"+mDailyTmpMax+"℃");
             mDailyForecast.addView(view);

         }
         mSuggestionAirTxt.setText("空气质量：" + weather.suggestion.air.txt);
         mSuggestionComfTxt.setText("舒适度：" + weather.suggestion.comf.txt);
         mSuggestionCwTxt.setText("洗车指数：" + weather.suggestion.cw.txt);
         mSuggestionSportTxt.setText("运动指数：" + weather.suggestion.sport.txt);
 //       mWeatherLayout.setVisibility(View.INVISIBLE);
    }
}
