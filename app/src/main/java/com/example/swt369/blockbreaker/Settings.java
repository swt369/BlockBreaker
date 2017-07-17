package com.example.swt369.blockbreaker;

import android.graphics.Color;

/**
 * Created by swt369 on 2017/6/1.
 * 控制台
 */

final class Settings {

    //横向最大逻辑单位
    static final float AREA_WIDTH = 500;
    //纵向最大逻辑单位
    static final float AREA_HEIGHT = 375;
    //
    static final float GAMEAREA_RATIO = 0.8f;
    //板初始位置
    static final float BAR_X_ORIGIN = 225;
    static final float BAR_Y = 290;
    //板初始长度
    static final float BAR_LENGTH_ORIGIN = 75;
    //板厚
    static final float BAR_THICKNESS = 60;
    //板颜色
    static final int BAR_COLOR = Color.RED;
    //板判定范围扩大值，像素单位
    static final float BAR_EXTEND_OFFSET_PIXEL_X = 50;
    static final float BAR_EXTEND_OFFSET_PIXEL_Y = 50;
    //最大发射偏角
    static final float BAR_ANGLE_MAX = (float)(Math.PI/4);
    //球半径
    static final float BALL_RADIUS = 6;
    //球色
    static final int BALL_COLOR = Color.WHITE;
    static final int BALL_COLOR_PENETRATE = Color.GREEN;
    //球最大单向速度
    static final float BALL_MAXSPEED = 5.0f;
    //
    static final int BALL_TAILCOUNT = 40;
    //正常砖块色
    static final int BRICK_COLOR_NORMAL = Color.BLUE;
    static final int BRICK_COLOR_PENETRATE = Color.GREEN;
    static final int BRICK_COLOR_BIGBALL = Color.CYAN;
    static final int BRICK_COLOR_LONGBAR = Color.rgb(255,97,0);
    static final int BRICK_COLOR_SPEEDUP = Color.YELLOW;
    //砖块厚
    static final float BRICK_THICKNESS = 18;
    //
    static final float BRICK_LENGTH_DEFAULT = 48;
    //虚接触
    static final float TOUCH_OFFSET = 0.01f;
    static final float JUDGE_OFFSET = 10f;
    //每秒帧数
    static final float FRAMECOUNT =(1000.0f/60.0f);
    //穿透球持续时间
    static final int DURATION_PENETRATE = 2500;
    static final int DURATION_BIGBALL = 4000;
    static final int DURATION_LONGBAR = 4000;
    static final int DURATION_SPEEDUP = 3000;
    //
    static final double PROBABILITY_PENETRATE = 0.03;
    static final double PROBABILITY_BIGBALL = 0.05;
    static final double PROBABILITY_LONGBAR = 0.03;
    static final double PROBABILITY_SPEEDUP = 0.02;
    //
    static final float SPEEDUP_RATIO = 1.5f;
    //
    static final int COLOR_BOARD = Color.rgb(221,160,221);
    //
    static final int SCOREBOARD_TOP = 150;
    static final int SINGLESCOREDIGIT_WIDTH = 40;
    static final int SINGLESCOREDIGIT_HEIGHT = 60;
    static final int SCORE_ONEBRICK = 100;
    //
    static final float EDITOR_FIXEDBRICK_OFFSET_X = 80;
    static final float EDITOR_FIXEDBRICK_OFFSET_Y = 100;
    static final float EDITOR_FIXEDBRICK_THICKNESS = 50;
    //
    static final int MAXLEVEL_OFFSET = 8;
    //
    static final float EDITOR_BUTTON_CONFRIM_HEIGHT = 200;
    //forbid instantiation
    private Settings(){}
}
