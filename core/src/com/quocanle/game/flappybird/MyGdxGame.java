package com.quocanle.game.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
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
	int score = 0;
	int scoringTube = 0;
	BitmapFont font;
	Rectangle birdRectangle;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;
	Rectangle baseRectangle;
	Sound wingSound;
	Sound hitSound;
	Sound dieSound;
	Sound pointSound;
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
		wingSound = Gdx.audio.newSound(Gdx.files.internal("sound/wing.wav"));
		hitSound = Gdx.audio.newSound(Gdx.files.internal("sound/hit.wav"));
		pointSound = Gdx.audio.newSound(Gdx.files.internal("sound/point.wav"));
		dieSound = Gdx.audio.newSound(Gdx.files.internal("sound/die.wav"));
		birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

		// tube top and bottom is between top of screen on top of base, so the max offset is the height of the screen minus the gap and the base
		maxTubeOffset = (Gdx.graphics.getHeight() + Gdx.graphics.getHeight() / 6) / 2 - gap / 2 - 500;
		random = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4; // 3/4 of the screen width
		for (int i = 0; i < numberOfTubes; i++) {
			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;
			tubeOffset[i] = (random.nextFloat() - 0.5f) * maxTubeOffset;
		}

		birdRectangle = new Rectangle();
		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];
		for (int i = 0; i < numberOfTubes; i++) {
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
		baseRectangle = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 6);

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
	}

	@Override
	public void render () {
//		if (gameState != 0) {
//
//		}
////		else {
////			if (Gdx.input.justTouched()) {
////				gameState = 1;
////			}
////		}

		ScreenUtils.clear(1, 0, 0, 1);
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 0) {
			int playBtnX = Gdx.graphics.getWidth() / 2 - playBtn.getWidth() / 2;
			int playBtnY = Gdx.graphics.getHeight() / 2 - playBtn.getHeight() / 2 - 200;
			if (Gdx.input.justTouched()) {
				int touchX = Gdx.input.getX();
				int touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // We subtract from the height because y=0 is at the top

				if (touchX >= playBtnX && touchX <= playBtnX + playBtn.getWidth() && touchY >= playBtnY && touchY <= playBtnY + playBtn.getHeight()) {
					velocity = -30;
					gameState = 1;
					wingSound.play();
				}
			}
			batch.draw(playBtn, playBtnX, playBtnY);
			batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
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
			batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
			if (Gdx.input.justTouched()) {
				velocity = -30;
				flapState = 1;
				wingSound.play();
			}

			if (velocity > 0) {
				flapState = 0;
			}

			if (birdY > 0 || velocity < 0) {
				velocity += gravity;
				birdY -= velocity;
			}

			for (int i = 0; i < numberOfTubes; i++) {
				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);
				tubeX[i] -= tubeVelocity;

				if (tubeX[i] < -topTube.getWidth()) {
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (random.nextFloat() - 0.5f) * maxTubeOffset;
				}
			}

			if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {
				score++;
				pointSound.play();
				if (scoringTube < numberOfTubes - 1) {
					scoringTube++;
				} else {
					scoringTube = 0;
				}
			}

			font.draw(batch, String.valueOf(score), Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 200);

			birdRectangle.set((Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2) + 20, birdY + 20, birds[flapState].getWidth() - 20, birds[flapState].getHeight() - 20);
			for (int i = 0; i < numberOfTubes; i++) {
				topTubeRectangles[i].set(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangles[i].set(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
			}

			// Check for collisions
			for (int i = 0; i < numberOfTubes; i++) {
				if (birdRectangle.overlaps(topTubeRectangles[i]) || birdRectangle.overlaps(bottomTubeRectangles[i])) {
					gameState = 2; // Game over
					hitSound.play();
					dieSound.play();
					break;
				} else if (birdRectangle.overlaps(baseRectangle)) {
					gameState = 2; // Game over
					hitSound.play();
					dieSound.play();
					break;
				}
			}
		}

		int playBtnX = Gdx.graphics.getWidth() / 2 - playBtn.getWidth() / 2;
		int playBtnY = Gdx.graphics.getHeight() / 2 - playBtn.getHeight() / 2 - 200;

		if (gameState == 2) {

			batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
			for (int i = 0; i < numberOfTubes; i++) {
				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);
			}

			if (Gdx.input.justTouched()) {

				int touchX = Gdx.input.getX();
				int touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // We subtract from the height because y=0 is at the top

				if (touchX >= playBtnX && touchX <= playBtnX + playBtn.getWidth() && touchY >= playBtnY && touchY <= playBtnY + playBtn.getHeight()) {
					gameState = 1;
					birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;
					for (int i = 0; i < numberOfTubes; i++) {
						tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;
						tubeOffset[i] = (random.nextFloat() - 0.5f) * maxTubeOffset;
					}
					velocity = -30;
					score = 0;
					scoringTube = 0;
					wingSound.play();
				}
			}
			// Display game over screen
			batch.draw(playBtn, playBtnX, playBtnY);
			int gameOverX = Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2;
			int gameOverY = Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2;
			batch.draw(gameOver, gameOverX, gameOverY);
			font.draw(batch, String.valueOf("Score: " + score), Gdx.graphics.getWidth() /4, Gdx.graphics.getHeight() / 4);
		}

		batch.draw(base, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 6);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
		for (int i = 0; i < birds.length; i++) {
			birds[i].dispose();
		}
		topTube.dispose();
		bottomTube.dispose();
		playBtn.dispose();
		gameOver.dispose();
		base.dispose();
		font.dispose();

	}
}
