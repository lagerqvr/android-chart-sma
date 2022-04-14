package fi.arcada.sos_projekt_chart_sma;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;



public class Statistics {

    public static ArrayList<Double> movingAverage (int window, ArrayList<Double> values) {
        // För exemplets skull deklarerar vi allt inom den här metoden, vi behöver inte
        // komma åt textViewMA utanför metoden, och vill hålla resten av koden städad

        // vår datamängd  0   1   2   3
        int[] dataset = {10, 22, 29, 2, 20, 41, 10, 33, 12, 24};

        // ArrayLists för glidande medelvärde
        ArrayList<Integer> ma = new ArrayList<>();
        ArrayList<Integer> betterMa = new ArrayList<>();

        int w = window; // fönsterstorlek

        // Mycket bättre sätt, nu kan vi enkelt använda olika fönsterstorlekar!
        for (int i = window-1; i < dataset.length; i++) {
            // Variabel för summan
            int sum = 0;
            // Inre loop för alla värden som hör till fönstret

            for (int j = 0; j < window; j++) {
                // Gå j steg bakåt och addera det värdet till summan
                sum += dataset[i-j];
            }
            // dividera summan med antalet värden (fönstretsa storlek)
            // Lägg till ArrayListen (sen fortsätter yttre loopen till nästa värde)
            betterMa.add(sum / window);
        }
        return values;
    }
}
