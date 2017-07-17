package com.example.swt369.blockbreaker;

import android.graphics.RectF;

import java.util.ArrayList;

/**
 * Created by swt369 on 2017/6/8.
 * 编辑器视图
 */

class EditorArea {

    //砖块对象，以ArrayList存储
    private ArrayList<ArrayList<Brick>> bricks;

    //最大层数
    private final int LEVEL = (int)(Settings.AREA_HEIGHT / Settings.BRICK_THICKNESS);

    //构造函数
    EditorArea(){
        initializeBricks();
    }

    //初始化砖块容器
    private void initializeBricks(){
        bricks = new ArrayList<>();
        for(int i=0;i<LEVEL;i++){
            bricks.add(new ArrayList<Brick>());
        }
    }

    //
    private int getLevel(int y){
        int level = (int)(y / Settings.BRICK_THICKNESS);
        if(level < 0) level = 0;
        if(level > LEVEL - Settings.MAXLEVEL_OFFSET) level = LEVEL - Settings.MAXLEVEL_OFFSET;
        return level;
    }

    //根据传入RectF对象添加砖块
    void addBrickFromRectF(RectF rectF){
        //检测是否合法
        if(rectF.right < 0 || rectF.left > Settings.AREA_WIDTH || rectF.bottom < 0 || rectF.top > Settings.AREA_HEIGHT) return;
        //计算所在层次
        int level = getLevel((int)rectF.top);
        if(rectF.left < 0) rectF.offsetTo(0,rectF.top);
        else if(rectF.right > Settings.AREA_WIDTH) rectF.offsetTo(Settings.AREA_WIDTH - rectF.width(),rectF.top);
        //添加砖块
        addBrick((int)rectF.left,(int)rectF.width(),level);
    }

    //添加砖块
    private void addBrick(int leftPosX,int length,int level){
        bricks.get(level).add(new Brick(leftPosX,Settings.BRICK_THICKNESS * level - 1,length));
        adjust_oneLevel(bricks.get(level));
    }

    //调整层次间砖块在容器内的次序，使之保证每一层中坐标递增
    private void adjust_oneLevel(ArrayList<Brick> oneLevel){
        //插入排序
        Brick temp = oneLevel.get(oneLevel.size() - 1);
        int i;
        for(i = oneLevel.size() - 1;i >= 1 && oneLevel.get(i - 1).getLeftPosX() > temp.getLeftPosX();i--)
            oneLevel.set(i,oneLevel.get(i - 1));
        oneLevel.set(i,temp);

        //判断新砖块与两侧砖块是否重叠
        boolean flag_overlap_left = (i >= 1) && temp.getLeftPosX() < oneLevel.get(i - 1).getRightPosX();
        boolean flag_overlap_right = (i < oneLevel.size() - 1) && temp.getRightPosX() > oneLevel.get(i + 1).getLeftPosX();
        //若与两侧均重叠则放弃
        if(flag_overlap_left && flag_overlap_right){
            oneLevel.remove(i);
            return;
        }
        //若都不重叠则完成
        if(!flag_overlap_left && !flag_overlap_right){
            return;
        }
        //若与左侧重叠则计算右侧空间，足够则调整位置，不足则放弃
        if(flag_overlap_left){
            float space = (i < oneLevel.size() - 1) ? oneLevel.get(i + 1).getLeftPosX() - temp.getRightPosX()
                    : Settings.AREA_WIDTH - temp.getRightPosX();
            float overlapLength = oneLevel.get(i - 1).getRightPosX() - temp.getLeftPosX();
            if(space >= overlapLength){
                temp.setLeftPosX(temp.getLeftPosX() + overlapLength);
            }else{
                oneLevel.remove(i);
            }
            return;
        }
        //若与右侧重叠则计算左侧空间，足够则调整位置，不足则放弃
        if(flag_overlap_right){
            float space = (i >= 1) ? temp.getLeftPosX() - oneLevel.get(i - 1).getRightPosX()
                    : temp.getLeftPosX();
            float overlapLength = temp.getRightPosX() - oneLevel.get(i + 1).getLeftPosX();
            if(space >= overlapLength){
                temp.setLeftPosX(temp.getLeftPosX() - overlapLength);
            }else{
                oneLevel.remove(i);
            }
        }
    }

    //获取砖块容器
    ArrayList<ArrayList<Brick>> getBricks(){
        return bricks;
    }

    Brick findBrickByPoint(int x,int y){
        if(x < 0 || x > Settings.AREA_WIDTH || y < 0 || y > Settings.AREA_HEIGHT) return null;
        int level = getLevel(y);
        ArrayList<Brick> oneLevel = bricks.get(level);
        for(int i = 0;i < oneLevel.size();i++){
            Brick brick = oneLevel.get(i);
            if(new RectF(brick.getLeftPosX(),brick.getLeftPosY(),brick.getRightPosX(),brick.getLeftPosY() + Settings.BRICK_THICKNESS)
                    .contains(x,y)){
                oneLevel.remove(i);
                return brick;
            }
        }
        return null;
    }

    int getBrickNum(){
        int num = 0;
        for(int i = 0;i < bricks.size();i++){
            num += bricks.get(i).size();
        }
        return num;
    }

}
