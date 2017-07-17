package com.example.swt369.blockbreaker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by swt369 on 2017/6/1.
 * 绘图类，将逻辑坐标转换为像素坐标并绘制出来
 */

class GameView extends View implements Serializable{

    private final Context context;
    //1逻辑单位代表的像素数
    private float pixelX;
    private float pixelY;
    private float maxPixelX;
    private float maxPixelY;
    private float boardLeftPixelX;

    //区域对象
    private Area area;

    //
    private ScoreBoard scoreBoard;

    //画笔
    private Paint paintBar;
    private Paint paintBall;
    private Paint paintBall_Penetrate;
    private Paint paintBrick;
    private Paint paintBrick_Penetrate;
    private Paint paintBrick_Bigball;
    private Paint paintBrick_Longbar;
    private Paint paintBrick_Speedup;
    private Paint paintBoard;

    private Bitmap bitmapBall;
    private Bitmap bitmapBar;
    private Bitmap bitmapTail;
    private Bitmap bitmapBrickNormal;
    private Bitmap bitmapBrickPenetrate;
    private Bitmap bitmapBigBall;
    private Bitmap bitmapLongBar;
    private Bitmap bitmapSpeedUp;

    //构造函数，需传入上下文
    public GameView(Context context) {
        super(context);
        this.context = context;

        maxPixelX = context.getResources().getDisplayMetrics().widthPixels;
        maxPixelY = context.getResources().getDisplayMetrics().heightPixels;

        //根据设备像素数计算单位逻辑单位代表的像素数
        boardLeftPixelX = maxPixelX * Settings.GAMEAREA_RATIO;
        pixelX = boardLeftPixelX / Settings.AREA_WIDTH;
        pixelY = (float)context.getResources().getDisplayMetrics().heightPixels / Settings.AREA_HEIGHT;

        initializeScoreBoard();
        area = new Area(context,scoreBoard);

        initializePaintBar();
        initializePaintBall();
        initializePaintBrick();
        initializePaintBoard();

        this.setBackgroundResource(R.drawable.bg);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBar(canvas);
        drawBall(canvas);
        drawBricks(canvas);
        drawBoard(canvas);
        scoreBoard.draw(canvas);
    }

    private void initializePaintBar() {
        paintBar = new Paint();
        paintBar.setColor(Settings.BAR_COLOR);
        bitmapBar = BitmapFactory.decodeResource(context.getResources(),R.drawable.bar);
    }

    private void initializePaintBall() {
        paintBall = new Paint();
        paintBall.setAntiAlias(true);
        paintBall.setStyle(Paint.Style.FILL);
        paintBall.setColor(Settings.BALL_COLOR);
        bitmapBall = BitmapFactory.decodeResource(context.getResources(),R.drawable.cursor);
        bitmapTail = BitmapFactory.decodeResource(context.getResources(),R.drawable.cursortrail);

        paintBall_Penetrate = new Paint();
        paintBall_Penetrate.setColor(Settings.BALL_COLOR_PENETRATE);
    }

    private void initializePaintBrick() {
        paintBrick = new Paint();
        paintBrick.setColor(Settings.BRICK_COLOR_NORMAL);
        bitmapBrickNormal = BitmapFactory.decodeResource(context.getResources(),R.drawable.brick_normal);
        paintBrick_Penetrate = new Paint();
        paintBrick_Penetrate.setColor(Settings.BRICK_COLOR_PENETRATE);
        bitmapBrickPenetrate = BitmapFactory.decodeResource(context.getResources(),R.drawable.brick_penetrate);
        paintBrick_Bigball = new Paint();
        paintBrick_Bigball.setColor(Settings.BRICK_COLOR_BIGBALL);
        bitmapBigBall = BitmapFactory.decodeResource(context.getResources(),R.drawable.brick_bigball);
        paintBrick_Longbar = new Paint();
        paintBrick_Longbar.setColor(Settings.BRICK_COLOR_LONGBAR);
        bitmapLongBar = BitmapFactory.decodeResource(context.getResources(),R.drawable.brick_longbar);
        paintBrick_Speedup = new Paint();
        paintBrick_Speedup.setColor(Settings.BRICK_COLOR_SPEEDUP);
        bitmapSpeedUp = BitmapFactory.decodeResource(context.getResources(),R.drawable.brick_speedup);
    }

    private void initializePaintBoard(){
        paintBoard = new Paint();
        paintBoard.setColor(Settings.COLOR_BOARD);
    }

    //
    private void initializeScoreBoard(){
        int[] imgs = new int[10];
        imgs[0] = R.drawable.score_0;
        imgs[1] = R.drawable.score_1;
        imgs[2] = R.drawable.score_2;
        imgs[3] = R.drawable.score_3;
        imgs[4] = R.drawable.score_4;
        imgs[5] = R.drawable.score_5;
        imgs[6] = R.drawable.score_6;
        imgs[7] = R.drawable.score_7;
        imgs[8] = R.drawable.score_8;
        imgs[9] = R.drawable.score_9;
        scoreBoard = new ScoreBoard(context,(int)maxPixelX - 20,Settings.SCOREBOARD_TOP
                ,Settings.SINGLESCOREDIGIT_WIDTH,Settings.SINGLESCOREDIGIT_HEIGHT,imgs);
    }

    private void drawBar(Canvas canvas) {
        canvas.drawBitmap(bitmapBar,new Rect(0,0,bitmapBar.getWidth(),bitmapBar.getHeight()),area.getBar().getRect(pixelX,pixelY),paintBar);
    }

    private void drawBall(Canvas canvas) {
        float radius = area.getBall().getRadius();
        if(!area.getBall().canPenetrate())
            canvas.drawCircle(pixelX * area.getBall().getPosX(),pixelY * area.getBall().getPosY(),
                    pixelX * radius,paintBall);
//            canvas.drawBitmap(bitmapBall,new Rect(0,0,bitmapBall.getWidth(),bitmapBall.getHeight()),area.getBall().getBallRectF(),paintBall);
        else
            canvas.drawCircle(pixelX * area.getBall().getPosX(),pixelY * area.getBall().getPosY(),
                    pixelX * radius,paintBall_Penetrate);
        LinkedList<PointF> tail = area.getBall().getTail();
        for(int i = 0;i < tail.size();i++){
            PointF tailPointF = tail.get(i);
            canvas.drawBitmap(bitmapTail,new Rect(0,0,bitmapTail.getWidth(),bitmapTail.getHeight()),
                    new RectF((tailPointF.x - radius) * pixelX,(tailPointF.y - radius) * pixelY,
                            (tailPointF.x + radius) * pixelX,(tailPointF.y + radius) * pixelY)
                    ,new Paint());
        }
    }

    private void drawBricks(Canvas canvas) {
        ArrayList<ArrayList<Brick>> bricks = area.getBricks();
            for(int i=0;i<bricks.size();i++){
                ArrayList<Brick> oneLevel = bricks.get(i);
                for(int j=0;j<oneLevel.size();j++){
                    Brick brick = oneLevel.get(j);
                    if(!brick.isExist()) continue;
                    if(brick.getType() == Brick.BRICKTYPE_NORMAL)
                        canvas.drawBitmap(bitmapBrickNormal
                                ,new Rect(0,0,bitmapBrickNormal.getWidth(),bitmapBrickNormal.getHeight())
                                ,brick.getRectF(pixelX,pixelY),paintBrick);
                    else if(brick.getType() == Brick.BRICKTYPE_PENETRATE)
                        canvas.drawBitmap(bitmapBrickPenetrate
                                ,new Rect(0,0,bitmapBrickPenetrate.getWidth(),bitmapBrickPenetrate.getHeight())
                                ,brick.getRectF(pixelX,pixelY),paintBrick_Penetrate);
                    else if(brick.getType() == Brick.BRICKTYPE_BIGBALL)
                        canvas.drawBitmap(bitmapBigBall
                            ,new Rect(0,0,bitmapBigBall.getWidth(),bitmapBigBall.getHeight())
                            ,brick.getRectF(pixelX,pixelY),paintBrick_Bigball);
                    else if(brick.getType() == Brick.BRICKTYPE_LONGBAR)
                        canvas.drawBitmap(bitmapLongBar
                                ,new Rect(0,0,bitmapLongBar.getWidth(),bitmapLongBar.getHeight())
                                ,brick.getRectF(pixelX,pixelY),paintBrick_Longbar);
                    else if(brick.getType() == Brick.BRICKTYPE_SPEEDUP)
                        canvas.drawBitmap(bitmapSpeedUp
                                ,new Rect(0,0,bitmapSpeedUp.getWidth(),bitmapSpeedUp.getHeight())
                                ,brick.getRectF(pixelX,pixelY),paintBrick_Speedup);
                }
            }
    }

    //
    private void drawBoard(Canvas canvas){
        canvas.drawRect(boardLeftPixelX,0,maxPixelX,maxPixelY,paintBoard);
    }

    //用于判定是否触摸到了板
    public RectF getExtendedBarRectF(){
        RectF rectF = area.getBar().getRect(pixelX,pixelY);
        return new RectF(rectF.left - Settings.BAR_EXTEND_OFFSET_PIXEL_X,rectF.top - Settings.BAR_EXTEND_OFFSET_PIXEL_Y,
                rectF.right + Settings.BAR_EXTEND_OFFSET_PIXEL_X,rectF.bottom + Settings.BAR_EXTEND_OFFSET_PIXEL_Y);
    }

    //设置板左上角坐标，逻辑单位
    public void setBarX(float leftPosX){
        area.getBar().setLeftPosX(leftPosX);
    }

    //将板置于左侧
    public void setBarAtLeft(){ area.getBar().setLeftPosX(0);}

    //将板置于右侧
    public void setBarAtRight(){ area.getBar().setLeftPosX(Settings.AREA_WIDTH - area.getBar().getLength());}

    //获取X轴上1逻辑单位像素数
    public float getPixelX(){
        return pixelX;
    }

    //获取板右上角坐标,像素单位
    public float getBarRightPixel(){
        return area.getBar().getRect(pixelX,pixelY).right;
    }

    //获取板左上角坐标,像素单位
    public float getBarLeftPixel(){
        return area.getBar().getRect(pixelX,pixelY).left;
    }

    public float getAreaMaxPixelX() {return boardLeftPixelX;}

    //获取球中心坐标，像素单位
    public float getBallCenterPixel(){
        return area.getBall().getPosX() * pixelX;
    }

    //设置球中心坐标，逻辑单位
    public void setBallCenterX(float centerPosX){
        area.getBall().setPosX(centerPosX);
    }

    //判断球是否粘附在板上
    public boolean isOnBar(){
       return area.getBall().isOnBar();
    }

    //初始化球速并发射
    public void initializeBallSpeed(){
        area.getBar().setBallSpeed(area.getBall());
        area.getBall().leaveBar();
    }

    //计算下一帧并更新显示
    public void nextFrame(){
        area.nextFrame();
        scoreBoard.nextFrame();
        invalidate();
    }
}
