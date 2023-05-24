package com.taewon.mygallag;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

public class PauseDialog extends Dialog {

    RadioGroup bgMusicOnOff;
    RadioGroup effectSoundOnOff;

    public PauseDialog(@NonNull Context context){
        super(context);
        setContentView(R.layout.pause_dialog);
        bgMusicOnOff = findViewById(R.id.bgMusicOnOff);
        effectSoundOnOff = findViewById(R.id.effectSoundOnOff);
        init();
    }

    public void init() {
        bgMusicOnOff.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId){
                    case R.id.bgMusicOn:
                        MainActivity.bgMusic.setVolume(1,1);
                        break;
                    case R.id.bgMusicOff:
                        MainActivity.bgMusic.setVolume(0,0);
                        break;
                }
            }
        });

        effectSoundOnOff.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId){
                    case R.id.effectSoundOn:
                        MainActivity.effectVolumn = 1.0f;
                        break;
                    case R.id.effectSoundOff:
                        MainActivity.effectVolumn = 0;
                        break;
                }
            }
        });

        findViewById(R.id.dialogCancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { dismiss(); }
        });

        findViewById(R.id.dialogOkBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { dismiss(); }
        });
    }

}
