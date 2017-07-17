package com.example.swt369.blockbreaker;

import android.content.Context;
import android.graphics.RectF;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;

/**
 * Created by swt369 on 2017/6/8.
 * 编辑器控制器对象
 */

class EditorController implements View.OnTouchListener{

    //
    private Context context;
    //编辑器视图对象
    private EditorView editorView;
    //消息处理对象
    private Handler handler;
    //编辑器区域对象
    private EditorArea editorArea;
    //
    private boolean flag_onMove;

    EditorController(EditorView editorView, Context context,Handler handler){
        this.editorView = editorView;
        this.context = context;
        this.handler = handler;
        editorArea = new EditorArea();
        editorView.setArea(editorArea);
        flag_onMove = false;
    }

    private RectF getRectFByCenter(float x,float y){
        float halfLength = editorView.getDefaultBrickLengthPixel() / 2;
        float halfHeight = editorView.getDefaultBrickThicknessPixel() / 2;
        return new RectF(x - halfLength,y - halfHeight,x + halfLength, y + halfHeight);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(editorView.getButtonConfirmRectF().contains(event.getX(),event.getY())){
                    File dir;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath());
                    }else {
                        dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath());
                    }
                    int num = dir.listFiles().length;
                    File file = new File(dir.getPath()+File.separator+String.valueOf(num + 1) + ".txt");
                    Brick.bricksToTxt(editorArea.getBricks(),file);
                    Message m = handler.obtainMessage();
                    m.what = Codes.CODE_EDITOR_BACK;
                    handler.sendMessage(m);
                    return true;
                }
                flag_onMove = editorView.getRectF_brickFixed().contains(event.getX(), event.getY());
                if(flag_onMove){
                    editorView.setRectF_movingBrick(getRectFByCenter(event.getX(),event.getY()));
                    editorView.invalidate();
                    return true;
                }
                Brick brick = editorArea.findBrickByPoint(editorView.pixelToLogic_X(event.getX()),editorView.pixelToLogic_Y(event.getY()));
                if(brick != null){
                    flag_onMove = true;
                    editorView.setRectF_movingBrick(new RectF(brick.getLeftPosX(), brick.getLeftPosY(),
                            brick.getRightPosX(),brick.getLeftPosY() + Settings.BRICK_THICKNESS));
                }else{
                    editorView.setRectF_movingBrick(null);
                }
                editorView.invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                if(!flag_onMove) return true;
                editorView.setRectF_movingBrick(getRectFByCenter(event.getX(),event.getY()));
                editorView.invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                if(!flag_onMove) return true;
                flag_onMove = false;
                editorArea.addBrickFromRectF(editorView.pixelToLogic_rectF(editorView.getRectF_movingBrick()));
                editorView.setRectF_movingBrick(null);
                editorView.invalidate();
                Log.i("brickNum",String.valueOf(editorArea.getBrickNum()));
                return true;
        }
        return false;
    }

}
