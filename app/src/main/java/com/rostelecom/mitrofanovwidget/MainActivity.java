package com.rostelecom.mitrofanovwidget;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
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
        setInfo();
    }

    private void setInfo() {
        new ConnectFetch(this, new CityPreference(this).getCity(), new ConnectFetch.OnConnectionCompleteListener() {
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
    }


    public void changeCity(String city) {
        new CityPreference(this).setCity(city);
        setInfo();
    }

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Измените город:");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeCity(input.getText().toString());
            }
        });
        builder.show();
    }

    public void setCity(View view) {
        showInputDialog();
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