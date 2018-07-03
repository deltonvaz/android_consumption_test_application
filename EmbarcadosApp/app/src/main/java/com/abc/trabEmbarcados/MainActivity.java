package com.abc.trabEmbarcados;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.abc.trabEmbarcados.R.id.graph0;

/*
* Infos dos alimentos obtidos em https://github.com/raulfdm/taco-api
* */

public class MainActivity extends AppCompatActivity {
    private TextView result;

    ArrayList<Registro> alimentos = new ArrayList<Registro>();
    Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result = (TextView)findViewById(R.id.TVresult);

        //getJson("arroz");
        //getJson("biscoito");
        alimentos.add(new Registro("03/06/2018", "arroz", 1, "100"));
        alimentos.add(new Registro("03/07/2018", "biscoito", 1, "150"));
        alimentos.add(new Registro("03/08/2018", "maçã", 1, "50"));
        alimentos.add(new Registro("03/09/2018", "feijão", 1, "80"));
        alimentos.add(new Registro("03/10/2018", "batata", 1, "70"));
        alimentos.add(new Registro("03/10/2018", "pizza", 1, "200"));
        alimentos.add(new Registro("03/11/2018", "queijo", 1, "200"));
        alimentos.add(new Registro("03/11/2018", "queijo", 1, "10"));
        alimentos.add(new Registro("03/11/2018", "queijo", 1, "40"));
        alimentos.add(new Registro("03/12/2018", "laranja", 1, "40"));
        alimentos.add(new Registro("03/13/2018", "morango", 1, "90"));
    }

    public void getSpeechInput(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale o nome do alimento que comeu");

        try{
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException e){
            Toast.makeText(this, "Seu dispositivo não aceita reconhecimento de voz", Toast.LENGTH_SHORT).show();
        }
//        if(intent.resolveActivity(getPackageManager()) != null){
////            startActivityForResult(intent, 10);
////        }else{
////            Toast.makeText(this, "Seu dispositivo não aceita reconhecimento de voz", Toast.LENGTH_SHORT).show();
////        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 100:
                if(resultCode == RESULT_OK && data != null){
                    ArrayList<String> resultado = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    result.setText(resultado.get(0));
                    //System.out.println(resultado.get(0));
                    getJson(resultado.get(0));
//                    if(resultado.get(0).contains("arroz")){
//                        //Toast.makeText(this, "Comeu arroz", Toast.LENGTH_SHORT).show();
//                        Intent startNewActivity = new Intent(this, DisplayMessageActivity.class);
//                        startActivity(startNewActivity);
//                    }
                }
                break;
        }
    }

    public void showLogs(View view){
        Intent startNewActivity = new Intent(this, LogActivity.class);
        startNewActivity.putExtra("alimentos", alimentos);
        startActivity(startNewActivity);
    }


    public void showGraphs(View view){
        Intent startNewActivity = new Intent(this, GraphActivity.class);
        startNewActivity.putExtra("alimentos", alimentos);
        startActivity(startNewActivity);
    }

    public void getJson(String nomeAlimento){
        String json = null;
        try {
            InputStream is = getAssets().open("alimentos.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                if(obj.getString("descricacao").contains(nomeAlimento)){
                    //alimentos.add(obj.getString("carboidrato"));
                    alimentos.add(new Registro(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(cal.getTime()), nomeAlimento, 1,obj.getString("carboidrato")));
                    break;
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
