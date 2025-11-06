package com.rostelecom.mitrofanovwidget;

import org.json.JSONObject;
import java.text.DateFormat;
import java.util.Date;

public class StaticWeatherAnalyze {

    public static String getCityField(JSONObject json) {
        try {
            return "ORENBURG, RU";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NaN";
    }

    public static String getLastUpdateTime(JSONObject json) {
        try {
            DateFormat df = DateFormat.getDateTimeInstance();
            return df.format(new Date(json.getLong("now")*1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NaN";
    }

    public static String getDetailsField(JSONObject json) {
        try {
            JSONObject fact = json.getJSONObject("fact");
            String condition = translateCondition(fact.getString("condition"));

            return condition.toUpperCase() +
                    "\nВлажность: " + fact.getString("humidity") + "%" +
                    "\nДавление: " + fact.getString("pressure_mm") + " мм";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NaN";
    }

    public static String getTemperatureField(JSONObject json) {
        try {
            JSONObject fact = json.getJSONObject("fact");
            return String.format("%.2f", fact.getDouble("temp"))+ " °C";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NaN";
    }

    private static String translateCondition(String condition) {
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
}