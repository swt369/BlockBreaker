package com.example.swt369.blockbreaker;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

import java.io.Serializable;

/**
 * Created by swt369 on 2017/6/4.
 * 管理音乐与音效
 */

class Sound implements Serializable {
    private SoundPool soundPool;
    private SparseIntArray soundMap;
    private Context context;
    Sound(Context context){
        this.context = context;
        initializeSoundPool();
    }
    private void initializeSoundPool(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder spb = new SoundPool.Builder();
            spb.setMaxStreams(5);
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            spb.setAudioAttributes(attrBuilder.build());
            soundPool = spb.build();
        }else {
            soundPool = new SoundPool(5,AudioManager.STREAM_MUSIC,1);
        }
        soundMap = new SparseIntArray();
        soundMap.put(1,soundPool.load(context,R.raw.knockbrick,1));
        soundMap.put(2,soundPool.load(context,R.raw.knockwall,1));
        soundMap.put(3,soundPool.load(context,R.raw.knockbar,1));
    }
    void knockWall(){
        soundPool.play(soundMap.get(1),1,1,0,0,1);
    }
    void knockBrick(){
        soundPool.play(soundMap.get(2),1,1,0,0,1);
    }
    void knockBar(){
        soundPool.play(soundMap.get(3),1,1,0,0,1);
    }
}
