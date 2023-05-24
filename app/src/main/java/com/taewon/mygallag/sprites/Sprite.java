package com.taewon.mygallag.sprites;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Sprite {
    protected float x,y;        // 이미지의 좌표
    protected int width, height;    // 이미지의 가로 세로 길이
    protected float dx, dy;     //이미지가 움직이는 크기
    private Bitmap bitmap;      //이미지 파일
    private int id;         //이미지 리소스 파일 id
    private RectF rect;     //이미지의 각 사각형 꼭짓점 좌표

    public Sprite(Context context, int resourceID, float x, float y){
        this.id = resourceID;       //초기화 과정으로 Sprite를 상속받은 객체가 생성될 때 각 변수들의 정보를 저장
        this.x = x; this.y = y;
        bitmap = BitmapFactory.decodeResource(context.getResources(),resourceID);
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        rect = new RectF();
    }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public void draw(Canvas canvas, Paint paint){ canvas.drawBitmap(bitmap,x,y,paint); }    // 화면에 그려내는 함수

    public void move() {        // 객체의 움직임을 좌표화하는 함수
        x = x+dx; y = y+dy;
        rect.left = x; rect.right = x+width;
        rect.top = y; rect.bottom = y+height;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getDx() { return dx; }
    public float getDy() { return dy; }

    public void setDx(float dx) { this.dx = dx; }
    public void setDy(float dy) { this.dy = dy; }
    public RectF getRect() { return rect; }
    public boolean checkCollision (Sprite other){       // 다른 객체와 충돌하였는가를 판단하는 boolean 함수
        return RectF.intersects(this.getRect(), other.getRect());
    }
    public void handleCollision(Sprite other){ }        // 다른 객체와 충돌했을 때 어떤 작용을 할 것인지 구현하는 함수
    public Bitmap getBitmap() { return bitmap; }
    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }
}
