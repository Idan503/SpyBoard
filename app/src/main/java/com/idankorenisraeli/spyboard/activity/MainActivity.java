package com.idankorenisraeli.spyboard.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.idankorenisraeli.spyboard.R;
import com.idankorenisraeli.spyboard.data.DatabaseManager;

public class MainActivity extends AppCompatActivity {

    MaterialButton submitButton;
    TextInputEditText nameEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable name = nameEditText.getText();
                if(name!=null)
                    DatabaseManager.getInstance().setUserName(name.toString());
            }
        });
    }


    private void findViews(){
        this.submitButton = findViewById(R.id.main_BTN_submit);
        this.nameEditText = findViewById(R.id.main_EDT_name);
    }
}