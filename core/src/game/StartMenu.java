package game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;

import config.GameScreen;
import config.Storage;

public class StartMenu implements Screen {

    public Stage stage;
    private Skin skin;
    private TextButton startButton, exitButton;
    private Storage storage;
    private GameScreen gameScreen;
    
    public StartMenu(Viewport viewport, Game game, GameScreen gameScreen) {
    	this.gameScreen = gameScreen;
    	stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        storage = Storage.getInstance();
        storage.createFont();
        skin = storage.skin;
        
        createComponents();       
    }

    private void createComponents() {
    	startButton = new TextButton("Start Game", skin);
        exitButton = new TextButton("Exit Game", skin);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
            	gameScreen.switchToNewState(GameScreen.HOME);
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(startButton).padBottom(20).row();
        table.add(exitButton).padBottom(20);

        stage.addActor(table);
		
	}

	@Override
    public void show() {
        // Called when this screen becomes the current screen
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // Not needed for a menu
    }

    @Override
    public void resume() {
        // Not needed for a menu
    }

    @Override
    public void hide() {
        // Called when this screen is no longer the current screen
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
