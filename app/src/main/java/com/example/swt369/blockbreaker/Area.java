package com.example.swt369.blockbreaker;


import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by swt369 on 2017/6/1.
 * 区域对象，直接与绘图类gameView交互
 */

class Area implements Serializable{

    //上下文对象，对应GameActivity.this
    private Context context;
    //板对象
    private Bar bar;
    //球对象
    private Ball ball;
    //音池对象
    private Sound sound;
    //计分板对象
    private ScoreBoard scoreBoard;
    //当前分数
    private int score;
    //砖块对象，以ArrayList存储
    private ArrayList<ArrayList<Brick>> bricks;

    //最大层数
    private final int LEVEL = (int)(Settings.AREA_HEIGHT / Settings.BRICK_THICKNESS);

    //记录获取穿透状态的时间
    private long t_pre_penetrate = Long.MAX_VALUE;
    //记录球增大效果时间
    private long t_pre_bigball = Long.MAX_VALUE;
    //记录板增长效果时间
    private long t_pre_longbar = Long.MAX_VALUE;
    //记录提速效果时间
    private long t_pre_speedup = Long.MAX_VALUE;

    //构造函数，需传入上下文对象与计分板对象
    Area(Context context,ScoreBoard scoreBoard) {
        this.context = context;
        this.scoreBoard = scoreBoard;
        initializeBar();
        initializeBall();
        initializeBricks();
        initializeSound();
        score = 0;
    }

    //初始化板
    private void initializeBar() {
        bar = new Bar(Settings.BAR_X_ORIGIN,Settings.BAR_Y,Settings.BAR_LENGTH_ORIGIN);
    }

    //初始化球
    private void initializeBall() {
        if(bar != null) ball = bar.createBallOnCenter();
    }

    //初始化砖块
    private void initializeBricks() {
        File dir;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath());
        }else {
            dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath());
        }
        if(dir.exists()){
            int cur = new Random().nextInt(dir.listFiles().length + 1);
            if(cur < dir.listFiles().length){
                bricks = Brick.txtToBricks(dir.listFiles()[cur],LEVEL);
            }
        }
        if(bricks != null) return;
        bricks = new ArrayList<>();
        for(int i=0;i<LEVEL;i++){
            bricks.add(new ArrayList<Brick>());
        }
        for(int i=0;i<LEVEL - 8;i++){
            for(int j=(i%2==0?0:1);j<10;j+=2){
                addBrick(j* 50,48,i);
            }
        }
    }

    //初始化音池
    private void initializeSound(){
        sound = new Sound(context);
    }

    //添加砖块
    private void addBrick(int leftPosX,int length,int level){
        bricks.get(level).add(new Brick(leftPosX,Settings.BRICK_THICKNESS * level - 1,length));
    }

    //获取板对象
    Bar getBar(){
        return bar;
    }

    //获取球对象
    Ball getBall(){
        return ball;
    }

    //获取砖块
    ArrayList<ArrayList<Brick>> getBricks(){return bricks;}

    //判断单层碰撞情况，若碰撞返回对应转块索引号，未碰撞返回-1
    private int collisionJudge_OneLevel(ArrayList<Brick> oneLevel){
        for(int i=0;i<oneLevel.size();i++){
            Brick brick = oneLevel.get(i);
            if(Brick.collision_BrickAndBall(brick,ball)) return i;
        }
        return -1;
    }
    //击中砖块时更新分数
    private void updateScore(){
        score += Settings.SCORE_ONEBRICK;
        scoreBoard.updateScore(score);
    }

    //计算下一帧
    void nextFrame(){
        //计算球下一帧位置
        ball.nextFrame();
        if(ball.isOnBar() && (ball.getSpeedX() != 0 || ball.getSpeedY() != 0)) ball.leaveBar();
        //若球还在板上则不继续进行计算
        if(ball.isOnBar()) return;

        //获取当前时间，若时间间隔超过规定时间则更新状态
        long t_now = System.currentTimeMillis();
        if(t_now - t_pre_penetrate >= Settings.DURATION_PENETRATE){
            ball.setCanPenetrate(false);
            t_pre_penetrate = Long.MAX_VALUE;
        }
        if(t_now - t_pre_bigball >= Settings.DURATION_BIGBALL){
            ball.setRadius(Settings.BALL_RADIUS);
            t_pre_bigball = Long.MAX_VALUE;
        }
        if(t_now  - t_pre_longbar >= Settings.DURATION_LONGBAR){
            bar.setLength(Settings.BAR_LENGTH_ORIGIN);
            bar.setLeftPosX(bar.getLeftPosX() + Settings.BAR_LENGTH_ORIGIN / 2);
            t_pre_longbar = Long.MAX_VALUE;
        }
        if(t_now - t_pre_speedup >= Settings.DURATION_SPEEDUP){
            float totalSpeed = (ball.getSpeedX() == 0 && ball.getSpeedY() == 0) ?
                    Settings.BALL_MAXSPEED : (float) Math.sqrt(Math.pow(ball.getSpeedX(),2) + Math.pow(ball.getSpeedY(),2));
            float ratio = totalSpeed / Settings.BALL_MAXSPEED;
            ball.setSpeed(ball.getSpeedX() / ratio,ball.getSpeedY() / ratio);
            t_pre_speedup = Long.MAX_VALUE;
        }

        //判断是否撞墙，撞墙则更新速度方向
        if(ball.getPosX() - ball.getRadius() <= 0){
            ball.setPosX(ball.getRadius() + 1);
            ball.reverseSpeedX();
            sound.knockWall();
        }else if(ball.getPosX() + ball.getRadius() >= Settings.AREA_WIDTH){
            ball.setPosX(Settings.AREA_WIDTH - ball.getRadius() - 1);
            ball.reverseSpeedX();
            sound.knockWall();
        }
        if(ball.getPosY() - ball.getRadius() <= 0){
            ball.setPosY(ball.getRadius() + 1);
            ball.reverseSpeedY();
            sound.knockWall();
        }else if(ball.getPosY() + ball.getRadius() >= Settings.AREA_HEIGHT){
            ball.setPosY(Settings.AREA_HEIGHT - ball.getRadius() - 1);
            ball.reverseSpeedY();
            sound.knockWall();
        }

        //判断是否撞到板子，是则更新速度方向
        if(bar.isTouchedBar(ball)){
            bar.setBallSpeed(ball);
            sound.knockBar();
            return;
        }

        //计算球当前所在层数，球只可能碰到在当前层+-1层的范围内的砖块
        int ballLevel = (int)(ball.getPosY() / Settings.BRICK_THICKNESS);
        int ballLevel_up = ballLevel - 1;
        int ballLevel_down = ballLevel + 1;

        //上层
        if(ballLevel_up >= 0 && ballLevel_up < LEVEL){
            ArrayList<Brick> oneLevel = bricks.get(ballLevel_up);
            int res = collisionJudge_OneLevel(oneLevel);
            if(res != -1){
                sound.knockBrick();
                makeChanges(oneLevel.get(res));
                updateScore();
                return;
            }
        }

        //当前层
        if(ballLevel >= 0 && ballLevel < LEVEL){
            ArrayList<Brick> oneLevel = bricks.get(ballLevel);
            int res = collisionJudge_OneLevel(oneLevel);
            if(res != -1){
                sound.knockBrick();
                makeChanges(oneLevel.get(res));
                updateScore();
                return;
            }
        }

        //下层
        if(ballLevel_down >= 0 && ballLevel_down < LEVEL){
            ArrayList<Brick> oneLevel = bricks.get(ballLevel_down);
            int res = collisionJudge_OneLevel(oneLevel);
            if(res != -1){
                sound.knockBrick();
                makeChanges(oneLevel.get(res));
                updateScore();
            }
        }
    }

    //根据砖块类型更改状态
    private void makeChanges(Brick brick){
        switch (brick.getType()){
            case Brick.BRICKTYPE_NORMAL:
                break;
            case Brick.BRICKTYPE_PENETRATE:
                ball.setCanPenetrate(true);
                t_pre_penetrate = System.currentTimeMillis();
                break;
            case Brick.BRICKTYPE_BIGBALL:
                ball.setRadius(Settings.BALL_RADIUS * 2);
                t_pre_bigball = System.currentTimeMillis();
                break;
            case Brick.BRICKTYPE_LONGBAR:
                if(bar.getLength() != Settings.BAR_LENGTH_ORIGIN * 2){
                    bar.setLength(Settings.BAR_LENGTH_ORIGIN * 1.5f);
                    bar.setLeftPosX(bar.getLeftPosX() - Settings.BAR_LENGTH_ORIGIN / 2);
                }
                t_pre_longbar = System.currentTimeMillis();
                break;
            case Brick.BRICKTYPE_SPEEDUP:
                ball.setSpeed(ball.getSpeedX() * Settings.SPEEDUP_RATIO,ball.getSpeedY() * Settings.SPEEDUP_RATIO);
                t_pre_speedup = System.currentTimeMillis();
                break;
        }
    }
}
