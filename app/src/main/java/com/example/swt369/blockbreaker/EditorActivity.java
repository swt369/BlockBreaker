package com.example.swt369.blockbreaker;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class EditorActivity extends AppCompatActivity {

    //编辑器视图对象
    private EditorView editorView;

    //编辑器控制器对象
    private EditorController editorController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.editorActivity_layout);
        editorView = new EditorView(this);
        frameLayout.addView(editorView);
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what == Codes.CODE_EDITOR_BACK){
                    finish();
                }
                return false;
            }
        });
        editorController = new EditorController(editorView,this,handler);
        editorView.setOnTouchListener(editorController);
    }
}
