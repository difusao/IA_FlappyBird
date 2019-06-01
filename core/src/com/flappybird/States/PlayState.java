package com.flappybird.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import com.flappybird.FlappyBird;
import com.flappybird.Sprites.Bird;
import com.flappybird.Sprites.Tube;

import java.util.Random;

public class PlayState extends State {
    private static final int GROUND_Y_OFFSET = -30;
    private static final int TUBE_SPACING = 125;
    private static final int TUBE_COUNT = 4;

    private Bird bird[];
    private int nbird;
    private Texture background;
    private Texture ground;
    private Texture gameoverImg;
    private Vector2 groundPos1;
    private Vector2 groundPos2;
    private Vector2 groundPos3;
    private Vector2 groundPos4;
    private Vector2 groundPos5;
    private Array<Tube> tubes;
    private ShapeRenderer sr;
    private ShapeRenderer[] boxLine;
    private ShapeRenderer[] boxBg;
    private BitmapFont[] font;
    private SpriteBatch batch;
    private boolean gameover;
    private int last = nbird;
    private Texture point;
    private float middle = 0;
    private float birdX = 0;

    private ShapeRenderer circle1;

    public PlayState(GameStateManager gsm){

        super(gsm);

        point = new Texture("flapbird/pingo.png");

        nbird = 1;
        bird = new Bird[nbird];
        boxBg = new ShapeRenderer[nbird];
        boxLine = new ShapeRenderer[nbird];
        font = new BitmapFont[nbird];

        circle1 = new ShapeRenderer();
        circle1.setColor(Color.BLUE);

        for(int i=0; i<nbird; i++) {
            bird[i] = new Bird(0, 200);

            boxLine[i] = new ShapeRenderer();
            boxLine[i].setColor(Color.ORANGE);

            boxBg[i] = new ShapeRenderer();
            boxBg[i].setColor(.1f,.1f,.1f,0);

            font[i] = new BitmapFont();
            font[i].setColor(Color.WHITE);
            font[i].getData().setScale(0.6f, 0.6f);

            gameover = false;
        }

        background = new Texture("flapbird/bg.png");
        ground = new Texture("flapbird/ground.png");
        gameoverImg = new Texture("flapbird/gameover.png");
        point = new Texture("flapbird/pingo.png");

        tubes = new Array<Tube>();

        for(int i = 1; i <= TUBE_COUNT; i++) {
            tubes.add(new Tube(i * (TUBE_SPACING + Tube.TUBE_WIDTH)));
        }

        groundPos1 = new Vector2(ground.getWidth() * (-1), GROUND_Y_OFFSET);
        groundPos2 = new Vector2(0, GROUND_Y_OFFSET);
        groundPos3 = new Vector2(ground.getWidth(), GROUND_Y_OFFSET);
        groundPos4 = new Vector2(ground.getWidth() * 2, GROUND_Y_OFFSET);
        groundPos5 = new Vector2(ground.getWidth() * 3, GROUND_Y_OFFSET);

        gameover = false;
        batch = new SpriteBatch();
    }

    public void RandJump(){
        Random rand = new Random();
        int ranX = rand.nextInt(100);

        //if(ranX<nbird)
        //if(ranX % 2 == 0)
        if(ranX == 100)
            if(!gameover)
                bird[0].jump();
    }

    @Override
    public void handleInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            if(gameover)
                gsm.set(new PlayState(gsm));
            else
                bird[last].jump();
        }

        if(Gdx.input.isTouched()) {
            gsm.set(new PlayState(gsm));
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        updateGround();

        boolean tubecolision = false;
        boolean floorcolision = false;

        for(int i=0; i<nbird; i++) {
            bird[i].update(dt);
            cam.position.set(bird[last].getX() + 80, cam.viewportHeight / 2, 0);

            // passaros que tocam os Canos
            for(Tube tube : tubes){
                if(cam.position.x - cam.viewportWidth / 2 > tube.getPosTopTube().x + tube.getTopTube().getWidth()){
                    tube.reposition(tube.getPosTopTube().x +((Tube.TUBE_WIDTH + TUBE_SPACING) * TUBE_COUNT));
                }

                if (tube.collides(bird[i].getBounds())) {
                    bird[i].colliding = true;
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

        if (bird[last].colliding) {
            gameover = true;
        }
    }

    public void updateGround(){
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
        //RandJump();

        birdX = bird[0].getX();

        float tubodist = 0;

        sb.setProjectionMatrix(cam.combined);
        sb.begin();

        //sb.draw(background, cam.position.x - (cam.viewportWidth / 2), 0);

        for(Tube tube : tubes){
            sb.draw(tube.getBottomTube(), tube.getPosBottomTube().x, tube.getPosBottomTube().y);
            sb.draw(tube.getTopTube(), tube.getPosTopTube().x, tube.getPosTopTube().y);
            middle = tube.getPosTopTube().y + ((point.getHeight()/2) - tube.TUBE_GAP)/2 - 5;
            //sb.draw(point, tube.getPosTopTube().x + 20, middle);
            font[0].draw(sb, "(" + middle + ")", tube.getPosTopTube().x + tube.TUBE_WIDTH, middle);

            //if (birdX == middle)
            //    birdX = 0;

            //System.out.println(middle);
        }

        System.out.println();

        //for(Tube tube : tubes){
            //circle1.begin(ShapeRenderer.ShapeType.Filled);
            //circle1.circle(100, 100, 10);
            //circle1.end();
        //}

        sb.draw(ground, groundPos1.x, groundPos1.y);
        sb.draw(ground, groundPos2.x, groundPos2.y);
        sb.draw(ground, groundPos3.x, groundPos3.y);
        sb.draw(ground, groundPos4.x, groundPos4.y);
        sb.draw(ground, groundPos5.x, groundPos5.y);

        for(int i=0; i<nbird; i++) {
            sb.draw(bird[i].getTexture(), bird[i].getX(), bird[i].getY());
            //font[i].draw(sb,i+"",bird[i].getX() - (bird[i].getWidth() / 2)+65, bird[i].getY() + (bird[i].getHeight() / 2) + 5);
            //font[i].draw(sb, "(" + (bird[i].getX() - middle) +  ")", bird[i].getX() + (bird[i].getWidth() / 2) - 15, bird[i].getY() + (bird[i].getHeight() / 2) + 15 );
            //System.out.println(tubodist);
        }

        sb.end();

        // Monitor
        batch.begin();
        int salt = FlappyBird.HEIGHT;
        for(int i=0; i<nbird; i++) {
            //font[i].draw(batch, "[" + i + "]  x = " + Math.round(bird[i].getX()) + " y = " + Math.round(bird[i].getY()), (FlappyBird.WIDTH - 200), salt - 15);
            //System.out.println(Math.round(bird[i].getX()) +"," + Math.round(bird[i].getY()));
            salt -= 24;
        }
        batch.end();
    }
}
