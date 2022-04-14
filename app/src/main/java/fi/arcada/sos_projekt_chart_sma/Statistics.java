package fi.arcada.sos_projekt_chart_sma;

import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;


public class Statistics {

    public static ArrayList<Double> movingAverage (ArrayList<Double> values, int window) {
            ArrayList<Double> ma = new ArrayList<>();

            for (int i = window-1; i < values.size(); i++) {
                double sum = 0.0;

                for (int j = 0; j < window; j++) {
                    sum += values.get(i - j); // t.ex. i:2 blir 2-0, 2-1, 2-2
                }
                ma.add(sum / window); // Medelvärde
            }
            return ma;
        }
    }
