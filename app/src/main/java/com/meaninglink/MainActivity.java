package com.meaninglink;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
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
    SharedPreferences sharedPreferences;
    Gson gson;
    TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadDictionary();

        final EditText et_input = findViewById(R.id.activity_main_et_input);
        final Button btn_find = findViewById(R.id.activity_main_btn_find);
        tv_result = findViewById(R.id.activity_main_tv_result);
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
                        //tv_result.setMovementMethod(new ScrollingMovementMethod());
                    }
                }
            }
        });

        tv_result.setOnTouchListener(new View.OnTouchListener() {
            private long timerStart;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    timerStart = System.currentTimeMillis();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    long duration = System.currentTimeMillis() - timerStart;
                    if (duration < ViewConfiguration.getTapTimeout()) {
                        int mOffset = tv_result.getOffsetForPosition(event.getX(), event.getY());
                        String input = tv_result.getText().toString();
                        try {
                            if (Character.isLetter(input.charAt(mOffset))) {
                                String clickedText = getClickedText(input, mOffset);
                                if(dictionary.containsKey(clickedText)) {
                                    showDialog(clickedText);
                                }
                                else {
                                    new getMeaning().execute(clickedText);
                                }
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

    private class getMeaning extends AsyncTask<String, Void, String> {
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
        protected String doInBackground(final String... strings) {
            URL += strings[0];

            try {
                Word word = new Word();

                Document document = Jsoup.connect(URL).userAgent(USER_AGENT).get();

                //To get the phonetics of the searched word
                Elements phoneticElement = document.select("span[class=XpoqFe]");
                String phonetic = phoneticElement.text();

                //To get the meaning of the searched word
                Elements meaningElement = document.select("div[class=QIclbb XpoqFe]");
                String meaning = meaningElement.text();

                word.setPhonetic(phonetic);
                word.setMeaning(meaning);

                dictionary.put(strings[0], word);

                //saveDictionary();

            }catch (IOException e) {
                e.printStackTrace();
            }
            return strings[0];
        }


        @Override
        protected void onPostExecute(String clickedText) {
            pd.dismiss();
            showDialog(clickedText);
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
            e.printStackTrace();
        }

        if(!Character.isLetter(string.charAt(startIndex))) {
            startIndex++;
        }

        try {
            while((Character.isLetter(string.charAt(endIndex))) || string.charAt(endIndex) == '-' || string.charAt(endIndex) == '\'' || string.charAt(endIndex) == '’') {
                endIndex++;
            }
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        String clickedText = string.substring(startIndex, endIndex);
        changeStyle(clickedText, startIndex, endIndex);
        return clickedText;
    }

    private void changeStyle(String clickedText, int startIndex, int endIndex) {
        String input = tv_result.getText().toString();
        String string = input.substring(0, startIndex);
        string += "<b><font color=blue>" + clickedText + "</font></b>";
        string += input.substring(endIndex);
        tv_result.setText(Html.fromHtml(string));
    }

    private void saveDictionary() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String hashMap = gson.toJson(dictionary);
        editor.putString("dict", hashMap);
        editor.apply();
    }

    private void loadDictionary() {
        gson = new Gson();
        sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
        final String dictionaryString = sharedPreferences.getString("dict", "");
        if(!dictionaryString.equals("")) {
            Type type = new TypeToken<HashMap<String,Word>>(){}.getType();
            dictionary = gson.fromJson(dictionaryString, type);
        }
        else{
            dictionary = new HashMap<>();
        }
    }

    private void showDialog(String clickedText) {
        Word word = dictionary.get(clickedText);
        builder.setMessage("Word:\n\t\t\t" + clickedText +"\n\n" +"Phonetic:\n\t\t\t" + word.getPhonetic() +"\n\n\t\t\t" + word.getMeaning());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
