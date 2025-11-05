package com.rostelecom.mitrofanovwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import java.util.Arrays;

public class AppWidget extends AppWidgetProvider {
    private static final String LOG_TAG = "YandexWidget";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        String widgetText = "Оренбург\n+4°C, облачно\nЯндекс.Погода";

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        appWidgetManager.updateAppWidget(appWidgetId, views);

        Log.d(LOG_TAG, "Yandex widget updated");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, "onUpdate called");
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        Log.d(LOG_TAG, "onUpdate " + Arrays.toString(appWidgetIds));
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
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