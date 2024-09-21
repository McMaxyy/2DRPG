package config;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import game.GameProj;
import game.StartMenu;

public class GameScreen implements Screen {
    private Game game;
    private Viewport viewport;
    private GameProj gameP;
    private StartMenu startMenu;

    private static final int MIN_WIDTH = 1280;
    private static final int MIN_HEIGHT = 720;
    public static int SELECTED_WIDTH = MIN_WIDTH;
    public static int SELECTED_HEIGHT = MIN_HEIGHT;

    private int currentState;
    public static final int HOME = 1;
    public static final int START = 2;

    public GameScreen(Game game) {
        this.game = game;
        // Initialize viewport with the correct selected dimensions
        viewport = new FitViewport(SELECTED_WIDTH, SELECTED_HEIGHT);
        Gdx.graphics.setUndecorated(false);
        Gdx.graphics.setWindowedMode(1280, 720);
        setCurrentState(HOME);
    }

    public void setCurrentState(int newState) {
        currentState = newState;

        switch (currentState) {
	        case HOME:
	            gameP = new GameProj(viewport, game, this);
	            Gdx.input.setInputProcessor(gameP.stage);
	            break;
	        case START:
	            startMenu = new StartMenu(viewport, game, this);
	            Gdx.input.setInputProcessor(startMenu.stage);
	            break;
        }
    }
    
    public void switchToNewState(int scene) {
        setCurrentState(scene);
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub

    }

    @Override
    public void render(float delta) {
        // Clear the screen with the background color
        Gdx.gl.glClearColor(55 / 255f, 55 / 255f, 55 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        switch (currentState) {
            case HOME:
                gameP.render(delta);
                break;
            case START:
                startMenu.render(delta);
                break;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        viewport.apply();
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        gameP.dispose();
        startMenu.dispose();
    }
}
