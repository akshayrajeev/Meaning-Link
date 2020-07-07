package com.meaninglink;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
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
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    HashMap<String,Word> dictionary;
    AlertDialog.Builder builder;

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
        final TextView tv_result = findViewById(R.id.activity_main_tv_result);
        final Button btn_find = findViewById(R.id.activity_main_btn_find);
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Meaning");

        builder.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        btn_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(btn_find.getText().equals("Edit")) {
                    btn_find.setText(R.string.find);
                    tv_result.setVisibility(View.GONE);
                    et_input.setVisibility(View.VISIBLE);
                }
                else {
                    String temp = et_input.getText().toString();
                    if(temp.equals("")) {
                        Toast.makeText(getApplicationContext(), "No Input", Toast.LENGTH_LONG).show();
                    }
                    else {
                        inputMethodManager.hideSoftInputFromWindow(tv_result.getWindowToken(), 0);
                        btn_find.setText(R.string.edit);
                        et_input.setVisibility(View.GONE);
                        tv_result.setVisibility(View.VISIBLE);
                        tv_result.setText(temp);
                        tv_result.setMovementMethod(new ScrollingMovementMethod());
                    }
                }
            }
        });

        tv_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tv_result.setOnTouchListener(new View.OnTouchListener() {
            private static final int MAX_CLICK_DURATION = 100;
            private long timerStart;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    timerStart = System.currentTimeMillis();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    long duration = System.currentTimeMillis() - timerStart;
                    if (duration < MAX_CLICK_DURATION) {
                        int mOffset = tv_result.getOffsetForPosition(event.getX(), event.getY());
                        String input = tv_result.getText().toString();
                        try {
                            if (Character.isLetter(input.charAt(mOffset))) {
                                String clickedText = getClickedText(input, mOffset);
                                new getMeaning().execute(clickedText);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                return false;
            }
        });
    }

    private class getMeaning extends AsyncTask<String, Void, Void> {
        ProgressDialog pd;
        String URL = "https://www.google.com/search?q=define+";
        public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(final String... strings) {
            URL += strings[0];
            try {
                Word word = new Word();
                Document document = Jsoup.connect(URL).userAgent(USER_AGENT).get();
                Elements phoneticElement = document.select("span[class=XpoqFe]");
                String phonetic = phoneticElement.text();
                word.setPhonetic(phonetic);
                dictionary.put(strings[0], word);
                builder.setMessage("Phonetic:\n\t\t\t" + phonetic);
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private String getClickedText(String string, int offset) {

        int startIndex = offset;
        int endIndex = offset;

        try {
            while(Character.isLetter(string.charAt(startIndex)) || string.charAt(startIndex) == '-' || string.charAt(startIndex) == '\'' || string.charAt(startIndex) == '’') {
                startIndex--;
            }
        } catch (StringIndexOutOfBoundsException e) {
            startIndex = 0;
        }

        if(!Character.isLetter(string.charAt(startIndex))) {
            startIndex++;
        }

        try {
            while((Character.isLetter(string.charAt(endIndex))) || string.charAt(endIndex) == '-' || string.charAt(endIndex) == '\'' || string.charAt(endIndex) == '’') {
                endIndex++;
            }
        } catch (StringIndexOutOfBoundsException e) {

        }

        return string.substring(startIndex, endIndex);
    }
}
