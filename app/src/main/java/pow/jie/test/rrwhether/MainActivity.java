package pow.jie.test.rrwhether;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static pow.jie.test.rrwhether.Util.weatherForecastList;
import static pow.jie.test.rrwhether.Util.weatherToday;

public class MainActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView city;
    private TextView upgradeTime;
    private TextView degree;
    private LinearLayout forecastLayout;
    private TextView whetherNow;
    private TextView aqi;
    private TextView suggestion;
    private EditText searchbar;
    private Button searchButton;

    public static final int UPDATE_FRAGMENT = 1;
    public static final int CAN_NOT_FOUND_CITY = 2;
    public static final int CLEAR_EDITTEXT = 3;
    public static final int TIME_OUT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化控件
        weatherLayout = findViewById(R.id.weather_layout);
        city = findViewById(R.id.tv_city);
        upgradeTime = findViewById(R.id.tv_update_time);
        degree = findViewById(R.id.tv_degree);
        forecastLayout = findViewById(R.id.forecast_layout);
        whetherNow = findViewById(R.id.tv_weather_now);
        aqi = findViewById(R.id.tv_aqi);
        suggestion = findViewById(R.id.tv_suggestion);
        searchbar = findViewById(R.id.et_search);
        searchButton = findViewById(R.id.bt_search);

        //创建sp存储城市信息
        SharedPreferences sharedPreferences = this.getSharedPreferences("city", MODE_PRIVATE);
        SharedPreferences sharedPreferences1 = this.getSharedPreferences("weather", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        String weatherString = sharedPreferences1.getString("weather", null);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchbar.getText().equals("")) {
                    Toast.makeText(MainActivity.this, "未输入城市名称", Toast.LENGTH_LONG);
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            editor.putString("city",searchbar.getText().toString());
                            initData();
                        }
                    }).start();
                }
            }
        });
        initData();
    }

    private void initData(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("city", MODE_PRIVATE);
        SharedPreferences sharedPreferences1 = this.getSharedPreferences("weather", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        String weatherString = sharedPreferences1.getString("weather", null);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isFirstRun) {
            editor.putBoolean("isFirstRun", false);
            editor.putString("city", "北京");
            editor.apply();
        }
        if (weatherString != null) {
            Util.handleWeatherResponse(weatherString);
            showWhetherInfo();
        } else {
            //创建http连接
            new Thread(new Runnable() {
                @Override
                public void run() {
//                    String response;
                    SharedPreferences preferences = getSharedPreferences("city", MODE_PRIVATE);
                    String city = preferences.getString("city", "北京");
//                    response = Util.getWebInfo(city);
//                    Util.handleWeatherResponse(response);
//                    showWhetherInfo();
                    String jsonDate = null;
                    try {
                        jsonDate = Util.getWebInfo(city);
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        Message message = new Message();
                        message.what = TIME_OUT;
                        handler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (jsonDate != null) {
                        try {
                            Util.handleWeatherResponse(jsonDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Message message = new Message();
                            message.what = CAN_NOT_FOUND_CITY;
                            handler.sendMessage(message);
                            e.printStackTrace();
                        }
                        Message message = new Message();
                        message.what = UPDATE_FRAGMENT;
                        handler.sendMessage(message);
                    }
                }
            }).start();
        }
    }
    private void showWhetherInfo() {
        String cityValue = weatherToday.getCity();
        String aqiValue = weatherToday.getAqi();
        String temperatureValue = weatherToday.getWendu();
        String suggestionValue = weatherToday.getGanmao();
        city.setText(cityValue);
        aqi.setText(aqiValue);
        degree.setText(temperatureValue);
        suggestion.setText(suggestionValue);
        forecastLayout.removeAllViews();
        whetherNow.setText(weatherForecastList.get(0).getWeatherType());
        for (WeatherForecast weatherForecast : weatherForecastList) {
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.tv_date);
            TextView highText = view.findViewById(R.id.tv_high);
            TextView lowText = view.findViewById(R.id.tv_low);
            TextView typeText = view.findViewById(R.id.tv_type);
            TextView windText = view.findViewById(R.id.tv_wind);
            dateText.setText(weatherForecast.getDate());
            highText.setText(weatherForecast.getHigh());
            lowText.setText(weatherForecast.getLow());
            typeText.setText(weatherForecast.getWeatherType());
            windText.setText(weatherForecast.getWind());
            forecastLayout.addView(view);
        }
        weatherLayout.setVisibility(View.VISIBLE);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == CLEAR_EDITTEXT)
                searchbar.setText("");
            else if (msg.what == TIME_OUT)
                Toast.makeText(MainActivity.this, "请求超时,请检查网络连接", Toast.LENGTH_LONG).show();
        }
    };
}
