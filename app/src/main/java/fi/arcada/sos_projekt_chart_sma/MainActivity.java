package fi.arcada.sos_projekt_chart_sma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView statsText;
    LineChart chart;
    Button button1, button2;
    int smaWin;
    String currency, datefrom, dateto, currencyChoice, fromDate, toDate;
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
            smaWin = Integer.parseInt(sharedPref.getString("windowSize", "3"));

            chart = (LineChart) findViewById(R.id.chart);

            // TEMPORÄRA VÄRDEN
            currency = currencyChoice;
            datefrom = fromDate;
            dateto = toDate;

            statsText = findViewById(R.id.statsText);
            button1 = findViewById(R.id.button1);
            button2 = findViewById(R.id.button2);

            statsText.setText(currency + " | " + fromDate + " - " + toDate);

            // Hämta växelkurser från API
            ArrayList<Double> currencyValues = getCurrencyValues(currency, datefrom, dateto);
            // Skriv ut dem i konsolen
            System.out.println(currencyValues.toString());

            createSimpleGraph(currencyValues);

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

    public void createSimpleGraph(ArrayList<Double> dataSet) {
        List<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < dataSet.size(); i++) {
            entries.add(new Entry(i, dataSet.get(i).floatValue()));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "Temperatur");
        LineData lineData = new LineData(lineDataSet);

        chart.setData(lineData);
        chart.invalidate(); // refresh

    }
}