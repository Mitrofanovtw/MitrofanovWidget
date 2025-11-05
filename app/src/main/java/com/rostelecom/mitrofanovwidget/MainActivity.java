package com.rostelecom.mitrofanovwidget;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    private Handler handler;
    private TextView weatherTextView;
    private Button refreshButton;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "onCreate started");

        handler = new Handler();
        executorService = Executors.newSingleThreadExecutor();

        weatherTextView = findViewById(R.id.weather);
        refreshButton = findViewById(R.id.refresh_button);

        String apiKey = getString(R.string.yandex_weather_api_key);
        if (apiKey.equals("ВАШ_ЯНДЕКС_КЛЮЧ") || apiKey.isEmpty()) {
            weatherTextView.setText("Установите Яндекс API ключ\n\nПолучите на yandex.ru/dev/weather");
        } else {
            updateWeatherData("Orenburg");
        }

        refreshButton.setOnClickListener(v -> {
            Log.d(LOG_TAG, "Refresh button clicked");
            updateWeatherData("Orenburg");
        });
    }

    private void updateWeatherData(final String city) {
        Log.d(LOG_TAG, "updateWeatherData for city: " + city);

        weatherTextView.setText("Загрузка данных...");

        executorService.execute(() -> {
            Log.d(LOG_TAG, "Background thread started");
            final JSONObject json = ConnectFetch.getJSON(MainActivity.this, city);

            handler.post(() -> {
                if (json == null) {
                    Log.e(LOG_TAG, "Failed to get weather data");
                    weatherTextView.setText("Ошибка загрузки\nПроверьте интернет и API ключ");
                    Toast.makeText(MainActivity.this,
                            "Ошибка загрузки данных",
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.d(LOG_TAG, "Successfully received weather data");
                    renderYandexWeather(json);
                    Toast.makeText(MainActivity.this,
                            "Данные обновлены",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void renderYandexWeather(JSONObject json) {
        try {
            Log.d(LOG_TAG, "renderYandexWeather started");

            JSONObject info = json.getJSONObject("info");
            double lat = info.getDouble("lat");
            double lon = info.getDouble("lon");

            JSONObject fact = json.getJSONObject("fact");
            int temp = fact.getInt("temp");
            int feelsLike = fact.getInt("feels_like");
            String condition = fact.getString("condition");
            String icon = fact.getString("icon");
            double windSpeed = fact.getDouble("wind_speed");
            String windDir = fact.getString("wind_dir");
            int pressureMm = fact.getInt("pressure_mm");
            int humidity = fact.getInt("humidity");

            JSONObject forecast = json.getJSONObject("forecast");
            String sunrise = forecast.getString("sunrise");
            String sunset = forecast.getString("sunset");

            String updatedOn = DateFormat.getDateTimeInstance().format(new Date());

            String conditionRu = translateCondition(condition);
            String windDirRu = translateWindDir(windDir);

            String weatherText = String.format(
                    "Оренбург\n" +
                            "Температура: %d°C\n" +
                            "Ощущается как: %d°C\n" +
                            "Погода: %s\n" +
                            "Ветер: %.1f м/с, %s\n" +
                            "Давление: %d мм рт.ст.\n" +
                            "Влажность: %d%%\n" +
                            "Восход: %s\n" +
                            "Закат: %s\n" +
                            "Обновлено: %s\n\n" +
                            "Данные: Яндекс.Погода",
                    temp, feelsLike, conditionRu, windSpeed, windDirRu,
                    pressureMm, humidity, sunrise, sunset, updatedOn
            );

            weatherTextView.setText(weatherText);
            Log.d(LOG_TAG, "Yandex weather data displayed successfully");

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error in renderYandexWeather: " + e.getMessage());
            weatherTextView.setText("Ошибка обработки данных\n\nПроверьте API ключ");
            e.printStackTrace();
        }
    }

    private String translateCondition(String condition) {
        switch (condition) {
            case "clear": return "ясно";
            case "partly-cloudy": return "малооблачно";
            case "cloudy": return "облачно с прояснениями";
            case "overcast": return "пасмурно";
            case "light-rain": return "небольшой дождь";
            case "rain": return "дождь";
            case "heavy-rain": return "сильный дождь";
            case "showers": return "ливень";
            case "wet-snow": return "дождь со снегом";
            case "light-snow": return "небольшой снег";
            case "snow": return "снег";
            case "snow-showers": return "снегопад";
            case "hail": return "град";
            case "thunderstorm": return "гроза";
            case "thunderstorm-with-rain": return "дождь с грозой";
            case "thunderstorm-with-hail": return "гроза с градом";
            default: return condition;
        }
    }

    private String translateWindDir(String windDir) {
        switch (windDir) {
            case "nw": return "северо-западный";
            case "n": return "северный";
            case "ne": return "северо-восточный";
            case "e": return "восточный";
            case "se": return "юго-восточный";
            case "s": return "южный";
            case "sw": return "юго-западный";
            case "w": return "западный";
            case "c": return "штиль";
            default: return windDir;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}