package fi.arcada.sos_projekt_chart_sma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView titleText, statsText;
    Button button1, button2;
    String currency, datefrom, dateto, currencyChoice, fromDate, toDate,smaWin;
    SharedPreferences sharedPref;
    SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            currencyChoice = sharedPref.getString("currencyChoice", "EUR");
            fromDate = sharedPref.getString("fromDate", "2022-01-01");
            toDate = sharedPref.getString("toDate", "2022-02-01");
            smaWin = sharedPref.getString("windowSize", "3");

            // TEMPORÄRA VÄRDEN
            currency = currencyChoice;
            datefrom = fromDate;
            dateto = toDate;

            titleText = findViewById(R.id.titleText);
            statsText = findViewById(R.id.statsText);
            button1 = findViewById(R.id.button1);
            button2 = findViewById(R.id.button2);

            titleText.setText("Exchange rate - " + currencyChoice);
            statsText.setText(currency + " | " + fromDate + " - " + toDate);

            // Hämta växelkurser från API
            ArrayList<Double> currencyValues = getCurrencyValues(currency, datefrom, dateto);
            // Skriv ut dem i konsolen
            System.out.println(currencyValues.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void settingsClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    // Färdig metod som hämtar växelkursdata
    public ArrayList<Double> getCurrencyValues(String currency, String from, String to) {

        CurrencyApi api = new CurrencyApi();
        ArrayList<Double> currencyData = null;

        String urlString = String.format("https://api.exchangerate.host/timeseries?start_date=%s&end_date=%s&symbols=%s",
                from.trim(),
                to.trim(),
                currency.trim());

        try {
            String jsonData = api.execute(urlString).get();

            if (jsonData != null) {
                currencyData = api.getCurrencyData(jsonData, currency.trim());
                Toast.makeText(getApplicationContext(), String.format("Hämtade %s valutakursvärden från servern", currencyData.size()), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Kunde inte hämta växelkursdata från servern: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return currencyData;
    }
}