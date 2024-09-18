package game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import config.GameScreen;
import config.Storage;
import managers.AnimationManager;
import managers.AnimationManager.State;

public class GameProj implements Screen {
    private Viewport vp;
    public Stage stage;
    private Game game;
    private GameScreen gameScreen;
    private Storage storage;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Rectangle player;
    private Rectangle floor;
    private Vector2 playerVelocity;
    private AnimationManager animationManager;
    private boolean facingRight = true;
    private final float gravity = -20f;
    private final float jumpSpeed = 500f;
    private final float moveSpeed = 150f;
    private boolean isJumping = false;

    public GameProj(Viewport viewport, Game game, GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.game = game;
        this.vp = viewport;
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        storage = Storage.getInstance();
        storage.createFont();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        playerVelocity = new Vector2();
        animationManager = new AnimationManager();

        createComponents();
    }

    private void createComponents() {
        player = new Rectangle();
        player.x = 100;
        player.y = 100;
        player.width = 80;
        player.height = 64;

        floor = new Rectangle();
        floor.x = 0;
        floor.y = 50;
        floor.width = vp.getWorldWidth();
        floor.height = 50;
    }

    private void handleInput() {
        boolean isRunning = false;

        if (Gdx.input.isKeyPressed(Keys.A)) {
            playerVelocity.x = -moveSpeed;
            isRunning = true;
            if (facingRight) {
                facingRight = false;
                animationManager.setFacingRight(false);
            }
        } else if (Gdx.input.isKeyPressed(Keys.D)) {
            playerVelocity.x = moveSpeed;
            isRunning = true;
            if (!facingRight) {
                facingRight = true;
                animationManager.setFacingRight(true);
            }
        } else {
            playerVelocity.x = 0;
        }

        if (Gdx.input.isKeyPressed(Keys.SPACE) && !isJumping) {
            playerVelocity.y = jumpSpeed;
            isJumping = true;
            animationManager.setState(State.JUMPING);
        }

        if (!isJumping) {
            if (isRunning) {
                animationManager.setState(State.RUNNING);
            } else if(playerVelocity.x == 0){
                animationManager.setState(State.IDLE);
            }
        }
    }


    @Override
    public void show() {
        // TODO Auto-generated method stub
    }

    @Override
    public void render(float delta) {
    	ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);

        // Apply gravity if jumping
        if (player.y > floor.y + floor.height || isJumping) {
            playerVelocity.y += gravity;
        }

        // If player has landed, reset jumping state
        if (player.y <= floor.y + floor.height) {
            player.y = floor.y + floor.height;
            playerVelocity.y = 0;
            isJumping = false;

            // Switch back to IDLE or RUNNING only after landing
            if (playerVelocity.x == 0) {
                animationManager.setState(State.IDLE);
            } else {
                animationManager.setState(State.RUNNING);
            }
        }


        handleInput();

        player.x += playerVelocity.x * Gdx.graphics.getDeltaTime();
        player.y += playerVelocity.y * Gdx.graphics.getDeltaTime();

        if (player.y < floor.y + floor.height) {
            player.y = floor.y + floor.height;
            isJumping = false;
        }

        vp.getCamera().update();

        animationManager.update(delta);

        TextureRegion currentFrame = animationManager.getCurrentFrame();

        // Begin drawing
        batch.setProjectionMatrix(vp.getCamera().combined);
        batch.begin();
        batch.draw(currentFrame, player.x, player.y, player.width, player.height);
        batch.end();

        shapeRenderer.setProjectionMatrix(vp.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.55f, 0.27f, 0.07f, 1);
        shapeRenderer.rect(floor.x, floor.y, floor.width, floor.height);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        vp.update(width, height);
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
        batch.dispose();
        shapeRenderer.dispose();
    }
}
