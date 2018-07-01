package com.abc.trabEmbarcados;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

/*
* Infos dos alimentos obtidos em https://github.com/raulfdm/taco-api
* */

public class MainActivity extends AppCompatActivity {
    private TextView result;
    ArrayList<String> alimentos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result = (TextView)findViewById(R.id.TVresult);
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
                    System.out.println(resultado.get(0));
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
                    alimentos.add(obj.getString("carboidrato"));
                }
            }
            Intent startNewActivity = new Intent(this, DisplayMessageActivity.class);
            startNewActivity.putExtra("infos", alimentos.toString());
            startActivity(startNewActivity);

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
