package com.rostelecom.mitrofanovwidget;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ConnectFetch(this, "Orenburg", new ConnectFetch.OnConnectionCompleteListener() {
            @Override
            public void onSuccess(JSONObject response) {
                renderWeather(response);
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(MainActivity.this,
                        message,
                        Toast.LENGTH_LONG).show();
            }
        });

        Button refreshButton = findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(v -> {
            new ConnectFetch(MainActivity.this, "Orenburg", new ConnectFetch.OnConnectionCompleteListener() {
                @Override
                public void onSuccess(JSONObject response) {
                    renderWeather(response);
                }

                @Override
                public void onFail(String message) {
                    Toast.makeText(MainActivity.this,
                            message,
                            Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void renderWeather(JSONObject json) {
        try {
            Glide
                    .with(this)
                    .load(ConnectFetch.getIconUrl(json))
                    .into((ImageView)findViewById(R.id.weather_icon));

            ((TextView)findViewById(R.id.city_field)).setText(StaticWeatherAnalyze.getCityField(json));
            ((TextView)findViewById(R.id.updated_field)).setText(StaticWeatherAnalyze.getLastUpdateTime(json));
            ((TextView)findViewById(R.id.details_field)).setText(StaticWeatherAnalyze.getDetailsField(json));
            ((TextView)findViewById(R.id.current_temperature_field)).setText(StaticWeatherAnalyze.getTemperatureField(json));

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}