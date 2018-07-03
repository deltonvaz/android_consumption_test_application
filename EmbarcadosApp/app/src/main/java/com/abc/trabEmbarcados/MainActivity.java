package com.abc.trabEmbarcados;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.abc.trabEmbarcados.R.id.graph;

/*
* Infos dos alimentos obtidos em https://github.com/raulfdm/taco-api
* */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Debug";

    ArrayList<Registro> alimentos = new ArrayList<Registro>();
    Calendar cal = Calendar.getInstance();
    String fileName = "meusArquivos";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alimentos = getSavedArrayList();
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 100:
                if(resultCode == RESULT_OK && data != null){
                    ArrayList<String> resultado = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    getJson(resultado.get(0));
                }
                break;
        }
    }

    public void showLogs(View view){
        Intent startNewActivity = new Intent(this, LogActivity.class);
        startNewActivity.putExtra("alimentos", getSavedArrayList());
        startActivity(startNewActivity);
    }


    public void showGraphs(View view){
        Intent startNewActivity = new Intent(this, GraphActivity.class);
        startNewActivity.putExtra("alimentos", getSavedArrayList());
        startActivity(startNewActivity);
    }

    public void getJson(String nomeAlimento){
        String json = null;
        boolean j = false;
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
                if(obj.getString("descricacao").contains(nomeAlimento)) {
                    alimentos.add(new Registro(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(cal.getTime()), nomeAlimento, 1, obj.getString("carboidrato")));
                    saveArrayList(alimentos);
                    j = true;
                    break;
                }
            }
            if(j){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Alimento "+nomeAlimento+" foi adicionado com sucesso!")
                        .setTitle(R.string.dialog_title);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Alimento "+nomeAlimento+" não encontrado na base de dados!")
                        .setTitle("Erro");
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Registro> getSavedArrayList() {
        ArrayList<Registro> savedArrayList = null;
        try {
            FileInputStream inputStream = openFileInput("meusArquivos");
            ObjectInputStream in = new ObjectInputStream(inputStream);
            savedArrayList = (ArrayList<Registro>) in.readObject();
            in.close();
            inputStream.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return savedArrayList;
    }

    public void saveArrayList(ArrayList<Registro> arrayList) {
        try {
            FileOutputStream fileOutputStream = openFileOutput("meusArquivos", Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
            out.writeObject(arrayList);
            out.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
