package com.example.swt369.blockbreaker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by swt369 on 2017/6/8.
 * 编辑器视图
 */

class EditorView extends View {

    //
    private Context context;

    //1逻辑单位代表的像素数
    private float pixelX;
    private float pixelY;
    private float maxPixelX;
    private float maxPixelY;
    private float boardLeftPixelX;

    //
    private final int LEVEL = (int)(Settings.AREA_HEIGHT / Settings.BRICK_THICKNESS);

    //
    private Paint paintBoard;
    private Paint paintBrick;
    private Paint paintBrick_frame;
    private Paint paintDashLine;
    private Paint paintButtonConfirm;

    //
    private EditorArea editorArea;

    //
    private final RectF rectF_brickFixed;
    private RectF rectF_movingBrick;

    public EditorView(Context context) {
        super(context);
        this.context = context;

        maxPixelX = context.getResources().getDisplayMetrics().widthPixels;
        maxPixelY = context.getResources().getDisplayMetrics().heightPixels;

        //根据设备像素数计算单位逻辑单位代表的像素数
        boardLeftPixelX = maxPixelX * Settings.GAMEAREA_RATIO;
        pixelX = boardLeftPixelX / Settings.AREA_WIDTH;
        pixelY = (float)context.getResources().getDisplayMetrics().heightPixels / Settings.AREA_HEIGHT;

        //
        rectF_brickFixed = new RectF(boardLeftPixelX + Settings.EDITOR_FIXEDBRICK_OFFSET_X,Settings.EDITOR_FIXEDBRICK_OFFSET_Y,
                maxPixelX - Settings.EDITOR_FIXEDBRICK_OFFSET_X,Settings.EDITOR_FIXEDBRICK_OFFSET_Y + Settings.EDITOR_FIXEDBRICK_THICKNESS);

        //
        rectF_movingBrick = null;

        //
        initializePaintBoard();
        initializePaintBrick();
        initializePaintLine();
        initializePaintButton();

        this.setBackgroundResource(R.drawable.bg_editor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawBrick_fixed(canvas);
        drawLines(canvas);
        drawMovingBrick(canvas);
        drawBricks(canvas);
        drawButtons(canvas);
    }

    private void initializePaintBoard(){
        paintBoard = new Paint();
        paintBoard.setColor(Settings.COLOR_BOARD);
    }

    private void initializePaintBrick(){
        paintBrick = new Paint();
        paintBrick.setColor(Settings.BRICK_COLOR_NORMAL);
        paintBrick_frame = new Paint();
        paintBrick_frame.setStyle(Paint.Style.STROKE);
    }

    private void initializePaintLine(){
        paintDashLine = new Paint();
        paintDashLine.setStyle(Paint.Style.STROKE);
        paintDashLine.setPathEffect(new DashPathEffect(new float[]{100,30},0));
        paintDashLine.setStrokeWidth(5);
    }

    private void initializePaintButton(){
        paintButtonConfirm = new Paint();
    }

    private void drawBoard(Canvas canvas){
        canvas.drawRect(boardLeftPixelX,0,maxPixelX,maxPixelY,paintBoard);
    }

    private void drawBrick_fixed(Canvas canvas){
        canvas.drawRect(rectF_brickFixed,paintBrick);
    }

    private void drawLines(Canvas canvas){
        for(int i = 1;i < LEVEL - Settings.MAXLEVEL_OFFSET + 2;i++){
            Path path = new Path();
            path.moveTo(0,Settings.BRICK_THICKNESS * i * pixelX - 1);
            path.lineTo(boardLeftPixelX,Settings.BRICK_THICKNESS * i * pixelX - 1);
            canvas.drawPath(path,paintDashLine);
        }
    }
    private void drawBricks(Canvas canvas) {
        ArrayList<ArrayList<Brick>> bricks = editorArea.getBricks();
        for(int i = 0;i < bricks.size();i++){
            ArrayList<Brick> oneLevel = bricks.get(i);
            for(int j=0;j<oneLevel.size();j++){
                Brick brick = oneLevel.get(j);
                if(!brick.isExist()) continue;
                canvas.drawRect(brick.getRectF(pixelX,pixelY),paintBrick);
                canvas.drawRect(brick.getRectF(pixelX,pixelY),paintBrick_frame);
            }
        }
    }

    private void drawMovingBrick(Canvas canvas){
        if(rectF_movingBrick != null){
            canvas.drawRect(rectF_movingBrick,paintBrick);
        }
    }

    private void drawButtons(Canvas canvas){
        canvas.drawRect(getButtonConfirmRectF(),paintButtonConfirm);
    }

    void setArea(EditorArea editorArea){
        this.editorArea = editorArea;
    }

    RectF getRectF_brickFixed(){
        return rectF_brickFixed;
    }

    float getDefaultBrickLengthPixel(){
        return Settings.BRICK_LENGTH_DEFAULT * pixelX;
    }

    float getDefaultBrickThicknessPixel(){ return Settings.BRICK_THICKNESS * pixelY; }

    void setRectF_movingBrick(RectF rectF_movingBrick){
        this.rectF_movingBrick = rectF_movingBrick;
    }

    RectF getRectF_movingBrick(){
        return rectF_movingBrick;
    }

    RectF pixelToLogic_rectF(RectF rectF){
        return new RectF(rectF.left / pixelX,rectF.top / pixelY,rectF.right / pixelX,rectF.bottom / pixelY);
    }

    int pixelToLogic_X(float x){
        return (int)(x / pixelX);
    }

    int pixelToLogic_Y(float y){
        return (int)(y / pixelY);
    }

    RectF getButtonConfirmRectF(){
        return new RectF(boardLeftPixelX,maxPixelY - Settings.EDITOR_BUTTON_CONFRIM_HEIGHT,maxPixelX,maxPixelY);
    }
}
