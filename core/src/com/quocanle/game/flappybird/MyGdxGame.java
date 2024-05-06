package com.quocanle.game.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background, topTube, bottomTube, playBtn, gameOver, base;
	Texture[] birds;
	int flapState = 0;
	float timeToFlap = 0;
	float birdY = 0;
	float velocity = 0;
	int gameState = 0;
	int gravity = 2;
	float gap = 400; // Gap between top and bottom tubes
	float maxTubeOffset; // Maximum offset of the tube's position
	Random random;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;
	float tubeVelocity = 4;
//	float[] backgroundX = new float[2];
	int score = 0;
	int scoringTube = 0;
	BitmapFont font;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		playBtn = new Texture("playbtn.png");
		gameOver = new Texture("game_over.png");
		base = new Texture("base.png");
		birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 200;
		random = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4; // Distance between tubes
		for (int i = 0; i < numberOfTubes; i++) {
			tubeOffset[i] = maxTubeOffset * (random.nextFloat() - 0.4f);
			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + i * distanceBetweenTubes;
		}
//
//		for (int i = 0; i < 2; i++) {
//			backgroundX[i] = i * background.getWidth();
//		}

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
	}

	@Override
	public void render () {
		if (gameState != 0) {
			if (Gdx.input.justTouched()) {
				velocity = -30;
				flapState = 1;
			}

			if (velocity > 0) {
				flapState = 0;
			}

			if (birdY > 0 || velocity < 0) {
				velocity += gravity;
				birdY -= velocity;
			}
		}
//		else {
//			if (Gdx.input.justTouched()) {
//				gameState = 1;
//			}
//		}


		int playBtnX = Gdx.graphics.getWidth() / 2 - playBtn.getWidth() / 2;
		int playBtnY = Gdx.graphics.getHeight() / 2 - playBtn.getHeight() / 2 - 200;
		if (Gdx.input.justTouched()) {
			int touchX = Gdx.input.getX();
			int touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // We subtract from the height because y=0 is at the top

			if (touchX >= playBtnX && touchX <= playBtnX + playBtn.getWidth() && touchY >= playBtnY && touchY <= playBtnY + playBtn.getHeight()) {
				velocity = -30;
				gameState = 1;
			}
		}

		if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {
			score++;
			if (scoringTube < numberOfTubes - 1) {
				scoringTube++;
			} else {
				scoringTube = 0;
			}
		}

		ScreenUtils.clear(1, 0, 0, 1);
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
		if (gameState == 0) {
			batch.draw(playBtn, playBtnX, playBtnY);
			timeToFlap += Gdx.graphics.getDeltaTime();
			if (timeToFlap > 0.5f) { // Change the flapState every 0.5 seconds
				if (flapState == 0) {
					flapState = 1;
				} else {
					flapState = 0;
				}
				timeToFlap = 0;
			}
		}

		if (gameState == 1){
			for (int i = 0; i < numberOfTubes; i++) {
				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);
				tubeX[i] -= tubeVelocity;

				if (tubeX[i] < -topTube.getWidth()) {
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
				}
			}

			font.draw(batch, String.valueOf(score), Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 200);
		}

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
