package com.flappybird.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.flappybird.FlappyBird;
import com.flappybird.Sprites.Bird;
import com.flappybird.Sprites.Tube;
import com.nn.NeuralNetWork;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class PlayState3 extends State {

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

    // Neural Network
    NeuralNetWork nn;
    String pathDataSet;
    String pathNetwork;
    int[] layers = new int[]{2, 2, 1};
    int weightstotal = 9;
    int wavetotal = 1;
    int wave = 0;
    int gen = 0;
    double mut = 0.05;
    double[][] weights = new double[wavetotal][weightstotal];
    double[][] bestWeights = new double[wavetotal][weightstotal];
    float maxDistance = 0f;

    public PlayState3(GameStateManager gsm){
        super(gsm);

        if(Gdx.graphics.getHeight() > FlappyBird.HEIGHT){
            // NeuralNetwork
            pathDataSet = "/data/data/com.flappybird/files/DataSet.tset";
            pathNetwork = "/data/data/com.flappybird/files/NeuralNetwork.nnet";
        }else{
            // NeuralNetwork
            pathDataSet = "NeurophProject_FlappyBird/Training Sets/DataSet/DataSet.tset";
            pathNetwork = "NeurophProject_FlappyBird/Neural Networks/NeuralNetwork.nnet";
        }

        // Neural Network
        nn = new NeuralNetWork(pathDataSet, pathNetwork);

        // Create NeuralNetwork
        nn.CreateMLP(layers);

        // Load NeuralNetwork
        nn.LoadMLP();

        // Generate Weights Ramdom
        RefreshWights();

        start();

        /*
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
        */
    }

    private void start(){
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

        groundPos1 = new Vector2(ground.getWidth() * (-1), GROUND_Y_OFFSET);
        groundPos2 = new Vector2(0, GROUND_Y_OFFSET);
        groundPos3 = new Vector2(ground.getWidth(), GROUND_Y_OFFSET);
        groundPos4 = new Vector2(ground.getWidth() * 2, GROUND_Y_OFFSET);
        groundPos5 = new Vector2(ground.getWidth() * 3, GROUND_Y_OFFSET);

        gameover = false;
    }

    private void SetNN(){
        for(int i=0; i<wavetotal; i++) {
            // Get weights and set NeuralNetwork on items
            for (int j=0; j<weightstotal; j++) {
                // Define weights for shots
                nn.setWeights(weights[i]);

                // Test NeuralNetwork
                //double[] output = nn.SetNN(new double[]{targetX/100});
                //angle[i] = (float) output[0];
                //power[i] = (float) output[1] * 100;
            }
        }
    }

    public float RamdomValues(float  min, float  max){
        Random b = new Random();
        return min + (max - min) * b.nextFloat();
    }

    private void WeightsRamdom(int wavetotal, int weightstotal) {
        for(int i=0; i<wavetotal; i++)
            for(int j = 0; j< weightstotal; j++)
                weights[i][j] = RamdomValues(-1.0000000000f, 1.0000000000f);
    }

    @Override
    public void handleInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            if(gameover)
                gsm.set(new PlayState3(gsm));
            else
                bird.jump();
        }

        if(Gdx.input.isTouched()) {
            if(gameover)
                gsm.set(new PlayState3(gsm));
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
                bird.colliding = true;
                gameover = true;
            }
        }

        if(bird.getY() <= ground.getHeight() + GROUND_Y_OFFSET){
            bird.colliding = true;
            gameover = true;
        }

        cam.update();
    }

    private void RefreshWights() {
        // Generate Weights Ramdom
        WeightsRamdom(wavetotal, weightstotal);

        for(int i=0; i<wavetotal; i++) {
            // Get Weights and test Neural Network
            SetNN();
        }

        nn.SaveMLP();
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

            double output = nn.TestNN(new double[]{ (difX/100), (difY/100)})[0];

            if(output > 0.5)
                bird.jump();

            if( difX > 0 && difX < 175 ) {
                sb.draw(point, ( middleX - point.getWidth() / 2 ), middleY);
                //font.draw(sb, String.format(Locale.US,"x=%01.0f y=%01.0f", middleX, middleY), middleX, middleY);
                //System.out.printf(Locale.US, "X=%06.2f, Y=%06.2f%n", difX, difY);
                //System.out.printf(Locale.US, "output = %0 20.17f%n", output );

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

        if(gameover) {
            //sb.draw(gameoverImg, cam.position.x - gameoverImg.getWidth() / 2, cam.position.y);
            //RefreshWights();


            if(bird.getX() > maxDistance) {
                maxDistance = bird.getX();
                double wtmp[] = nn.getWeights();
                bestWeights = CloneWeights(wavetotal, 1, weights, wtmp, mut);
                nn.setWeights(bestWeights[0]);

                System.out.println("---------------------------------------------------------------");
                System.out.printf(Locale.US, "distance = %f%n%s%n", maxDistance, Arrays.toString(bestWeights[0]) );
                System.out.println("---------------------------------------------------------------");
            }else{
                RefreshWights();
                System.out.printf(Locale.US, "%f%n%s%n", maxDistance, Arrays.toString(weights[0]) );
            }

            start();
        }

        sb.end();
    }

    private double[][] CloneWeights(int total, int count, double[][] rWeights, double[] weights, double mut) {
        double[][] weightsTMP = new double[total][rWeights.length];

        // Add shots in target
        System.out.println();
        for(int i = 0; i < count; i++)
            weightsTMP[i] = rWeights[i];

        // Clone left weights
        for(int i = count; i < total; i++) {
            double[] row = new double[weights.length];

            for (int j = 0; j < weights.length; j++) {
                double rnd = Math.random();
                if (rnd > mut)
                    row[j] = weights[j];
                else
                    row[j] = RamdomValues(-1.0000000000f, 1.0000000000f);
            }

            weightsTMP[i] = row;
        }

        return weightsTMP;
    }
}
