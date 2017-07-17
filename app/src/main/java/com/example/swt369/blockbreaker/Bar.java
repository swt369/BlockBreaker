package com.example.swt369.blockbreaker;

import android.graphics.RectF;

import java.io.Serializable;

/**
 * Created by swt369 on 2017/6/1.
 * 板对象，主要维护板的位置、长度
 */

class Bar implements Serializable {

    //板左上角坐标与长度，逻辑单位
    private float leftPosX;
    private float leftPosY;
    private float length;

    //构造函数，需传入板左上角初始坐标与长度，逻辑单位
    Bar(float leftPosX, float leftPosY, float length){
        this.leftPosX = leftPosX;
        this.leftPosY = leftPosY;
        this.length = length;
    }

    //
    float getLength(){ return length;}

    //
    void setLength(float length){this.length = length;}

    //在板重心位置创建一个新的球对象
    Ball createBallOnCenter(){
        return new Ball(leftPosX + length / 2,leftPosY - Settings.BALL_RADIUS,Settings.BALL_RADIUS);
    }

    //返回一个与板重合的RectF对象，像素单位
    RectF getRect(float pixelX, float pixelY){
        return new RectF(pixelX * leftPosX,pixelY * leftPosY,pixelX * (leftPosX + length),pixelY * (leftPosY + Settings.BAR_THICKNESS));
    }

    //设置板左上角X坐标，逻辑单位
    void setLeftPosX(float leftPosX){
        this.leftPosX = leftPosX;
    }

    //
    float getLeftPosX(){return leftPosX;}

    //设置初始球速，根据球在板上的位置计算x,y轴速度比例
    void setBallSpeed(Ball ball){
        float angle = (ball.getPosX() - leftPosX - length/2) / (length/2) * Settings.BAR_ANGLE_MAX;
        float totalSpeed = (ball.getSpeedX() == 0 && ball.getSpeedY() == 0) ?
                Settings.BALL_MAXSPEED : (float) Math.sqrt(Math.pow(ball.getSpeedX(),2) + Math.pow(ball.getSpeedY(),2));
        ball.setSpeed((float)(totalSpeed * Math.sin(angle)),-(float)(totalSpeed * Math.cos(angle)));
    }

    //判断球是否碰到了板
    boolean isTouchedBar(Ball ball){
        RectF rectBall = ball.getBallRectF();
        RectF rectBar = new RectF(leftPosX - ball.getRadius(),leftPosY - ball.getRadius(),
                leftPosX + length + ball.getRadius(),leftPosY + ball.getRadius());
        return rectBar.intersect(rectBall);
    }
}
