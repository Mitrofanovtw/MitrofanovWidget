package com.rostelecom.mitrofanovwidget;

import android.content.Context;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectFetch {
    private static final String LOG_TAG = "YandexWeatherAPI";

    private static final String YANDEX_WEATHER_API =
            "https://api.weather.yandex.ru/v2/forecast?lat=%s&lon=%s&extra=true";

    private static final String ORENBURG_LAT = "51.7727";
    private static final String ORENBURG_LON = "55.0988";

    public static JSONObject getJSON(Context context, String city) {
        return getYandexWeatherData(context);
    }

    private static JSONObject getYandexWeatherData(Context context) {
        try {
            String apiKey = context.getString(R.string.yandex_weather_api_key);
            String urlString = String.format(YANDEX_WEATHER_API, ORENBURG_LAT, ORENBURG_LON);

            Log.d(LOG_TAG, "Fetching from Yandex API: " + urlString);

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            connection.setRequestProperty("X-Yandex-API-Key", apiKey);

            int responseCode = connection.getResponseCode();
            Log.d(LOG_TAG, "Yandex Response Code: " + responseCode);

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                reader.close();

                Log.d(LOG_TAG, "Yandex API Success! Response: " + json.toString());

                JSONObject data = new JSONObject(json.toString());
                return data;

            } else {
                Log.e(LOG_TAG, "Yandex HTTP error: " + responseCode + " - " + connection.getResponseMessage());
                return getMockYandexWeatherData();
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "Yandex API Exception: " + e.getMessage());
            return getMockYandexWeatherData();
        }
    }

    private static JSONObject getMockYandexWeatherData() {
        try {
            String mockJson =
                    "{" +
                            "\"now\": 1603845955," +
                            "\"now_dt\": \"2023-10-27T10:45:55Z\"," +
                            "\"info\": {" +
                            "  \"lat\": 51.7727," +
                            "  \"lon\": 55.0988," +
                            "  \"tzinfo\": {" +
                            "    \"name\": \"Europe/Moscow\"," +
                            "    \"offset\": 10800" +
                            "  }," +
                            "  \"def_pressure_mm\": 746," +
                            "  \"url\": \"https://yandex.ru/pogoda/orenburg\"" +
                            "}," +
                            "\"fact\": {" +
                            "  \"temp\": 4," +
                            "  \"feels_like\": 1," +
                            "  \"icon\": \"ovc\"," +
                            "  \"condition\": \"cloudy\"," +
                            "  \"wind_speed\": 3.5," +
                            "  \"wind_dir\": \"w\"," +
                            "  \"pressure_mm\": 745," +
                            "  \"pressure_pa\": 993," +
                            "  \"humidity\": 72," +
                            "  \"daytime\": \"d\"," +
                            "  \"polar\": false," +
                            "  \"season\": \"autumn\"," +
                            "  \"obs_time\": 1603845955" +
                            "}," +
                            "\"forecast\": {" +
                            "  \"sunrise\": \"07:45\"," +
                            "  \"sunset\": \"17:30\"" +
                            "}" +
                            "}";

            Log.w(LOG_TAG, "Using mock Yandex weather data");
            return new JSONObject(mockJson);

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error creating mock Yandex data: " + e.getMessage());
            return null;
        }
    }
}