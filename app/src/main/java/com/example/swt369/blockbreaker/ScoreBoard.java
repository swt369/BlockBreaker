package com.example.swt369.blockbreaker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by swt369 on 2017/6/7.
 * 计分板
 */

class ScoreBoard {

    private final Context context;
    private final int MAXDIGIT = 8;
    private int targetScore;
    private int curScore;
    private int[] scoreDigits;
    private int delta = 3;
    private int right;
    private int top;
    private int width;
    private int height;
    private int[] imgs;
    private Bitmap[] bitmaps;
    private Paint paint;


    ScoreBoard(Context context,int right,int top,int width,int height,int[] imgs){
        this.context = context;
        this.right = right;
        this.top = top;
        this.width = width;
        this.height = height;
        this.imgs = imgs;
        targetScore = 0;
        curScore = 0;
        initializeBitmaps();
        initializeScoreDigits();
        initializePaint();
    }

    //
    private void initializeBitmaps(){
        bitmaps = new Bitmap[10];
        for(int i = 0 ; i <= 9 ; i++){
            bitmaps[i] = BitmapFactory.decodeResource(context.getResources(),imgs[i]);
        }
    }

    //
    private void initializeScoreDigits(){
        scoreDigits = new int[MAXDIGIT];
        for(int i = 0;i < MAXDIGIT ;i++){
            scoreDigits[i] = 0;
        }
    }

    //
    private void initializePaint(){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setFilterBitmap(true);
    }

    //
    private void carry(int digit){
        if(digit >= MAXDIGIT) return;
        scoreDigits[digit]++;
        if(scoreDigits[digit] >= 10){
            scoreDigits[digit] -= 10;
            carry(digit+1);
        }
    }

    //
    private void setDelta(){
        delta = (int)((targetScore - curScore) / (Settings.FRAMECOUNT) * 2);
        if(delta < 1) delta = 1;
    }

    //
    private void addDeltaToScore(){
        if(curScore >= targetScore) return;
        setDelta();
        if(curScore + delta >= targetScore){
            scoreDigits[0] += targetScore - curScore;
            curScore = targetScore;
        }else {
            curScore += delta;
            scoreDigits[0] += delta;
        }
        while (scoreDigits[0] >= 10){
            scoreDigits[0] -= 10;
            carry(1);
        }
    }

    //
    void updateScore(int targetScore){
        this.targetScore = targetScore;
    }

    //
    void nextFrame(){
        addDeltaToScore();
    }

    //
    void draw(Canvas canvas){
        int curX = right;
        for(int i = 0;i < MAXDIGIT; i++){
            curX -= width;
            Bitmap bitmap = bitmaps[scoreDigits[i]];
            canvas.drawBitmap(bitmap,new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()),new Rect(curX,top,curX + width,top + height),paint);
        }
    }
}
