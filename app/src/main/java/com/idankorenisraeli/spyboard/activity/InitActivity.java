package com.idankorenisraeli.spyboard.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.idankorenisraeli.spyboard.R;
import com.idankorenisraeli.spyboard.data.DatabaseManager;

public class InitActivity extends AppCompatActivity {

    MaterialButton submitButton;
    TextInputEditText nameEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        String name = DatabaseManager.getInstance().getUserName();
        if (name != null) {
            nameEditText.setText(name);
            disableInterface();
            //name already saved 
        } else {
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Editable name = nameEditText.getText();
                    if (name != null) {
                        DatabaseManager.getInstance().setUserName(name.toString());
                        disableInterface();
                        finish();
                    }
                }
            });
        }
    }

    private void disableInterface() {
        nameEditText.setEnabled(false);
        submitButton.setVisibility(View.GONE);
    }

    private void findViews() {
        this.submitButton = findViewById(R.id.main_BTN_submit);
        this.nameEditText = findViewById(R.id.main_EDT_name);
    }


    @Override
    protected void onResume() {
        super.onResume();
        DatabaseManager.getInstance().setInitActivityShown(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DatabaseManager.getInstance().setInitActivityShown(false);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseManager.getInstance().setInitActivityShown(false);
        finish();
    }


}