package com.example.swt369.blockbreaker;

import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by swt369 on 2017/6/1.
 * 砖块对象，主要维护砖块的左上角坐标、长度以及标志（是否存在）
 */

class Brick implements Serializable {

    //砖块左上角坐标与长度，逻辑单位
    private float leftPosX;
    private float leftPosY;
    private float length;

    //种类
    private int type;
    //可获取加速效果
    static final int BRICKTYPE_SPEEDUP = 4;
    //可获取增大板长效果
    static final int BRICKTYPE_LONGBAR = 3;
    //可获取增大球半径效果
    static final int BRICKTYPE_BIGBALL = 2;
    //可获取穿透效果
    static final int BRICKTYPE_PENETRATE = 1;
    //普通
    static final int BRICKTYPE_NORMAL = 0;
    //是否存在
    private boolean isExist = true;

    //构造函数，需传入砖块左上角坐标与长度，逻辑单位
    Brick(float leftPosX, float leftPosY, float length){
        this.leftPosX = leftPosX;
        this.leftPosY = leftPosY;
        this.length = length;
        initializeType();
    }

    //确定砖块类型
    private void initializeType(){
        double temp = Math.random();
        double sum = 0;
        if(temp <= (sum += Settings.PROBABILITY_PENETRATE)) type = BRICKTYPE_PENETRATE;
        else if(temp <= (sum += Settings.PROBABILITY_BIGBALL)) type = BRICKTYPE_BIGBALL;
        else if(temp <= (sum += Settings.PROBABILITY_LONGBAR)) type = BRICKTYPE_LONGBAR;
        else if(temp <= (sum += Settings.PROBABILITY_SPEEDUP)) type = BRICKTYPE_SPEEDUP;
        else type = BRICKTYPE_NORMAL;
    }

    //获取左侧X坐标，逻辑单位
    float getLeftPosX() {return leftPosX;}

    //获取左侧Y坐标，逻辑单位
    float getLeftPosY() {return leftPosY;}

    //获取右侧X坐标，逻辑单位
    float getRightPosX() {return leftPosX + length;}

    //设置左侧坐标，逻辑单位
    void setLeftPosX(float leftPosX) {this.leftPosX = leftPosX;}

    //获取砖长，逻辑单位
    float getLength() {return length;}

    //返回一个与砖块重合的RectF对象，像素单位
    RectF getRectF(float pixelX, float pixelY){
        return new RectF(pixelX * leftPosX,pixelY * leftPosY,pixelX * (leftPosX + length),pixelY * (leftPosY + Settings.BRICK_THICKNESS));
    }

    //获取类型
    int getType() {return type;}

    //是否存在
    boolean isExist(){
        return isExist;
    }

    //使板消失
    void disappear(){
        isExist = false;
    }

    //根据要求，处理砖和球的碰撞结果
    private static void collision_handle(Brick brick, Ball ball, int flag_speed){
        if(!ball.canPenetrate()){
            switch (flag_speed){
                case 0:
                    ball.setSpeedToUp();
                    break;
                case 1:
                    ball.setSpeedToDown();
                    break;
                case 2:
                    ball.setSpeedToLeft();
                    break;
                case 3:
                    ball.setSpeedToRight();
                    break;
            }
        }
        brick.disappear();
    }

    //处理砖与球碰撞事件
    static boolean collision_BrickAndBall(Brick brick, Ball ball){
        if(!brick.isExist()) return false;

        //检测是否会碰撞
        RectF rectBall = ball.getBallRectF();
        RectF rectBrick = new RectF(brick.getLeftPosX(),brick.getLeftPosY(),
                brick.getLeftPosX() + brick.getLength(),brick.getLeftPosY() + Settings.BRICK_THICKNESS);
        if(!rectBall.intersect(rectBrick)) return false;

        //上部碰撞
        RectF rectTop = new RectF(brick.getLeftPosX() - Settings.TOUCH_OFFSET,brick.getLeftPosY() - Settings.TOUCH_OFFSET,
                brick.getLeftPosX() + brick.getLength() + Settings.TOUCH_OFFSET,brick.getLeftPosY() + Settings.TOUCH_OFFSET);
        //底部碰撞
        RectF rectBottom = new RectF(brick.getLeftPosX() - Settings.TOUCH_OFFSET,brick.getLeftPosY() + Settings.BRICK_THICKNESS - Settings.TOUCH_OFFSET,
                brick.getLeftPosX() + brick.length + Settings.TOUCH_OFFSET,brick.getLeftPosY() + Settings.BRICK_THICKNESS + Settings.TOUCH_OFFSET);
        if(rectTop.intersect(rectBall)){
            collision_handle(brick,ball, 0);
        }else if(rectBottom.intersect(rectBall)){
            collision_handle(brick,ball, 1);
        }

        //左侧碰撞
        RectF rectLeft = new RectF(brick.getLeftPosX() - Settings.TOUCH_OFFSET,brick.getLeftPosY() - Settings.TOUCH_OFFSET,
                brick.getLeftPosX() + Settings.TOUCH_OFFSET,brick.getLeftPosY() + Settings.BRICK_THICKNESS + Settings.TOUCH_OFFSET);
        //右侧碰撞
        RectF rectRight = new RectF(brick.getLeftPosX() + brick.getLength() - Settings.TOUCH_OFFSET,brick.getLeftPosY() - Settings.TOUCH_OFFSET,
                brick.getLeftPosX() + brick.getLength() + Settings.TOUCH_OFFSET,brick.getLeftPosY() + Settings.BRICK_THICKNESS + Settings.TOUCH_OFFSET);
        if(rectLeft.intersect(rectBall)){
            collision_handle(brick,ball, 2);
        }else if(rectRight.intersect(rectBall)){
            collision_handle(brick,ball, 3);
        }
        return true;
    }

    //将砖块信息存储为txt文件
    static void bricksToTxt(ArrayList<ArrayList<Brick>> bricks, File file){
        if(!file.getName().contains(".txt")){
            Log.i("txt","invalid");
            return;
        }
        BufferedWriter bufferedWriter;
        try{
            bufferedWriter = new BufferedWriter(new FileWriter(file));
        }catch (IOException e) {
            e.printStackTrace();
            return;
        }
        for(int i = 0;i < bricks.size();i++){
            ArrayList<Brick> oneLevel = bricks.get(i);
            for(int j = 0;j < oneLevel.size();j++){
                Brick brick = oneLevel.get(j);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(brick.getLeftPosX());
                stringBuilder.append(' ');
                stringBuilder.append(brick.getLength());
                stringBuilder.append(' ');
                stringBuilder.append(brick.getLeftPosY());
                try {
                    bufferedWriter.write(stringBuilder.toString());
                    bufferedWriter.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.i("txt","created");
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取txt文件并转化为砖块
    @Nullable
    static ArrayList<ArrayList<Brick>> txtToBricks(File file, int maxLevel){
        ArrayList<ArrayList<Brick>> bricks = new ArrayList<>();
        for(int i = 0 ;i < maxLevel;i++) bricks.add(new ArrayList<Brick>());
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while((line = bufferedReader.readLine()) != null){
                String[] nums = line.split(" ");
                float leftPosX = Float.parseFloat(nums[0]);
                float length = Float.parseFloat(nums[1]);
                float leftPosY = Float.parseFloat(nums[2]);
                int level = (int)(leftPosY / Settings.BRICK_THICKNESS);
                bricks.get(level).add(new Brick(leftPosX,leftPosY,length));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return bricks;
    }

}
