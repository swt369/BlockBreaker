package com.example.swt369.blockbreaker;

import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.Serializable;

/**
 * Created by swt369 on 2017/6/1.
 * 控制器对象，主要用于处理触摸事件与控制线程
 */

class GameController implements Runnable,View.OnTouchListener,Serializable {

    //绘图类
    private GameView gameView;
    //主线程消息处理对象
    private Handler handler;

    //触摸点X坐标，像素单位
    private float touchedX;
    //触摸时板左上角X坐标，像素单位
    private float barLeftX;
    //触摸时球中心X坐标，像素单位
    private float ballCenterX;
    //
    private boolean isPause = false;

    //构造函数，需传入绘图对象与主线程消息处理对象
    GameController(GameView gameView,Handler handler){
        this.gameView = gameView;
        this.handler = handler;
    }

    //处理触摸事件
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            //按下
            case MotionEvent.ACTION_DOWN:
                RectF rectF = gameView.getExtendedBarRectF();
                //若触点在判定范围内则记录信息
                if(rectF.contains(event.getX(),event.getY())){
                    touchedX = event.getX();
                    barLeftX = gameView.getBarLeftPixel();
                    ballCenterX = gameView.getBallCenterPixel();
                }else{
                    touchedX = -1;
                }
                return true;
            //移动
            case MotionEvent.ACTION_MOVE:
                //若触点合法则更新位置信息
                if(touchedX != -1){
                    float offset = event.getX() - touchedX;
                    gameView.setBarX((barLeftX + offset) / gameView.getPixelX());
                    if(gameView.getBarLeftPixel() <= 0)
                        gameView.setBarAtLeft();
                    else if(gameView.getBarRightPixel() >= gameView.getAreaMaxPixelX())
                        gameView.setBarAtRight();
                    else if(gameView.isOnBar()){
                        gameView.setBallCenterX((ballCenterX + offset) / gameView.getPixelX());
                    }
                    gameView.invalidate();
                }
                return true;
            //抬起
            case MotionEvent.ACTION_UP:
                //若球在板上则发射球
                if(touchedX != -1){
                    if(gameView.isOnBar()){
                        gameView.initializeBallSpeed();
                    }
                    touchedX = -1;
                }
                return true;
        }
        return true;
    }

    //以60帧的速度发送信息更新界面
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()){
            Log.i(Thread.currentThread().getName(),"running");
            try {
                Thread.sleep((long)Settings.FRAMECOUNT);
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }catch (Exception e){
                e.printStackTrace();
            }
            if(!isPause){
                Message m = handler.obtainMessage();
                m.what = Codes.CODE_INVALIDATE;
                handler.sendMessage(m);
            }
        }
    }

    void pause(){
        isPause = true;
    }

    void resume(){
        isPause = false;
    }
}
