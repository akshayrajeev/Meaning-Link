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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ViewActivity extends AppCompatActivity {
    TextView tv_result;
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
                                if(saveLoad.contains(clickedText.toLowerCase())) {
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

    private class getMeaning extends AsyncTask<String, Void, Boolean> {
        ProgressDialog pd;
        String searchString = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ViewActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            searchString = strings[0];
            try {
                if(scrape(searchString)) {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean isNull) {
            if(isNull) {
                Toast.makeText(getApplicationContext(), "No Such Word Found", Toast.LENGTH_LONG).show();
            }
            else {
                showDialog(searchString);
            }
            pd.dismiss();
        }
    }

    String handleDictionaryCodes(String sentence) {
        sentence = sentence.replace("[ T ]","[Transitive Verb]");
        sentence = sentence.replace("[ I ]","[Intransitive Verb]");
        sentence = sentence.replace("[ L ]", "[Linking Verb]");
        sentence = sentence.replace("[ C ]", "[Countable Noun]");
        sentence = sentence.replace("[ U ]", "[Uncountable Noun]");
        return sentence;
    }

    private boolean scrape(String searchString) throws IOException {
        String URL = "https://dictionary.cambridge.org/us/dictionary/english/" + searchString;

        Word word = new Word();

        Document document = Jsoup.connect(URL).get();
        Elements sections = document.select("div[class=pr dsense]");
        if(sections.size() == 0) {
            sections = document.select("div[class=pr entry-body__el]");
            for(Element section : sections) {
                Elements headings = section.select("span[class=pos dpos]");
                String stringHeading = "";
                if(headings.size() > 0) {
                    for(Element heading : headings) {
                        String temp = heading.text();
                        temp = temp.substring(0,1).toUpperCase() + temp.substring(1);
                        stringHeading += temp + "/";
                    }
                    stringHeading = stringHeading.substring(0,stringHeading.length()-1);
                }
                else if(headings.size() == 0) {
                    stringHeading = "Short Form";
                }
                else {
                    stringHeading = headings.text();
                    stringHeading = stringHeading.substring(0,1).toUpperCase() + stringHeading.substring(1);
                }
                String stringMeaning = section.select("div[class=def ddef_d db]").text();
                if(stringMeaning.endsWith(":")) {
                    stringMeaning = stringMeaning.substring(0,stringMeaning.length()-1);
                }
                stringMeaning = stringMeaning.substring(0,1).toUpperCase() + stringMeaning.substring(1);
                Elements exampleSentences = section.select("div[class=examp dexamp]");
                if(exampleSentences.size() > 0) {
                    word.addMeaning(stringHeading,stringMeaning);
                }
                for(Element exampleSentence : exampleSentences) {
                    word.addExample(stringMeaning,handleDictionaryCodes(exampleSentence.text()));
                }
            }
        }
        else {
            for(Element section : sections) {
                Elements mainHeading = section.select("span[class=pos dsense_pos]");
                String stringMainHeading = "";
                if(mainHeading.size() > 0) {
                    for (Element heading : mainHeading) {
                        String temp = heading.text();
                        temp = temp.substring(0, 1).toUpperCase() + temp.substring(1);
                        stringMainHeading += temp + "/";
                    }
                    stringMainHeading = stringMainHeading.substring(0,stringMainHeading.length()-1);
                }
                String subHeading = section.select("span[class=guideword dsense_gw]").text();
                String heading = stringMainHeading + " " + subHeading;
                heading = heading.substring(0,1).toUpperCase() + heading.substring(1);
                Elements subSections = section.select("div[class=def-block ddef_block ]");
                for(Element subSection : subSections) {
                    String stringMeaning = subSection.select("div[class=def ddef_d db]").text();
                    if(stringMeaning.endsWith(":")) {
                        stringMeaning = stringMeaning.substring(0,stringMeaning.length()-1);
                    }
                    stringMeaning = stringMeaning.substring(0,1).toUpperCase() + stringMeaning.substring(1);
                    Elements exampleSentences = subSection.select("div[class=examp dexamp]");
                    if(exampleSentences.size() > 0) {
                        word.addMeaning(heading,stringMeaning);
                    }
                    for(Element exampleSentence : exampleSentences) {
                        word.addExample(stringMeaning,handleDictionaryCodes(exampleSentence.text()));
                    }
                }
            }
        }
        if(word.isNull()) {
            return false;
        }
        else {
            saveLoad.add(searchString.toLowerCase(),word);
            return true;
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
        Word word = saveLoad.getWord(clickedText.toLowerCase());
        builder.setMessage(word.getMessage());
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