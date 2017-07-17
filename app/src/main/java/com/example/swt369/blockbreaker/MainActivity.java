package com.example.swt369.blockbreaker;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeControls();

        File destDir;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            destDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath());
        }else {
            destDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath());
        }
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

    }

    @Override
    public void setRequestedOrientation(int requestedOrientation){

    }

    //initialize controls
    private void initializeControls(){
        initializeButtonStart();
        initializeButtonOption();
        initializeButtonEdit();
        initializeButtonExit();
    }

    //initialize the start button
    private void initializeButtonStart(){
        ImageButton imageButton_Start = (ImageButton) findViewById(R.id.mainActivity_ImageButton_Start);
        imageButton_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,GameActivity.class);
                startActivity(intent);
            }
        });
    }

    //initialize the option button
    private void initializeButtonOption(){
        ImageButton imageButton_Option = (ImageButton) findViewById(R.id.mainActivity_ImageButton_Option);
        imageButton_Option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    //initialize the edit button
    private void initializeButtonEdit(){
        ImageButton imageButton_Edit = (ImageButton) findViewById(R.id.mainActivity_ImageButton_Edit);
        imageButton_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,EditorActivity.class);
                startActivity(intent);
            }
        });
    }

    //initialize the exit button
    private void initializeButtonExit(){
        ImageButton imageButton_Exit = (ImageButton) findViewById(R.id.mainActivity_ImageButton_Exit);
        imageButton_Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
