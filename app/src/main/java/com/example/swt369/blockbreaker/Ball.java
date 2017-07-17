package com.example.swt369.blockbreaker;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by swt369 on 2017/6/1.
 * 球类，主要维护球的位置、速度、状态
 */

class Ball implements Serializable {

    //位置，逻辑单位
    private float posX;
    private float posY;

    //速度，逻辑单位
    private float speedX = 0f;
    private float speedY = 0f;

    //半径，逻辑单位
    private float radius;

    //球是否粘附在板上，true代表是，false代表否
    private boolean isOnBar = true;

    //是否是穿透球
    private boolean canPenetrate = false;

    //
    private LinkedList<PointF> tail;

    //构造函数，传入位置与半径
    Ball(float posX, float posY, float radius){
        this.posX = posX;
        this.posY = posY;
        this.radius = radius;
        tail = new LinkedList<>();
    }

    //获取球X坐标，逻辑单位
    float getPosX() {
        return posX;
    }

    //设置球X坐标，逻辑单位
    synchronized void setPosX(float posX) {this.posX = posX;}

    //获取球Y坐标，逻辑单位
    float getPosY() {
        return posY;
    }

    //设置球Y坐标，逻辑单位
    synchronized void setPosY(float posY) {this.posY = posY;}

    //获取球半径，逻辑单位
    float getRadius(){ return radius;}

    //设置球半径，逻辑单位
    synchronized void setRadius(float radius){ this.radius = radius;}

    //使球离开板
    void leaveBar(){
        isOnBar = false;
    }

    //判断球是否粘附在板上
    boolean isOnBar(){
        return isOnBar;
    }

    //设置球速度，逻辑单位
    synchronized void setSpeed(float speedX, float speedY){
        this.speedX = speedX;
        this.speedY = speedY;
    }

    //获取X向速度
    float getSpeedX(){return speedX;}

    //获取Y向速度
    float getSpeedY(){return speedY;}

    //使X方向速度反向
    synchronized void reverseSpeedX(){speedX *= -1.0f;}

    //使Y方向速度反向
    synchronized void reverseSpeedY(){
        speedY *= -1.0f;
    }

    //使Y方向速度朝上
    synchronized void setSpeedToUp(){
        speedY = -Math.abs(speedY);
    }

    //使Y方向速度朝下
    synchronized void setSpeedToDown(){
        speedY = Math.abs(speedY);
    }

    //使X方向速度朝左
    synchronized void setSpeedToLeft(){
        speedX = -Math.abs(speedX);
    }

    //使X方向速度朝右
    synchronized void setSpeedToRight(){
        speedX = Math.abs(speedX);
    }

    //是否是穿透球
    boolean canPenetrate(){ return canPenetrate;}

    //设置是否穿透
    synchronized void setCanPenetrate(boolean flag) {canPenetrate = flag;}

    //将球设置为下一帧的状态
    void nextFrame(){
        //若球不在板上则根据速度更新球的坐标
        if(!isOnBar){
            addToTail(posX,posY);
            posX += speedX;
            posY += speedY;
        }
    }

    LinkedList<PointF> getTail(){return tail;}

    //
    private void addToTail(float posX,float posY){
        tail.add(new PointF(posX,posY));
        if(tail.size() > Settings.BALL_TAILCOUNT){
            tail.removeFirst();
        }
    }

    //获取包裹球对象的RectF对象
    RectF getBallRectF(){
        return new RectF(posX - radius,posY - radius,posX + radius,posY + radius);
    }
}
