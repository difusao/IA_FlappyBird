package com.flappybird.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.flappybird.Sprites.Bird;
import com.flappybird.Sprites.Tube;

import java.util.Locale;

public class PlayState2 extends State  {

    static int NBIRD = 1;

    static final int GROUND_Y_OFFSET = -30;
    static final int TUBE_SPACING = 125;
    static final int TUBE_COUNT = 4;

    Bird bird[];

    Texture background;
    Texture ground;
    Texture gameoverImg;
    Texture point;

    Vector2 groundPos1;
    Vector2 groundPos2;
    Vector2 groundPos3;
    Vector2 groundPos4;
    Vector2 groundPos5;

    Array<Tube> tubes;

    BitmapFont[] font;
    SpriteBatch batch;
    boolean gameover;
    int last = NBIRD;

    float middleX = 0;
    float middleY = 0;
    float difX = 0;
    float difY = 0;

    protected PlayState2(GameStateManager gsm) {
        super(gsm);

        bird = new Bird[NBIRD];
        font = new BitmapFont[NBIRD];

        for(int i = 0; i< NBIRD; i++) {
            bird[i] = new Bird(0, 200);
            font[i] = new BitmapFont();
            font[i].setColor(Color.WHITE);
            font[i].getData().setScale(1.0f, 1.0f);

            gameover = false;
        }

        // Textures
        background = new Texture("images_original/bg.png");
        ground = new Texture("images_original/ground.png");
        gameoverImg = new Texture("images_original/gameover.png");
        point = new Texture("images/pingo.png");

        tubes = new Array<Tube>();

        for(int i = 1; i <= TUBE_COUNT; i++)
            tubes.add(new Tube(i * (TUBE_SPACING + Tube.TUBE_WIDTH)));

        groundPos1 = new Vector2(cam.position.x - cam.viewportWidth / 2, GROUND_Y_OFFSET);
        groundPos2 = new Vector2((cam.position.x - cam.viewportWidth / 2) + ground.getWidth(), GROUND_Y_OFFSET);
        //groundPos1 = new Vector2(ground.getWidth() * (-1), GROUND_Y_OFFSET);
        //groundPos2 = new Vector2(0, GROUND_Y_OFFSET);
        //groundPos3 = new Vector2(ground.getWidth(), GROUND_Y_OFFSET);
        //groundPos4 = new Vector2(ground.getWidth() * 2, GROUND_Y_OFFSET);
        //groundPos5 = new Vector2(ground.getWidth() * 3, GROUND_Y_OFFSET);

        gameover = false;
        batch = new SpriteBatch();
    }

    @Override
    public void handleInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            if(gameover)
                gsm.set(new PlayState2(gsm));
            else
                bird[last].jump();
        }

        if(Gdx.input.isTouched())
            gsm.set(new PlayState2(gsm));
    }

    @Override
    public void update(float dt) {
        handleInput();
        updateGround();

        boolean tubecolision = false;
        boolean floorcolision = false;

        for(int i = 0; i< NBIRD; i++) {
            bird[i].update(dt);
            cam.position.set(bird[i].getX()+80, cam.viewportHeight / 2, 0);
            //cam.position.set(bird[last].getX() + 80, cam.viewportHeight / 2, 0);


            // passaros que tocam os Canos
            for(Tube tube : tubes){
                if(cam.position.x - cam.viewportWidth / 2 > tube.getPosTopTube().x + tube.getTopTube().getWidth())
                    tube.reposition(tube.getPosTopTube().x +((Tube.TUBE_WIDTH + TUBE_SPACING) * TUBE_COUNT));

                if (tube.collides(bird[i].getBounds())) {
                    //bird[i].colliding = true;
                }
            }

            // passaros que tocam o Chão
            if (bird[i].getY() <= ground.getHeight() + GROUND_Y_OFFSET) {
                //bird[i].colliding = true;
            }else{
                last = i;
            }

            cam.update();
        }

        if (bird[last].colliding)
            gameover = true;
    }

    @Override
    public void render(SpriteBatch sb) {
        //RandJump();
        //birdX = bird[0].getX();
        //float tubodist = 0;

        sb.setProjectionMatrix(cam.combined);
        sb.begin();

        sb.draw(background, cam.position.x - (cam.viewportWidth / 2), 0);

        for(Tube tube : tubes){
            sb.draw(tube.getBottomTube(), tube.getPosBottomTube().x, tube.getPosBottomTube().y);
            sb.draw(tube.getTopTube(), tube.getPosTopTube().x, tube.getPosTopTube().y);

            middleX = tube.getPosTopTube().x + tube.TUBE_GAP / 2;
            middleY = tube.getPosTopTube().y - tube.TUBE_GAP / 2;

            difX = (middleX - (bird[0].getX() - 25));
            difY = ((middleY-82) - (bird[0].getY() - 82));

            //font1[0].draw(sb, "(" + (middleY -bird[0].getX()) + ")", tube.getPosTopTube().x, middleY);
            //if( difX > 0 && difX < 175 ) {
            //    font1[0].draw(sb, "pos= " + String.format(Locale.US,"%01.0f / %01.0f", difX, difY) , middleX, middleY);
            //    sb.draw(point, middleX - 30, middleY);
            //}
        }

        sb.draw(ground, groundPos1.x, groundPos1.y);
        sb.draw(ground, groundPos2.x, groundPos2.y);
        //sb.draw(ground, groundPos1.x, groundPos1.y);
        //sb.draw(ground, groundPos2.x, groundPos2.y);
        //sb.draw(ground, groundPos3.x, groundPos3.y);
        //sb.draw(ground, groundPos4.x, groundPos4.y);
        //sb.draw(ground, groundPos5.x, groundPos5.y);

        for(int i = 0; i< NBIRD; i++)
              sb.draw(bird[i].getTexture(), bird[i].getX(), bird[i].getY());

        sb.end();
    }

    public void updateGround(){
        /*
        if(cam.position.x - (cam.viewportWidth / 2) > groundPos1.x + ground.getWidth())
            groundPos1.add(ground.getWidth() * 5, 0);

        if(cam.position.x - (cam.viewportWidth / 2)  > groundPos2.x + ground.getWidth())
            groundPos2.add(ground.getWidth() * 5, 0);

        if(cam.position.x - (cam.viewportWidth / 2)  > groundPos3.x + ground.getWidth())
            groundPos3.add(ground.getWidth() * 5, 0);

        if(cam.position.x - (cam.viewportWidth / 2)  > groundPos4.x + ground.getWidth())
            groundPos4.add(ground.getWidth() * 5, 0);

        if(cam.position.x - (cam.viewportWidth / 2)  > groundPos5.x + ground.getWidth())
            groundPos5.add(ground.getWidth() * 5, 0);
        */
    }
}
