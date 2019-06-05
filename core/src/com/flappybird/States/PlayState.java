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

public class PlayState extends State {

    private static final int GROUND_Y_OFFSET = -30;
    private static final int TUBE_SPACING = 125;
    private static final int TUBE_COUNT = 4;

    private Bird bird;

    private Texture background;
    private Texture ground;
    private Texture gameoverImg;
    private Texture point;

    private Vector2 groundPos1;
    private Vector2 groundPos2;
    private Vector2 groundPos3;
    private Vector2 groundPos4;
    private Vector2 groundPos5;

    private Array<Tube> tubes;

    private boolean gameover;

    BitmapFont font;

    float middleX = 0;
    float middleY = 0;
    float difX = 0;
    float difY = 0;

    public PlayState(GameStateManager gsm){
        super(gsm);

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f, 1.0f);

        bird = new Bird(10, 200);
        point = new Texture("images/pingo.png");
        //background = new Texture("images_original/bg.png");
        ground = new Texture("images/ground.png");
        gameoverImg = new Texture("images_original/gameover.png");

        tubes = new Array<Tube>();

        for(int i = 1; i <= TUBE_COUNT; i++)
            tubes.add(new Tube(i * (TUBE_SPACING + Tube.TUBE_WIDTH)));

        //groundPos1 = new Vector2(cam.position.x - cam.viewportWidth / 2, GROUND_Y_OFFSET);
        //groundPos2 = new Vector2((cam.position.x - cam.viewportWidth / 2) + ground.getWidth(), GROUND_Y_OFFSET);

        groundPos1 = new Vector2(ground.getWidth() * (-1), GROUND_Y_OFFSET);
        groundPos2 = new Vector2(0, GROUND_Y_OFFSET);
        groundPos3 = new Vector2(ground.getWidth(), GROUND_Y_OFFSET);
        groundPos4 = new Vector2(ground.getWidth() * 2, GROUND_Y_OFFSET);
        groundPos5 = new Vector2(ground.getWidth() * 3, GROUND_Y_OFFSET);

        gameover = false;
    }

    @Override
    public void handleInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            if(gameover)
                gsm.set(new PlayState(gsm));
            else
                bird.jump();
        }

        if(Gdx.input.isTouched()) {
            if(gameover)
                gsm.set(new PlayState(gsm));
            else
                bird.jump();
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        updateGround();
        bird.update(dt);
        cam.position.set(bird.getX() + 80, cam.viewportHeight / 2, 0);

        for(Tube tube : tubes){
            if(cam.position.x - cam.viewportWidth / 2 > tube.getPosTopTube().x + tube.getTopTube().getWidth())
                tube.reposition(tube.getPosTopTube().x +((Tube.TUBE_WIDTH + TUBE_SPACING) * TUBE_COUNT));

            if(tube.collides(bird.getBounds())){
                //bird.colliding = true;
                //gameover = true;
            }
        }

        if(bird.getY() <= ground.getHeight() + GROUND_Y_OFFSET){
            //gameover = true;
            //bird.colliding = true;
        }

        cam.update();
    }

    public void updateGround(){
        /*
        if(cam.position.x - (cam.viewportWidth / 2) > groundPos1.x + ground.getWidth())
            groundPos1.add(ground.getWidth() * 2, 0);

        if(cam.position.x - (cam.viewportWidth / 2) > groundPos2.x + ground.getWidth())
            groundPos2.add(ground.getWidth() * 2, 0);
        */
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
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        //sb.draw(background, cam.position.x - (cam.viewportWidth / 2), 0);

        int i = 0;

        for(Tube tube : tubes){
            sb.draw(tube.getBottomTube(), tube.getPosBottomTube().x, tube.getPosBottomTube().y);
            sb.draw(tube.getTopTube(), tube.getPosTopTube().x, tube.getPosTopTube().y);

            middleX = tube.getPosTopTube().x + tube.TUBE_WIDTH / 2;
            middleY = tube.getPosTopTube().y - tube.TUBE_GAP / 2;

            difX = (middleX - (bird.getX() - 00));
            difY = (middleY - (bird.getY() - 82));

            if( difX > 0 && difX < 175 ) {

                sb.draw(point, ( middleX - point.getWidth() / 2 ), middleY);
                //font.draw(sb, String.format(Locale.US,"x=%01.0f y=%01.0f", middleX, middleY), middleX, middleY);
                System.out.printf(Locale.US, "X=%06.2f, Y=%06.2f%n", difX, difY);

                if( (middleX - bird.getX()) < 2)
                    System.out.println("-------------------------------------------------------------");
            }
        }

        sb.draw(ground, groundPos1.x, groundPos1.y);
        sb.draw(ground, groundPos2.x, groundPos2.y);
        sb.draw(ground, groundPos3.x, groundPos3.y);
        sb.draw(ground, groundPos4.x, groundPos4.y);
        sb.draw(ground, groundPos5.x, groundPos5.y);

        sb.draw(bird.getTexture(), bird.getX(), bird.getY());

        if(gameover)
            sb.draw(gameoverImg, cam.position.x - gameoverImg.getWidth() / 2, cam.position.y);

        sb.end();
    }
}
