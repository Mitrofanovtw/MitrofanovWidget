package com.rostelecom.mitrofanovwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import org.json.JSONObject;
import java.util.Arrays;

public class AppWidget extends AppWidgetProvider {
    final String LOG_TAG = "myLogs";

    static void updateAppWidget(final Context context, SharedPreferences sharedPreferences,
                                AppWidgetManager appWidgetManager, final int appWidgetId) {
        String widgetCity = sharedPreferences.getString(ConfigActivity.WIDGET_CITY + appWidgetId, "Orenburg");
        Log.d("WidgetDebug", "Updating widget " + appWidgetId + " for city: " + widgetCity);

        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        remoteViews.setTextViewText(R.id.city_field, widgetCity);

        // Устанавливаем временные данные пока загружаются
        remoteViews.setTextViewText(R.id.details_field, "Загрузка...");

        new ConnectFetch(context, widgetCity, new ConnectFetch.OnConnectionCompleteListener() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d("WidgetDebug", "Weather data received for widget");
                renderWeather(response, context, remoteViews, appWidgetId);
            }

            @Override
            public void onFail(String message) {
                Log.e("WidgetDebug", "Failed to load weather: " + message);
                remoteViews.setTextViewText(R.id.details_field, "Ошибка");
                remoteViews.setTextViewText(R.id.city_field, widgetCity);
                pushWidgetUpdate(context, appWidgetManager, appWidgetId, remoteViews);
            }
        });

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    public static void renderWeather(JSONObject json, Context context, RemoteViews remoteViews, int appWidgetId) {
        try {
            String temperature = StaticWeatherAnalyze.getTemperatureField(json);
            String city = "Orenburg"; // Статическое значение для теста

            Log.d("WidgetDebug", "Setting temperature: " + temperature + ", city: " + city);

            remoteViews.setTextViewText(R.id.details_field, temperature);
            remoteViews.setTextViewText(R.id.city_field, city);

            // Пробуем загрузить иконку
            try {
                AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context, R.id.weather_icon, remoteViews, appWidgetId);
                Glide.with(context.getApplicationContext())
                        .asBitmap()
                        .load(ConnectFetch.getIconUrl(json))
                        .into(appWidgetTarget);
            } catch (Exception e) {
                Log.e("WidgetDebug", "Error loading icon: " + e.getMessage());
                // Устанавливаем стандартную иконку при ошибке
                remoteViews.setImageViewResource(R.id.weather_icon, android.R.drawable.ic_menu_report_image);
            }

            pushWidgetUpdate(context, AppWidgetManager.getInstance(context), appWidgetId, remoteViews);

        } catch(Exception e) {
            Log.e("WidgetDebug", "Error in renderWeather: " + e.getMessage());
            remoteViews.setTextViewText(R.id.details_field, "Ошибка данных");
            pushWidgetUpdate(context, AppWidgetManager.getInstance(context), appWidgetId, remoteViews);
        }
    }

    public static void pushWidgetUpdate(Context context, AppWidgetManager appWidgetManager, int appWidgetId, RemoteViews remoteViews) {
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        Log.d("WidgetDebug", "Widget " + appWidgetId + " updated");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, "onUpdate called");
        SharedPreferences sp = context.getSharedPreferences(ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, sp, appWidgetManager, appWidgetId);
        }
        Log.d(LOG_TAG, "onUpdate " + Arrays.toString(appWidgetIds));
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        SharedPreferences.Editor editor = context.getSharedPreferences(ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE).edit();
        for (int widgetID : appWidgetIds) {
            editor.remove(ConfigActivity.WIDGET_CITY + widgetID);
        }
        editor.apply();
        Log.d(LOG_TAG, "onDeleted " + Arrays.toString(appWidgetIds));
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(LOG_TAG, "onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(LOG_TAG, "onDisabled");
    }
}