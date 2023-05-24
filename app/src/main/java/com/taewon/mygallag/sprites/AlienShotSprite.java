package com.taewon.mygallag.sprites;

import android.content.Context;

import com.taewon.mygallag.R;
import com.taewon.mygallag.SpaceInvadersView;

public class AlienShotSprite extends Sprite{

    Context context;
    SpaceInvadersView game;

    public AlienShotSprite(Context context,SpaceInvadersView game, float x, float y, int Dy){
        super(context, R.drawable.shot_001,x,y);
        this.game = game;
        this.context = context;
        setDy(dy);
    }

}
