package com.meaninglink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.UUID;

public class EditActivity extends AppCompatActivity {
    EditText et_input;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        et_input = findViewById(R.id.activity_edit_et_input);
        Intent i = getIntent();
        String input = i.getStringExtra("input");

        if(input != null) {
            et_input.setText(input);
            key = i.getStringExtra("key");
        }
        else {
            key = UUID.randomUUID().toString();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.edit_menu_check) {
            String input = et_input.getText().toString();
            if (input.equals("")) {
                Snackbar.make(findViewById(R.id.contraintLayout), "No Input!", Snackbar.LENGTH_LONG).show();
            } else {
                Intent i = new Intent(getApplicationContext(), ViewActivity.class);
                i.putExtra("input", input);
                i.putExtra("key", key);
                startActivity(i);
                finish();
            }
            return true;
        }
        return false;
    }
}