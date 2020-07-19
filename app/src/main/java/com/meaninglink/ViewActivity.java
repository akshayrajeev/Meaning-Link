package com.meaninglink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;


public class ViewActivity extends AppCompatActivity {
    TextView tv_result;
    HashMap<String,Word> dictionary;
    AlertDialog.Builder builder;
    String key, input;
    SaveLoad saveLoad;
    RelativeLayout rlOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        saveLoad = new SaveLoad(getApplicationContext());

        rlOverlay = findViewById(R.id.activity_view_overlay);
        if(saveLoad.countNote() == 0) {
            overlay();
        }

        tv_result = findViewById(R.id.activity_view_tv_result);
        tv_result.setMovementMethod(new ScrollingMovementMethod());
        Intent i = getIntent();
        input = i.getStringExtra("input");
        key = i.getStringExtra("key");

        tv_result.setText(input);
        builder = new AlertDialog.Builder(ViewActivity.this);
        builder.setTitle("Meaning");

        builder.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
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
            pd = new ProgressDialog(ViewActivity.this);
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

    private void overlay() {
        rlOverlay.setVisibility(View.VISIBLE);
        Button btnGotit = findViewById(R.id.activity_view_gotit);

        rlOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnGotit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlOverlay.setVisibility(View.GONE);
                invalidateOptionsMenu();
            }
        });
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

        changeStyle(startIndex, endIndex);
        return string.substring(startIndex, endIndex);
    }

    private void changeStyle(int startIndex, int endIndex) {
        SpannableString word = new SpannableString(input);
        word.setSpan(new ForegroundColorSpan(Color.BLUE), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        word.setSpan(new StyleSpan(Typeface.BOLD), startIndex,endIndex,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_result.setText(word);
    }

    private void showDialog(String clickedText) {
        Word word = dictionary.get(clickedText);
        builder.setMessage("Word:\n\t\t\t" + clickedText +"\n\n" +"Phonetic:\n\t\t\t" + word.getPhonetic() +"\n\n\t\t\t" + word.getMeaning());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_menu_edit:
                Intent i = new Intent(getApplicationContext(), EditActivity.class);
                i.putExtra("input", input);
                i.putExtra("key", key);
                startActivity(i);
                finish();
                return true;
            case R.id.view_menu_save:
                saveLoad.remove(key);
                saveLoad.add(new Note(key, input));
                Toast.makeText(getApplicationContext(), "Save Successful!", Toast.LENGTH_LONG).show();
                invalidateOptionsMenu();
                return true;
        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_menu, menu);
        MenuItem saveItem = menu.findItem(R.id.view_menu_save);
        MenuItem editItem = menu.findItem(R.id.view_menu_edit);

        if(rlOverlay.getVisibility() == View.VISIBLE) {
            saveItem.setEnabled(false);
            editItem.setEnabled(false);
            saveItem.getIcon().setAlpha(130);
            editItem.getIcon().setAlpha(130);
        }
        else {
            if(saveLoad.contains(new Note(key,input))) {
                saveItem.setEnabled(false);
                saveItem.getIcon().setAlpha(130);
            }
            else {
                saveItem.getIcon().setAlpha(255);
                editItem.getIcon().setAlpha(255);
            }
        }
        return true;
    }
}