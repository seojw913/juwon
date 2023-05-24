package com.taewon.mygallag.sprites;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.taewon.mygallag.MainActivity;
import com.taewon.mygallag.R;
import com.taewon.mygallag.SpaceInvadersView;
import com.taewon.mygallag.items.HealitemSprite;
import com.taewon.mygallag.items.PowerItemSprite;
import com.taewon.mygallag.items.SpeedItemSprite;

import java.util.ArrayList;
import java.util.Random;

public class AlienSprite extends Sprite{
    private Context context;    // 메인 액티비티
    private SpaceInvadersView game; // 게임 진행에 필요한 함수 사용
    ArrayList<AlienShotSprite> alienShotSprites;    // 적 총알 Sprite의 모음
    Handler fireHandler = null;     // 적 총알 발사 핸들러
    boolean isDestroyed = false;    // 적이 파괴되었는가

    public AlienSprite(Context context,SpaceInvadersView game, int resID, int x, int y){
        // 생성시 매개변수들 저장
        super(context,resID,x,y);
        this.context = context;
        this.game=game;
        alienShotSprites = new ArrayList<>();
        Random r = new Random();
        int randomDx = r.nextInt(5);
        int randomDy = r.nextInt(5);
        if(randomDy <= 0) randomDy = 1;
        dx = randomDx; dy = randomDy;   // 적의 움직임은 랜덤으로 지정
        fireHandler = new Handler(Looper.getMainLooper());  // 1초에 마다 랜덤으로 적이 총알을 발사하거나 안함
        fireHandler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Log.d("run","동작");
                        Random  r = new Random();
                        boolean isFire = r.nextInt(100)+1 <= 30;
                        if(isFire && !isDestroyed) {
                            fire();
                            fireHandler.postDelayed(this,1000);
                        }
                    }
                }, 1000);
    }

    @Override
    public void move() {    // 적 움직임 구현
        super.move();
        if(((dx<0) && (x<10)) || ((dx>0) && (x>800))) {
            dx = -dx;
            if(y>game.screenH){ game.removeSprite(this); }
        }
    }

    @Override
    public void handleCollision(Sprite other) {
        // 적이 다른 객체와 충돌 했을 때 기능 구현
        if(other instanceof ShotSprite){    // 플레이어 총알과 충돌시 객체 파괴
            game.removeSprite(other);
            game.removeSprite(this);
            destroyAlien();
            return;
        }
        if(other instanceof SpecialshotSprite){     // 플레이어 특수 공격과 충돌시 객체 파괴
            game.removeSprite(other);
            destroyAlien();
            return;
        }
    }

    private void destroyAlien() {   // 적 객체 파괴시
        isDestroyed = true;
        game.setCurrEnemyCount(game.getCurrEnemyCount()-1); // 현재 적 객체 수 -1
        for(int i = 0; i <alienShotSprites.size(); i++) // 적 객체가 사라졌으므로 적 총알 객체도 파괴
            game.removeSprite(alienShotSprites.get(i));
        spawnHealItem();    // 각종 아이템 랜덤 생성
        spawnPowerItem();
        spawnSpeedItem();
        game.setScore(game.getScore()+1);   // 점수 +1점
        MainActivity.scoreTv.setText(Integer.toString(game.getScore()));
    }

    private void fire() {   // 적 총알 발사시
        AlienShotSprite alienShotSprite = new AlienShotSprite(context,game,getX(),getY()+30,16);    // 적 총알 객체 생성
        alienShotSprites.add(alienShotSprite);  // 적 총알 객체 모음에 추가
        game.getSprites().add(alienShotSprite); // 화면에 나타나는 모든 객체 모음에 추가
    }

    private void spawnSpeedItem() {
        Random r = new Random();    // 랜덤확률로 아이템 생성 및 랜덤으로 이동
        int speedItemDrop = r.nextInt(100)+1;
        if(speedItemDrop <= 5){
            int dx = r.nextInt(10)+1;
            int dy = r.nextInt(10)+5;
            game.getSprites().add(new SpeedItemSprite(context, game,    // 아이템 객체 생성 및 화면 객체 모음에 추가
                            (int)this.getX(),(int)this.getY(),dx,dy));
        }
    }
    private void spawnPowerItem() {
        Random r = new Random();
        int powerItemDrop = r.nextInt(100)+1;
        if(powerItemDrop <= 3){
            int dx = r.nextInt(10)+1;
            int dy = r.nextInt(10)+10;
            game.getSprites().add(new PowerItemSprite(context, game,
                    (int)this.getX(),(int)this.getY(),dx,dy));
        }
    }

    private void spawnHealItem() {
        Random r = new Random();
        int healItemDrop = r.nextInt(100)+1;
        if(healItemDrop <= 1){
            int dx = r.nextInt(10)+1;
            int dy = r.nextInt(10)+10;
            game.getSprites().add(new HealitemSprite(context, game,
                    (int)this.getX(),(int)this.getY(),dx,dy));
        }
    }


}
