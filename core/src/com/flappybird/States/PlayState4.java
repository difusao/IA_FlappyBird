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

public class PlayState extends State {

    private static final int GROUND_Y_OFFSET = -30;
    private static final int TUBE_SPACING = 125;
    private static final int TUBE_COUNT = 4;

    // Neural Network
    NeuralNetWork nn;
    String pathDataSet;
    String pathNetwork;
    int[] layers = new int[]{2, 2, 1};
    int weightstotal = 9;
    int wavetotal = 10;
    int bestbird = 0;
    int gen = 0;
    double mut = 0.05;
    double[][] weights = new double[wavetotal][weightstotal];
    double[][] bestWeights = new double[wavetotal][weightstotal];
    float maxDistance = 0f;
    int birdDown = 0;

    private Bird[] bird = new Bird[wavetotal];

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

    private boolean[] gameover = new boolean[wavetotal];

    BitmapFont font;

    float[] middleX = new float[TUBE_COUNT];
    float[] middleY = new float[TUBE_COUNT];
    float[] difX = new float[wavetotal];
    float[] difY = new float[wavetotal];

    public PlayState(GameStateManager gsm){
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

        // Save Network
        //nn.SaveMLP();

        // Generate Weights Ramdom
        WeightsRamdom(wavetotal, weightstotal);

        start();

        System.out.println("Lista de pesos randomicamente gerados:");
        System.out.println("---------------------------------------------------------------------");
        for(int i=0; i<wavetotal; i++) {
            System.out.printf(Locale.US, "%02d) ", i);
            for (int j = 0; j < weightstotal; j++)
                System.out.printf(Locale.US, "%017.13f, ", weights[i][j]);
            System.out.println();
        }
        System.out.println();
    }

    private void start(){
        birdDown = 0;

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f, 1.0f);

        for(int i=0; i<wavetotal; i++)
            bird[i] = new Bird(10, 200);

        point = new Texture("images/pingo.png");
        background = new Texture("images_original/bg.png");
        ground = new Texture("images_original/ground.png");
        gameoverImg = new Texture("images_original/gameover.png");

        tubes = new Array<Tube>();

        for(int i = 1; i <= TUBE_COUNT; i++)
            tubes.add(new Tube(i * (TUBE_SPACING + Tube.TUBE_WIDTH)));

        for(int i=0; i<wavetotal; i++)
            gameover[i] = false;

        groundPos1 = new Vector2(ground.getWidth() * (-1), GROUND_Y_OFFSET);
        groundPos2 = new Vector2(0, GROUND_Y_OFFSET);
        groundPos3 = new Vector2(ground.getWidth(), GROUND_Y_OFFSET);
        groundPos4 = new Vector2(ground.getWidth() * 2, GROUND_Y_OFFSET);
        groundPos5 = new Vector2(ground.getWidth() * 3, GROUND_Y_OFFSET);

        cam.position.set(bird[bestbird].getX() + 80, cam.viewportHeight / 2, 0);
    }

    private void SetNN(){
        // Get weights and set NeuralNetwork on items
        for(int i=0; i<wavetotal; i++)
            // Define weights for shots
            for (int j=0; j<weightstotal; j++)
                nn.setWeights(weights[i]);
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

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isTouched()){
            start();
        }

        /*
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
        */
    }

    @Override
    public void update(float dt) {
        handleInput();
        updateGround();

        for(int i=0; i<wavetotal; i++)
            bird[i].update(dt);

        cam.position.set(bird[bestbird].getX() + 80, cam.viewportHeight / 2, 0);

        for(Tube tube : tubes){
            if(cam.position.x - cam.viewportWidth / 2 > tube.getPosTopTube().x + tube.getTopTube().getWidth())
                tube.reposition(tube.getPosTopTube().x +((Tube.TUBE_WIDTH + TUBE_SPACING) * TUBE_COUNT));

            for(int i=0; i<wavetotal; i++) {
                if (!bird[i].colliding && tube.collides(bird[i].getBounds())) {
                    bird[i].colliding = true;
                    gameover[i] = true;
                    BirdDown();
                }
            }
        }

        for(int i=0; i<wavetotal; i++) {
            if (!bird[i].colliding && bird[i].getY() <= ground.getHeight()/2 - GROUND_Y_OFFSET) {
                bird[i].colliding = true;
                gameover[i] = true;
                BirdDown();
            }
        }

        cam.update();
    }

    private void BirdDown(){
        if(birdDown < wavetotal)
            birdDown++;

        if(birdDown == wavetotal) {
            // Best bird
            System.out.println("-----------------------------------------------------------------");
            System.out.println("Best: " + bestbird);
            System.out.printf(Locale.US, "%02d x=%f y=%f dist=%f w=%s%n", bestbird, (difX[bestbird]), (difY[bestbird]), maxDistance, Arrays.toString(weights[bestbird]));
            System.out.println();

            // Clone Weights
            weights = CloneWeights(wavetotal, weights[bestbird], mut);
            System.out.println("Clone weights:");
            System.out.println("-----------------------------------------------------------------");
            for(int i=0; i<wavetotal; i++) {
                System.out.printf(Locale.US, "%02d) ", i);
                for (int j = 0; j < weightstotal; j++)
                    System.out.printf(Locale.US, "%017.13f, ", weights[i][j]);
                System.out.println();
            }
            System.out.println();

            maxDistance = 0;
            bestbird = 0;

            start();
            //Gdx.app.exit();
        }
    }

    private void RefreshWights() {
        // Generate Weights Ramdom
        WeightsRamdom(wavetotal, weightstotal);

        for(int i=0; i<wavetotal; i++)
            // Get Weights and test Neural Network
            SetNN();

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
        sb.draw(background, cam.position.x - (cam.viewportWidth / 2), 0);

        int t = 0;

        for(Tube tube : tubes){
            sb.draw(tube.getBottomTube(), tube.getPosBottomTube().x, tube.getPosBottomTube().y);
            sb.draw(tube.getTopTube(), tube.getPosTopTube().x, tube.getPosTopTube().y);

            middleX[t] = tube.getPosTopTube().x + tube.TUBE_WIDTH / 2;
            middleY[t] = tube.getPosTopTube().y - tube.TUBE_GAP / 2;

            t++;
        }

        for (int i=0; i<wavetotal; i++) {
            for(int j=0; j<TUBE_COUNT; j++) {
                difX[i] = (middleX[j] - (bird[i].getX() - 00)) / 100;
                difY[i] = (middleY[j] - (bird[i].getY() - 82)) / 100;
            }

            if(!bird[i].colliding) {
                nn.setWeights(weights[i]);
                double output = nn.TestNN(new double[]{(difX[i]), (difY[i])})[0];
                double[] w = nn.getWeights();

                //float output = RamdomValues(0, 9);
                if (output > 0.5)
                    bird[i].jump();

                System.out.printf(Locale.US, "%02d x=%f y=%f output=%f w=%s%n", i, (difX[i]), (difY[i]), output, Arrays.toString(w) );
            }

            if (bird[i].getX() > maxDistance) {
                maxDistance = bird[i].getX();
                bestbird = i;
            }

            if(i == wavetotal-1)
                System.out.println("-------------------------------------------------------------");

            sb.draw(bird[i].getTexture(), bird[i].getX(), bird[i].getY());
        }

        sb.draw(ground, groundPos1.x, groundPos1.y);
        sb.draw(ground, groundPos2.x, groundPos2.y);
        sb.draw(ground, groundPos3.x, groundPos3.y);
        sb.draw(ground, groundPos4.x, groundPos4.y);
        sb.draw(ground, groundPos5.x, groundPos5.y);

        sb.end();
    }

    private double[][] CloneWeights(int total, double[] bestWeights, double mut) {
        double[][] weightsTMP = new double[total][bestWeights.length];

        weightsTMP[0] = bestWeights;

        // Clone left weights
        for(int i = 1; i < total; i++) {
            double[] row = new double[bestWeights.length];
            for (int j = 0; j < bestWeights.length; j++) {
                double rnd = Math.random();
                if (rnd > mut)
                    row[j] = bestWeights[j];
                else
                    row[j] = RamdomValues(-1.0000000000f, 1.0000000000f);
            }

            weightsTMP[i] = row;
        }

        return weightsTMP;
    }
}
