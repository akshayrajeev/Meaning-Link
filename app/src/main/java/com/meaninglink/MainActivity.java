package com.meaninglink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class MainActivity extends AppCompatActivity {
    HashMap<String,Word> dictionary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
        String dictionaryString = sharedPreferences.getString("dict", "");
        if(!dictionaryString.equals("")) {
            Type type = new TypeToken<HashMap<String,Word>>(){}.getType();
            dictionary = gson.fromJson(dictionaryString, type);
        }
        else{
            dictionary = new HashMap<>();
        }
        final EditText et_input = findViewById(R.id.activity_main_et_input);
        final TextView result = findViewById(R.id.textView);
        Button btn_find = findViewById(R.id.activity_main_btn_find);

        btn_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = et_input.getText().toString();
                if(temp.equals("")) {
                    Toast.makeText(getApplicationContext(), "No Input", Toast.LENGTH_LONG).show();
                }
                else {
                    String [] words = temp.split(" ");
                    for(String word:words) {
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View widget) {
                                TextView tv = (TextView) widget;
                                Spanned s = (Spanned) tv.getText();
                                int start = s.getSpanStart(this);
                                int end = s.getSpanEnd(this);
                                Log.d("Message", "onClick [" + s.subSequence(start, end) + "]");
                            }
                        };
                        SpannableString spannableString = new SpannableString(word);
                        spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        result.append(spannableString + " ");
                    }
//                    SpannableString spannableString = new SpannableString(temp);
//                    spannableString.setSpan(clickableSpan,0,temp.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    result.setText(spannableString);

//                    temp = temp.replaceAll("\\p{Punct}","");
//                    LinkedHashSet<String>input = new LinkedHashSet<>(Arrays.asList(temp.split(" ")));
                    //new getMeaning().execute(input.toArray(new String[0]));
                }
            }
        });
    }

//    class getMeaning extends AsyncTask<String, Void, Void> {
//
//        @Override
//        protected Void doInBackground(String... strings) {
//            for(String wordString: strings) {
//                String URL = "https://www.google.co.in/search?q=define+";
//                URL += wordString;
//                try {
//                    Word word = new Word();
//                    Document document = Jsoup.connect(URL).get();
//                    Elements phoneticElement = document.select("span[class=XpoqFe]");
//                    Log.i("element", phoneticElement.toString());
//                    String phonetic = phoneticElement.text();
//                    word.setPhonetic(phonetic);
//                    dictionary.put(wordString,word);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            return null;
//        }
//    }
}
