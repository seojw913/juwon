package com.taewon.mygallag;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity {

    private Intent userIntent;
    ArrayList<Integer> bgMusicList;
    public static SoundPool effectSound;
    public static float effectVolumn;
    ImageButton specialShotBtn;
    public static ImageButton fireBtn, reloadBtn;
    JoystickView joyStick;
    public static TextView scoreTv;
    LinearLayout gameFrame;
    ImageView pauseBtn;
    public static LinearLayout lifeFrame;
    SpaceInvadersView spaceInvadersView;
    public static MediaPlayer bgMusic;
    int bgMusicIndex;
    public static TextView bulletCount;
    private static ArrayList<Integer> effectSoundList;
    public static final int PLAYER_SHOT = 0;
    public static final int PLAYER_HURT = 1;
    public static final int PLAYER_RELOAD = 2;
    public static final int PLAYER_GET_ITEM = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userIntent = getIntent();
        bgMusicIndex = 0;
        bgMusicList = new ArrayList<Integer>();
        bgMusicList.add(R.raw.main_game_bgm1);
        bgMusicList.add(R.raw.main_game_bgm2);
        bgMusicList.add(R.raw.main_game_bgm3);

        effectSound = new SoundPool(5, AudioManager.USE_DEFAULT_STREAM_TYPE,0);
        effectVolumn = 1;

        specialShotBtn = findViewById(R.id.specialShotBtn);
        joyStick = findViewById(R.id.joyStick);
        scoreTv = findViewById(R.id.score);
        fireBtn = findViewById(R.id.fireBtn);
        reloadBtn = findViewById(R.id.reloadBtn);
        gameFrame = findViewById(R.id.gameFrame);
        pauseBtn = findViewById(R.id.pauseBtn);
        lifeFrame = findViewById(R.id.lifeFrame);

        init();
        setBtnBehavior();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bgMusic.start();
        spaceInvadersView.resume();
    }

    private void init(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        spaceInvadersView = new SpaceInvadersView(this,
                userIntent.getIntExtra("character",R.drawable.ship_0000), size.x, size.y);
        gameFrame.addView(spaceInvadersView);

        changeBgMusic();
        bgMusic.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) { changeBgMusic(); }
        });

        bulletCount = findViewById(R.id.bulletCount);

        bulletCount.setText(spaceInvadersView.getPlayer().getBulletsCount() + "/30");
        scoreTv.setText(Integer.toString(spaceInvadersView.getScore()));

        effectSoundList = new ArrayList<>();
        effectSoundList.add(PLAYER_SHOT, effectSound.load(MainActivity.this,R.raw.player_shot_sound,1));
        effectSoundList.add(PLAYER_HURT, effectSound.load(MainActivity.this,R.raw.player_hurt_sound,1));
        effectSoundList.add(PLAYER_RELOAD, effectSound.load(MainActivity.this,R.raw.reload_sound,1));
        effectSoundList.add(PLAYER_GET_ITEM, effectSound.load(MainActivity.this,R.raw.player_get_item_sound,1));
        bgMusic.start();
    }


    private void changeBgMusic() {
        bgMusic = MediaPlayer.create(this, bgMusicList.get(bgMusicIndex));
        bgMusic.start();
        bgMusicIndex++;
        bgMusicIndex = bgMusicIndex % bgMusicList.size();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bgMusic.pause();
        spaceInvadersView.pause();
    }

    public static void effectSound(int flag){
        effectSound.play(effectSoundList.get(flag),effectVolumn,effectVolumn,0,0,1.0f);
    }

    private void setBtnBehavior() {
        joyStick.setAutoReCenterButton(true);
        joyStick.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Log.d("keycode",Integer.toString(i));
                return false;
            }
        });

        joyStick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                Log.d("angle",Integer.toString(angle));
                Log.d("force",Integer.toString(strength));
                if(angle > 67.5 && angle < 112.5){
                    //위
                    spaceInvadersView.getPlayer().moveUp(strength / 10);
                    spaceInvadersView.getPlayer().resetDx();
                }
                else if(angle > 247.5 && angle < 292.5){
                    //아래
                    spaceInvadersView.getPlayer().moveDown(strength / 10);
                    spaceInvadersView.getPlayer().resetDx();
                }
                else if(angle > 112.5 && angle < 157.5){
                    //왼쪽 대각선 위
                    spaceInvadersView.getPlayer().moveUp(strength / 10 * 0.5);
                    spaceInvadersView.getPlayer().moveLeft(strength / 10 * 0.5 );
                }
                else if(angle > 157.5 && angle < 202.5){
                    //왼쪽
                    spaceInvadersView.getPlayer().moveLeft(strength / 10 );
                    spaceInvadersView.getPlayer().resetDy();
                }
                else if(angle > 202.5 && angle < 247.5){
                    //왼쪽 대각선 아래
                    spaceInvadersView.getPlayer().moveDown(strength / 10 * 0.5);
                    spaceInvadersView.getPlayer().moveLeft(strength / 10 * 0.5 );
                }
                else if(angle > 22.5 && angle < 57.5){
                    //오른쪽 대각선 위
                    spaceInvadersView.getPlayer().moveUp(strength / 10 * 0.5);
                    spaceInvadersView.getPlayer().moveRight(strength / 10 * 0.5 );
                }
                else if(angle > 337.5 || angle < 22.5){
                    //오른쪽
                    spaceInvadersView.getPlayer().moveRight(strength / 10 );
                    spaceInvadersView.getPlayer().resetDy();
                }
                else if(angle > 292.5 && angle < 337.5){
                    //오른쪽 대각선 아래
                    spaceInvadersView.getPlayer().moveDown(strength / 10 * 0.5);
                    spaceInvadersView.getPlayer().moveRight(strength / 10 * 0.5 );
                }
            }
        });

        fireBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spaceInvadersView.getPlayer().fire();
            }
        });

        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spaceInvadersView.getPlayer().reloadBullets();
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spaceInvadersView.pause();
                PauseDialog pauseDialog = new PauseDialog(MainActivity.this);
                pauseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        spaceInvadersView.resume();
                    }
                });
                pauseDialog.show();
            }
        });

        specialShotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(spaceInvadersView.getPlayer().getSpecialShotCount() >= 0)
                    spaceInvadersView.getPlayer().specialShot();
            }
        });

    }



}