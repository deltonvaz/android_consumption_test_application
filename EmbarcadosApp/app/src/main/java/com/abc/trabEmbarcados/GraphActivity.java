package com.abc.trabEmbarcados;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Date;


public class GraphActivity extends AppCompatActivity {

    private GraphView graph;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_graphs);

        ArrayList<Registro> alimentos = (ArrayList<Registro>) getIntent().getSerializableExtra("alimentos");

        System.out.println(alimentos.toString());

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>();

        GraphView graph = (GraphView) findViewById(R.id.graph);

        int i=0;
        for(Registro entry : alimentos) {
            String name = entry.name;
            Date date = new Date(entry.date);
            Double calories = new Double(entry.calories);
            Integer quantity = entry.quantity;

            System.out.println(date.toString());
            series.appendData(new DataPoint(date, calories), true, 7,true);
            i++;
            if (i>6) {break;}
        }

        graph.addSeries(series);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this.getBaseContext()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(4); // only 4 because of the space

        // set manual y bounds to have nice steps
        graph.getViewport().setMinY(0);
        graph.getViewport().setYAxisBoundsManual(true);

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(true);
    }
}