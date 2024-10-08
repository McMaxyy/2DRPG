package game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;

import config.GameScreen;
import config.Storage;

public class LoadingScreen implements Screen {
	Viewport vp;
	Skin skin;
	private Game game;
    public Stage stage;
    private ProgressBar progressBar;
    private GameScreen gameScreen;
    private Storage storage;
    private Label loading;
    
    public LoadingScreen(Viewport viewport, Game game, GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.game = game;
        stage = new Stage(viewport);
        vp = viewport;
        Gdx.input.setInputProcessor(stage);
        storage = Storage.getInstance();
        storage.createFont();
        skin = storage.skin;

        // Create a progress bar using the default skin
        float progressBarWidth = vp.getWorldWidth() / 2f;
        float progressBarHeight = 50f;
        progressBar = new ProgressBar(0, 1, 0.01f, false, skin, "default-horizontal");

        // Calculate the position for the progress bar at the center of the screen
        float progressBarX = (vp.getWorldWidth() - progressBarWidth) / 2f;
        float progressBarY = (vp.getWorldHeight() - progressBarHeight) / 2f;
        progressBar.setBounds(progressBarX, progressBarY, progressBarWidth, progressBarHeight);

        // Create the loading label
        loading = new Label("Loading...", storage.labelStyle);
        float loadingX = progressBarX + progressBarWidth / 2.5f;
        float loadingY = progressBarY - 70f;
        loading.setPosition(loadingX, loadingY);

        stage.addActor(progressBar);
        stage.addActor(loading);
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(10 / 255f, 10 / 255f, 10 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Storage.assetManager.update()) {
        	Storage.setLevelNum(0);
	        gameScreen.switchToNewState(GameScreen.HOME);
        } else {
            progressBar.setValue(Storage.assetManager.getProgress());
            loading.setText("Loading..." + (int) (progressBar.getValue() * 100) + "%");
        }

        stage.act();
        stage.draw();
    }

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}
}
