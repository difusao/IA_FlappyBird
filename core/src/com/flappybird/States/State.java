package com.flappybird.States;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.flappybird.FlappyBird;

public abstract class State {
    protected GameStateManager gsm;
    protected OrthographicCamera cam;
    protected Vector3 mouse;

    protected State(GameStateManager gsm){
        this.gsm = gsm;
        cam = new OrthographicCamera();
        //cam.setToOrtho(false, 240, 400);
        cam.setToOrtho(false, 480, 800);
    }

    public abstract void handleInput();
    public abstract void update(float dt);
    public abstract void render(SpriteBatch sb);
}
