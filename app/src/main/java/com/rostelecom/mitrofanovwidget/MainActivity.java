package com.rostelecom.mitrofanovwidget;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import org.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        updateWeatherData("Orenburg");

        Button refreshButton = findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(v -> {
            updateWeatherData("Orenburg");
        });
    }

    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = ConnectFetch.getJSON(MainActivity.this, city);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(MainActivity.this,
                                    city + " - информация не найдена",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json){
        try {
            Glide
                    .with(this)
                    .load(ConnectFetch.getIconUrl(json))
                    .into((ImageView)findViewById(R.id.weather_icon));

            ((TextView)findViewById(R.id.city_field)).setText(StaticWeatherAnalyze.getCityField(json));
            ((TextView)findViewById(R.id.updated_field)).setText(StaticWeatherAnalyze.getLastUpdateTime(json));
            ((TextView)findViewById(R.id.details_field)).setText(StaticWeatherAnalyze.getDetailsField(json));
            ((TextView)findViewById(R.id.current_temperature_field)).setText(StaticWeatherAnalyze.getTemperatureField(json));

        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }
}