package com.taewon.mygallag;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.taewon.mygallag.sprites.AlienSprite;
import com.taewon.mygallag.sprites.Sprite;
import com.taewon.mygallag.sprites.StarshipSprite;

import java.util.ArrayList;
import java.util.Random;

public class SpaceInvadersView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    private static int MAX_ENEMY_COUNT = 10;        // 한 화면에 동시에 그려질 수 있는 최대 적 개체 수
    private Context context;        // 메인 액티비티
    private int characterId;        // 초기화면에서 선택한 캐릭터의 리소스 ID
    private SurfaceHolder ourHolder;
    // 캔버스 락&언락을 제어하여 캔버스를 잠근 후 그림을 그리고 언락하여 화면을 그릴 수 있음
    // 그림을 미리 그려놓고 화면에 띄우는 더블버퍼링 방식이라 화면 그리기 속도가 매우 빨라져
    // 프레임 단위로 화면이 변하는 게임에 적용하기 좋음
    private Paint paint ;       // 화면에 그려내기 위한 Paint 변수
    public int screenW, screenH;    // 화면 크기
    private Rect src, dst;  // 화면 크기 좌표 지정
    private ArrayList sprites = new ArrayList();    // 한 프레임에서 화면에 나오는 객체들을 저장
    private Sprite starship;        // 플레이어 캐릭터
    private int score, currEnemyCount;      // 점수와 현재 적 객체 수
    private Thread gameThread = null;       // 빠르게 진행되는 게임을 위한 스레드
    private volatile boolean running;       //게임이 진행중이냐를 판단하는 boolean 변수, volatile으로 Main Memory에서 정보를 바로 읽어와 정확성이 높음
    private Canvas canvas;      // 화면에 그려내기 위한 Canvas 변수
    int mapBitmapY = 0;     //

    public SpaceInvadersView(Context context, int characterId, int x, int y){   // 생성시 받아온 정보를 저장하는 함수
        super(context);
        this.context = context;
        this.characterId = characterId;
        ourHolder = getHolder();
        paint = new Paint();
        screenW = x;
        screenH = y;
        src = new Rect();
        dst = new Rect();
        dst.set(0,0,screenW,screenH);
        startGame();
    }

    private void startGame() {
        sprites.clear();    // 게임 시작시 화면을 비우고 점수를 초기화시킴
        initSprites();
        score = 0;
    }

    public void endGame(){
        Log.e("GameOver","GameOver");
        Intent intent = new Intent(context,ResultActivity.class);       // 게임 오버시 결과 화면으로 넘어가며 점수값을 넘겨줌
        intent.putExtra("score",score);
        context.startActivity(intent);
        gameThread.stop();
    }

    public void removeSprite(Sprite sprite){
        sprites.remove(sprite);
    }

    private void initSprites() {
        starship = new StarshipSprite(context,this,characterId,
                screenW/2, screenH-400, 1.5f);      // 게임 시작시 화면 플레이어의 위치와 속도 초기화
        sprites.add(starship);  // 화면에 담아낼 객체에 플레이어 추가
        spawnEnemy();   // 적 객체 호출
        spawnEnemy();
    }

    public void spawnEnemy() {
        Random r = new Random();    // 가로 세로 100~400 안에서 랜덤위치로 적 생성
        int x = r.nextInt(300)+100;
        int y = r.nextInt(300)+100;

        Sprite alien = new AlienSprite(context, this, R.drawable.ship_0002,100+x,100+y);    // 적 Sprite 생성
        sprites.add(alien);     // 화면에 담아낼 객체에 적 개체 추가
        currEnemyCount++;       // 현재 화면에 적 객체 수 +1
    }

    public ArrayList getSprites() { return sprites; }

    public void resume() {      // 일시정지 해제
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public StarshipSprite getPlayer() { return (StarshipSprite)starship; }  // 플레이어 객체

    public int getScore() { return score; }

    public void setScore(int score){ this.score = score; }

    public void setCurrEnemyCount(int currEnemyCount) {
        this.currEnemyCount = currEnemyCount;
    }

    public int getCurrEnemyCount() {
        return currEnemyCount;
    }

    public void pause() {       // 일시정지
        running = false;
        try {
            gameThread.join();
        }
        catch (InterruptedException e){
        }
    }



    @Override
    public void run() {     // 게임 진행중
        while(running){
            Random r = new Random();
            boolean isEnemySpawn = r.nextInt(100)+1 < (getPlayer().speed + (int) (getPlayer().getPowerLevel() / 2));
            // 적 객체 호출 확률. (플레이어 속도 + 플레이어 파워 레벨) / 2 보다 랜덤 1~100 이 더 작을시 적 스폰. 속도와 파워가 올라갈수록 적 스폰 확률 증가

            if(isEnemySpawn && currEnemyCount < MAX_ENEMY_COUNT) spawnEnemy();
            // 적 객체 호출 이 true고 현재 적 객체 수가 최대 적 객체수보다 작으면 적 스폰

            for(int i = 0 ; i < sprites.size(); i++){
                Sprite sprite = (Sprite) sprites.get(i);
                sprite.move();  // 화면에 그려진 객체들의 이동 구현
            }

            for(int p = 0; p < sprites.size(); p++){    // 화면에 존재하는 모든 Sprite의 충돌을 비교하기 위한 반복문
                for(int s = p+1; s<sprites.size(); s++){
                    try {
                        Sprite me = (Sprite) sprites.get(p);
                        Sprite other = (Sprite) sprites.get(s);

                        if(me.checkCollision(other)){   // 어느 두 객체가 충돌한다면
                            me.handleCollision(other);  // 양쪽 객체 모두 충돌함수를 실행
                            other.handleCollision(me);
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            draw();     // 객체들의 존재 및 이동 계산 후 화면에 그려내기
            try {
                Thread.sleep(10);   // 0.01초 간격으로 진행
            }
            catch (Exception e){}
        }
    }


    public void draw() {
        if(ourHolder.getSurface().isValid()){   // Surface가 유효할 때
            canvas = ourHolder.lockCanvas();    // 그림판에 Lock을 걸고
            canvas.drawColor(Color.BLACK);      // 화면을 모두 까맣게 지운 후
            mapBitmapY++;                       // 잘모르겠음 레이어 개념인가 했는데 이 변수가 사용된 코드들이 영향을 끼치지 않음
            if(mapBitmapY < 0) mapBitmapY = 0;
            paint.setColor(Color.BLUE);         // 파란색으로 색을 선택 후
            for(int i=0; i<sprites.size(); i++){    // 화면에 존재하는 모든 객체들을 그려주기
                Sprite sprite = (Sprite) sprites.get(i);
                sprite.draw(canvas, paint);
            }
            ourHolder.unlockCanvasAndPost(canvas);  // 그림판에 Lock 풀기
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) { startGame(); }
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
    }

}
