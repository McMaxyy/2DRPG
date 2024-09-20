package game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import config.GameScreen;
import config.Storage;
import objects.enemies.Pedro;
import objects.player.Player;
import managers.TileMapHelper;

public class GameProj implements Screen, ContactListener {
    private Viewport vp;
    private OrthographicCamera camera;
    public Stage stage;
    private World world;
    private final float TIME_STEP = 1/60f;
    private Storage storage;
    private SpriteBatch batch;
    private SpriteBatch eBatch;
    private ShapeRenderer shapeRenderer;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TileMapHelper mapHelper;
    private Box2DDebugRenderer box2DDebugRenderer;
    private Player player;
    private Pedro pedro;
    
    public GameProj(Viewport viewport, Game game, GameScreen gameScreen) {
        this.vp = viewport;
        stage = new Stage(viewport);
        this.world = new World(new Vector2(0, -9.81f), true);
        Gdx.input.setInputProcessor(stage);
        storage = Storage.getInstance();
        storage.createFont();
        batch = new SpriteBatch();
        eBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        this.mapHelper = new TileMapHelper(this);
        this.mapRenderer = mapHelper.setupMap();
        this.box2DDebugRenderer = new Box2DDebugRenderer();
        world.setContactListener(this);

        camera = (OrthographicCamera) vp.getCamera();
        camera.setToOrtho(false, viewport.getWorldWidth(), viewport.getWorldHeight());
    }
    
    private void cameraUpdate() {
    	Vector3 position = camera.position;
    	position.x = Math.round(player.getBody().getPosition().x * 100.0f * 10) / 10f;
    	position.y = Math.round(player.getBody().getPosition().y * 100.0f * 10) / 10f;
    	camera.position.set(position);
        camera.update();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);
        cameraUpdate();
        
        world.step(TIME_STEP, 6, 2);    
        
        mapRenderer.setView(camera);
        mapRenderer.render();
        player.update();
        pedro.update();
        
        batch.setProjectionMatrix(camera.combined);
        eBatch.setProjectionMatrix(camera.combined);
        player.render(batch);
        pedro.render(eBatch);
        
        box2DDebugRenderer.render(world, camera.combined.scl(100.0f));
    }

    @Override
    public void resize(int width, int height) {
        vp.update(width, height);
    }

    @Override
    public void show() {
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
        batch.dispose();
        shapeRenderer.dispose();
    }

	public World getWorld() {
		return world;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void setPedro(Pedro pedro) {
		this.pedro = pedro;
	}

	@Override
	public void beginContact(Contact contact) {
	    Fixture fixtureA = contact.getFixtureA();
	    Fixture fixtureB = contact.getFixtureB();

	    boolean isPedroA = fixtureA.getBody().getUserData() instanceof Pedro;
	    boolean isPedroB = fixtureB.getBody().getUserData() instanceof Pedro;
	    
	    boolean isPlayerA = fixtureA.getBody().getUserData() instanceof Player;
	    boolean isPlayerB = fixtureB.getBody().getUserData() instanceof Player;

	    if ((isPedroA && isPlayerB) || (isPedroB && isPlayerA)) {
	        Player player = isPlayerA ? (Player) fixtureA.getBody().getUserData() : (Player) fixtureB.getBody().getUserData();
	        player.die();
	    }
	}
	
	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
}
