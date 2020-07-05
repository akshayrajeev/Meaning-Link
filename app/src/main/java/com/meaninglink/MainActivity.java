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
import android.view.MotionEvent;
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
                    result.setText(temp);
//                    SpannableString spannableString = new SpannableString(temp);
//                    spannableString.setSpan(clickableSpan,0,temp.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    result.setText(spannableString);

//                    temp = temp.replaceAll("\\p{Punct}","");
//                    LinkedHashSet<String>input = new LinkedHashSet<>(Arrays.asList(temp.split(" ")));
                    //new getMeaning().execute(input.toArray(new String[0]));
                }
            }
        });

        result.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int mOffset = result.getOffsetForPosition(event.getX(), event.getY());
                    //  mTxtOffset.setText("" + mOffset);
                    Toast.makeText(MainActivity.this, findWordForRightHanded(result.getText().toString(), mOffset), Toast.LENGTH_SHORT).show();

                }
                return false;
            }
        });
    }

    private String findWordForRightHanded(String str, int offset) { // when you touch ' ', this method returns left word.
        if (str.length() == offset) {
            offset--; // without this code, you will get exception when touching end of the text
        }

        if (str.charAt(offset) == ' ') {
            offset--;
        }
        int startIndex = offset;
        int endIndex = offset;

        try {
            while (str.charAt(startIndex) != ' ' && str.charAt(startIndex) != '\n') {
                startIndex--;
            }
        } catch (StringIndexOutOfBoundsException e) {
            startIndex = 0;
        }

        try {
            while (str.charAt(endIndex) != ' ' && str.charAt(endIndex) != '\n') {
                endIndex++;
            }
        } catch (StringIndexOutOfBoundsException e) {
            endIndex = str.length();
        }

        // without this code, you will get 'here!' instead of 'here'
        // if you use only english, just check whether this is alphabet,
        // but 'I' use korean, so i use below algorithm to get clean word.
        char last = str.charAt(endIndex - 1);
        if (last == ',' || last == '.' ||
                last == '!' || last == '?' ||
                last == ':' || last == ';') {
            endIndex--;
        }

        return str.substring(startIndex, endIndex);
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
