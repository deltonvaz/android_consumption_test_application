package com.abc.trabEmbarcados;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class GraphActivity extends AppCompatActivity {

    private GraphView graph;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_graphs);

        ArrayList<Registro> alimentos = (ArrayList<Registro>) getIntent().getSerializableExtra("alimentos");
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>();
        graph = (GraphView) findViewById(R.id.graph);

        String name; Date date = null; double calories; Integer quantity;
        double i=0, totalCalories = 0;
        Date lastDate = null;
        for(Registro entry : alimentos) {
            name = entry.name;
            date = new Date(entry.date);
            calories = new Double(entry.calories);
            quantity = entry.quantity;

            if (lastDate == null || lastDate.getDay() == date.getDay()) {
                totalCalories = calories + totalCalories;
                series.appendData(new DataPoint(date, totalCalories), true, 99, true);
            } else {
                series.appendData(new DataPoint(date, calories), true, 99, true);
                totalCalories = calories;
                i++;
                if (i >= 6) {break;}
            }
            lastDate = date;
        }

        series.setDrawValuesOnTop(true);
        series.setSpacing(10);

        graph.addSeries(series);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this.getBaseContext(), new SimpleDateFormat("dd")));
        //graph.getGridLabelRenderer().setNumHorizontalLabels(5); // only 4 because of the space

        graph.getViewport().setMinY(0);
        graph.getViewport().setYAxisBoundsManual(true);

        graph.getGridLabelRenderer().setHumanRounding(true);


    }


}