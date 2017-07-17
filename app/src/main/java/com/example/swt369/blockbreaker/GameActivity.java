package com.example.swt369.blockbreaker;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;

public class GameActivity extends AppCompatActivity {

    //绘图类
    private GameView gameView;
    //
    private GameController gameController;
    //
    private Thread thread;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            thread.interrupt();
            Log.i("finish","finished");
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(thread.isAlive()){
            gameController.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.interrupt();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameController.resume();
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        FrameLayout layout = (FrameLayout)findViewById(R.id.gameActivity_layout);
        //消息处理
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == Codes.CODE_INVALIDATE) {
                    gameView.nextFrame();
                    return true;
                }
                return false;
            }
        });
        //初始化并加入绘图类
        gameView = new GameView(this);
        //初始化控制器对象并开启线程
        gameController = new GameController(gameView, handler);
        gameView.setOnTouchListener(gameController);
        layout.addView(gameView);
        thread = new Thread(gameController);
        thread.start();
    }

}
