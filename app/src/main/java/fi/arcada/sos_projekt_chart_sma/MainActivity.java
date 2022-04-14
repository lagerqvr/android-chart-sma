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
    ArrayList<Double> sma10, sma30, sma;
    LineChart chart;
    Button button1, button2;
    int smaWin;
    boolean s10 = true;
    boolean s30 = true;
    String currency, datefrom, dateto, currencyChoice, fromDate, toDate;
    SharedPreferences sharedPref;
    SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            currencyChoice = sharedPref.getString("currencyChoice", "SEK");
            fromDate = sharedPref.getString("fromDate", "2022-01-01");
            toDate = sharedPref.getString("toDate", "2022-04-01");
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
            System.out.println(Statistics.movingAverage(currencyValues, 10));
            System.out.println(Statistics.movingAverage(currencyValues, 30));

            sma = Statistics.movingAverage(currencyValues, smaWin);
            sma10 = Statistics.movingAverage(currencyValues, 10);
            sma30 = Statistics.movingAverage(currencyValues, 30);

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
                // Toast.makeText(getApplicationContext(), String.format("Fetched %s values from the server", currencyData.size()), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Couldn't fetch values from server: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return currencyData;
    }

    public void createSimpleGraph(ArrayList<Double> dataSet) {
        List<Entry> entries = new ArrayList<Entry>();
        ArrayList<Entry> SMA10 = new ArrayList<Entry>();
        ArrayList<Entry> SMA30 = new ArrayList<Entry>();

        for (int i = 0; i < dataSet.size(); i++) {
            entries.add(new Entry(i, dataSet.get(i).floatValue()));
        }
        for (int i = 0; i < sma10.size(); i++) {
            SMA10.add(new Entry(i, sma10.get(i).floatValue()));
        }
        for (int i = 0; i < sma30.size(); i++) {
            SMA30.add(new Entry(i, sma30.get(i).floatValue()));
        }

        // Chart data
        LineData lineData = new LineData();

        // Line 1
        LineDataSet lineDataSet = new LineDataSet(entries, currencyChoice);
        lineDataSet.setColor(getResources().getColor(R.color.blue));
        lineDataSet.setCircleColor(getResources().getColor(R.color.blue));
        lineData.addDataSet(lineDataSet);

        // Line 2
        LineDataSet lineDataSet10 = new LineDataSet(SMA10, "SMA10");
        if (s10) {
            lineDataSet10.setColor(getResources().getColor(R.color.red));
            lineDataSet10.setCircleColor(getResources().getColor(R.color.red));
            lineData.addDataSet(lineDataSet10);
        } else {
            lineDataSet10.setVisible(false);
        }

        // Line 3
        LineDataSet lineDataSet30 = new LineDataSet(SMA30, "SMA30");
        if (s30) {
            lineDataSet30.setColor(getResources().getColor(R.color.green));
            lineDataSet30.setCircleColor(getResources().getColor(R.color.green));
            lineData.addDataSet(lineDataSet30);
        } else {
            lineDataSet30.setVisible(false);
        }

        // Set data
        chart.setData(lineData);
        chart.invalidate(); // Refresh
    }

    public void s10Click(View view) {
        ArrayList<Double> currencyValues = getCurrencyValues(currency, datefrom, dateto);
        if (s10) {
            s10 = false;
        } else {
            s10 = true;
        }
        createSimpleGraph(currencyValues);
        chart.invalidate(); // redraw
    }

    public void s30Click(View view) {
        ArrayList<Double> currencyValues = getCurrencyValues(currency, datefrom, dateto);
        if (s30) {
            s30 = false;
        } else {
            s30 = true;
        }
        createSimpleGraph(currencyValues);
        chart.invalidate(); // redraw
    }
}