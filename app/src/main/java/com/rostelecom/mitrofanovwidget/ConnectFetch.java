package com.rostelecom.mitrofanovwidget;

import android.content.Context;
import android.os.Handler;
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
    private static final String OPEN_WEATHER_ICON =
            "https://openweathermap.org/img/wn/%s@2x.png";

    private OnConnectionCompleteListener listener;
    private Handler handler;

    public interface OnConnectionCompleteListener {
        void onSuccess(JSONObject response);
        void onFail(String message);
    }

    public ConnectFetch(Context context, String city, OnConnectionCompleteListener listener) {
        this.listener = listener;
        handler = new Handler();
        updateWeatherData(city, context);
    }

    private void updateWeatherData(final String city, final Context context) {
        new Thread() {
            public void run() {
                final JSONObject json = getJSON(context, city);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            listener.onFail(city + " - информация не найдена");
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            listener.onSuccess(json);
                        }
                    });
                }
            }
        }.start();
    }

    public static JSONObject getJSON(Context context, String city) {
        return getYandexWeatherData(context);
    }

    private static JSONObject getYandexWeatherData(Context context) {
        try {
            String apiKey = context.getString(R.string.yandex_weather_api_key);
            String urlString = String.format(YANDEX_WEATHER_API, ORENBURG_LAT, ORENBURG_LON);

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("X-Yandex-API-Key", apiKey);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                reader.close();
                return new JSONObject(json.toString());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Yandex API Exception: " + e.getMessage());
        }
        return getMockYandexWeatherData();
    }

    public static String getIconUrl(JSONObject json) {
        try {
            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            String icon = details.getString("icon");
            return String.format(OPEN_WEATHER_ICON, icon);
        } catch (Exception e) {
            return String.format(OPEN_WEATHER_ICON, "01d");
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
                            "  \"temp\": -5," +
                            "  \"feels_like\": -8," +
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
            return new JSONObject(mockJson);
        } catch (Exception e) {
            return null;
        }
    }
}