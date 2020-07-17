package com.meaninglink;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    Gson gson;
    ArrayList<Note> notes;
    PreviewAdapter previewAdapter;
    RecyclerView previewRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
        gson = new Gson();

        previewRecyclerView = findViewById(R.id.content_main_rv_preview);
        previewRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), EditActivity.class);
                startActivity(i);
            }
        });
    }

    void loadDocument() {
        String documentString = sharedPreferences.getString("note", "");
        if(!documentString.equals("")) {
            Type type = new TypeToken<ArrayList<Note>>(){}.getType();
            notes = gson.fromJson(documentString, type);
            previewAdapter = new PreviewAdapter(notes);
            previewRecyclerView.setAdapter(previewAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDocument();
    }
}