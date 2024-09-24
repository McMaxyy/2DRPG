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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import config.GameScreen;
import config.Storage;
import managers.TileMapHelper;
import objects.enemies.Mlem;
import objects.enemies.Peepee;
import objects.player.PlayerMage;
import objects.player.PlayerMelee;

public class GameProj implements Screen, ContactListener {
    private Viewport vp;
    private OrthographicCamera camera;
    public Stage stage;
    private World world;
    private final float TIME_STEP = 1/60f;
    private Storage storage;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TileMapHelper mapHelper;
    private Box2DDebugRenderer box2DDebugRenderer;
    private PlayerMelee playerMelee;
    private PlayerMage playerMage;
    private Mlem mlem, mlem2;
    private Peepee peepee, peepee2, peepee3, peepee4;
    private GameScreen gameScreen;
    
    public GameProj(Viewport viewport, Game game, GameScreen gameScreen) {
    	this.gameScreen = gameScreen;
        this.vp = viewport;
        stage = new Stage(viewport);
        this.world = new World(new Vector2(0, -9.81f), true);
        Gdx.input.setInputProcessor(stage);
        storage = Storage.getInstance();
        storage.createFont();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        this.mapHelper = new TileMapHelper(this);
        this.mapRenderer = mapHelper.setupMap(Storage.getLevelNum());
        this.box2DDebugRenderer = new Box2DDebugRenderer();
        world.setContactListener(this);

        camera = (OrthographicCamera) vp.getCamera();
        camera.setToOrtho(false, viewport.getWorldWidth(), viewport.getWorldHeight());
    }
    
    private void cameraUpdate() {
		Vector3 position = camera.position;
		if(Storage.getPlayerChar() == 1) {
			position.x = Math.round(playerMelee.getBody().getPosition().x * 100.0f * 10) / 10f;
	    	position.y = Math.round(playerMelee.getBody().getPosition().y * 100.0f * 10) / 10f;
	    }
	    else {
	    	position.x = Math.round(playerMage.getBody().getPosition().x * 100.0f * 10) / 10f;
	    	position.y = Math.round(playerMage.getBody().getPosition().y * 100.0f * 10) / 10f;
	    } 	
    	camera.position.set(position);
        camera.update();   	
    }
    
    private void renderGameOver() {
    	
//    	gameScreen.switchToNewState(GameScreen.START);
    }


    @Override
    public void render(float delta) {  
    	if (PlayerMelee.death) {
    		playerMelee.checkRespawn(); 
            return;
        }    	
    	else if (PlayerMage.death) {
    		playerMage.checkRespawn(); 
            return;
        }
    	
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);
        cameraUpdate();
        
        world.step(TIME_STEP, 6, 2);   
        
        mapRenderer.setView(camera);
        mapRenderer.render();
        
        if(playerMelee != null)
        	playerMelee.update();
        if(playerMage != null)
        	playerMage.update();
        if(peepee != null)
        	peepee.update();
        if(peepee2 != null)
        	peepee2.update();
        if(peepee3 != null)
        	peepee3.update();
        if(peepee4 != null)
        	peepee4.update();
        if(mlem != null)
        	mlem.update();
        if(mlem2 != null)
        	mlem2.update();
        
        checkForBodyDestruction();
        
        batch.setProjectionMatrix(camera.combined);       
        if(peepee != null)
        	peepee.render(batch);
        if(peepee2 != null)
        	peepee2.render(batch);
        if(peepee3 != null)
        	peepee3.render(batch);
        if(peepee4 != null)
        	peepee4.render(batch);
        if(mlem != null)
        	mlem.render(batch);
        if(mlem2 != null)
        	mlem2.render(batch);
        if(playerMelee != null)
        	playerMelee.render(batch);
        if(playerMage != null)
        	playerMage.render(batch);
        
//        box2DDebugRenderer.render(world, camera.combined.scl(100.0f));
    }

    private void checkForBodyDestruction() {
    	if (peepee != null && peepee.shouldDestroy) {
            world.destroyBody(peepee.getBody());
            peepee = null;
        }
        if (peepee2 != null && peepee2.shouldDestroy) {
            world.destroyBody(peepee2.getBody());
            peepee2 = null;
        }
        if (peepee3 != null && peepee3.shouldDestroy) {
            world.destroyBody(peepee3.getBody());
            peepee3 = null;
        }
        if (peepee4 != null && peepee4.shouldDestroy) {
            world.destroyBody(peepee4.getBody());
            peepee4 = null;
        }
        if (mlem != null && mlem.shouldDestroy) {
            world.destroyBody(mlem.getBody());
            mlem = null;
        }
        if (mlem2 != null && mlem2.shouldDestroy) {
            world.destroyBody(mlem2.getBody());
            mlem2 = null;
        }
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
        mapRenderer.dispose();
        box2DDebugRenderer.dispose();
        world.dispose();
    }

	public World getWorld() {
		return world;
	}

	public void setPlayerMelee(PlayerMelee player) {
		this.playerMelee = player;
	}
	
	public void setPlayerMage(PlayerMage player) {
		this.playerMage = player;
	}
	
	public void setPeepee(Peepee peepee) {
		this.peepee = peepee;
	}
	
	public void setPeepee2(Peepee peepee) {
		this.peepee2 = peepee;
	}
	
	public void setPeepee3(Peepee peepee) {
		this.peepee3 = peepee;
	}
	
	public void setPeepee4(Peepee peepee) {
		this.peepee4 = peepee;
	}
	
	public void setMlem(Mlem mlem) {
		this.mlem = mlem;
	}
	
	public void setMlem2(Mlem mlem) {
		this.mlem2 = mlem;
	}

	@Override
	public void beginContact(Contact contact) {
	    Fixture fixtureA = contact.getFixtureA();
	    Fixture fixtureB = contact.getFixtureB();
	    boolean isPlayerA, isPlayerB;
	    
	    boolean isMlemA = fixtureA.getBody().getUserData() instanceof Mlem;
	    boolean isMlemB = fixtureB.getBody().getUserData() instanceof Mlem;

	    boolean isPeepeeA = fixtureA.getBody().getUserData() instanceof Peepee;
	    boolean isPeepeeB = fixtureB.getBody().getUserData() instanceof Peepee;
	    
	    if(Storage.getPlayerChar() == 1) {
	    	isPlayerA = fixtureA.getBody().getUserData() instanceof PlayerMelee;
		    isPlayerB = fixtureB.getBody().getUserData() instanceof PlayerMelee;
	    }
	    else {
	    	isPlayerA = fixtureA.getBody().getUserData() instanceof PlayerMage;
		    isPlayerB = fixtureB.getBody().getUserData() instanceof PlayerMage;
	    }
	        
	    boolean isLevel2A = "level2".equals(fixtureA.getBody().getUserData());
	    boolean isLevel2B = "level2".equals(fixtureB.getBody().getUserData());
	    
	    boolean isLevel1A = "level1".equals(fixtureA.getBody().getUserData());
	    boolean isLevel1B = "level1".equals(fixtureB.getBody().getUserData());
	    
	    boolean isDeathA = "death".equals(fixtureA.getBody().getUserData());
	    boolean isDeathB = "death".equals(fixtureB.getBody().getUserData());
	    
	    boolean isEWallsA = "eWall".equals(fixtureA.getBody().getUserData());
	    boolean isEWallsB = "eWall".equals(fixtureB.getBody().getUserData());
	    
	    if ((isPlayerA && isLevel2B) || (isPlayerB && isLevel2A)) {
	        Storage.setLevelNum(2);
	        gameScreen.switchToNewState(GameScreen.HOME);
	    }
	    
	    if ((isPlayerA && isLevel1B) || (isPlayerB && isLevel1A)) {
	        Storage.setLevelNum(1);
	        gameScreen.switchToNewState(GameScreen.START);
	    }
	    
	    if (((isPlayerA && isDeathB) || (isPlayerB && isDeathA)) && Storage.getPlayerChar() == 1) {
	        PlayerMelee player = isPlayerA ? (PlayerMelee) fixtureA.getBody().getUserData() : (PlayerMelee) fixtureB.getBody().getUserData();
	        player.die();
	    }
	    else if (((isPlayerA && isDeathB) || (isPlayerB && isDeathA)) && Storage.getPlayerChar() == 2) {
	        PlayerMage player = isPlayerA ? (PlayerMage) fixtureA.getBody().getUserData() : (PlayerMage) fixtureB.getBody().getUserData();
	        player.die();
	    }	    
	    
	    if ((isPeepeeA && fixtureB.isSensor()) || (isPeepeeB && fixtureA.isSensor())) {
	        Peepee peepee = isPeepeeA ? (Peepee) fixtureA.getBody().getUserData() : (Peepee) fixtureB.getBody().getUserData();
	        peepee.die();
	    }

	    if (isPeepeeA && !isPlayerB && isEWallsB) {
	        ((Peepee) fixtureA.getBody().getUserData()).reverseDirection();
	    } else if (isPeepeeB && !isPlayerA && isEWallsA) {
	        ((Peepee) fixtureB.getBody().getUserData()).reverseDirection();
	    }
	    
	    if (((isPeepeeA && isPlayerB) || (isPeepeeB && isPlayerA)) && Storage.getPlayerChar() == 1) {
	        if ((peepee != null && !peepee.peepeeDeath) || 
	            (peepee2 != null && !peepee2.peepeeDeath) || 
	            (peepee3 != null && !peepee3.peepeeDeath) || 
	            (peepee4 != null && !peepee4.peepeeDeath)) {
	            
	            PlayerMelee player = isPlayerA ? (PlayerMelee) fixtureA.getBody().getUserData() : (PlayerMelee) fixtureB.getBody().getUserData();
	            player.die();
	        }
	    } else if (((isPeepeeA && isPlayerB) || (isPeepeeB && isPlayerA)) && Storage.getPlayerChar() == 2) {
	        if ((peepee != null && !peepee.peepeeDeath) || 
	            (peepee2 != null && !peepee2.peepeeDeath) || 
	            (peepee3 != null && !peepee3.peepeeDeath) || 
	            (peepee4 != null && !peepee4.peepeeDeath)) {
	            
	            PlayerMage player = isPlayerA ? (PlayerMage) fixtureA.getBody().getUserData() : (PlayerMage) fixtureB.getBody().getUserData();
	            player.die();
	        }
	    }
	    
	    if ((isMlemA && fixtureB.isSensor()) || (isMlemB && fixtureA.isSensor())) {
	        Mlem mlem = isMlemA ? (Mlem) fixtureA.getBody().getUserData() : (Mlem) fixtureB.getBody().getUserData();
	        mlem.die();
	    }

	    if (isMlemA && !isPlayerB && isEWallsB) {
	        ((Mlem) fixtureA.getBody().getUserData()).reverseDirection();
	    } else if (isMlemB && !isPlayerA && isEWallsA) {
	        ((Mlem) fixtureB.getBody().getUserData()).reverseDirection();
	    }
	    
	    if (((isMlemA && isPlayerB) || (isMlemB && isPlayerA)) && Storage.getPlayerChar() == 1) {
	        if ((peepee != null && !mlem.mlemDeath) || (mlem2 != null && !mlem2.mlemDeath)) {
	            PlayerMelee player = isPlayerA ? (PlayerMelee) fixtureA.getBody().getUserData() : (PlayerMelee) fixtureB.getBody().getUserData();
            	player.die();
	        }
	    }
	    else if (((isMlemA && isPlayerB) || (isMlemB && isPlayerA)) && Storage.getPlayerChar() == 2) {
	        if ((peepee != null && !mlem.mlemDeath) || (mlem2 != null && !mlem2.mlemDeath)) {
	            PlayerMage player = isPlayerA ? (PlayerMage) fixtureA.getBody().getUserData() : (PlayerMage) fixtureB.getBody().getUserData();
            	player.die();
	        }
	    }	    
	}
	
	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	    Fixture fixtureA = contact.getFixtureA();
	    Fixture fixtureB = contact.getFixtureB();
	    boolean isPlayerA, isPlayerB;
	    
	    if(Storage.getPlayerChar() == 1) {
	    	isPlayerA = fixtureA.getBody().getUserData() instanceof PlayerMelee;
		    isPlayerB = fixtureB.getBody().getUserData() instanceof PlayerMelee;
	    }
	    else {
	    	isPlayerA = fixtureA.getBody().getUserData() instanceof PlayerMage;
		    isPlayerB = fixtureB.getBody().getUserData() instanceof PlayerMage;
	    }	    
	    
	    boolean isEWallsA = "eWall".equals(fixtureA.getBody().getUserData());
	    boolean isEWallsB = "eWall".equals(fixtureB.getBody().getUserData());
	    
	    if ((isPlayerA && isEWallsB) || (isPlayerB && isEWallsA)) {
	        contact.setEnabled(false);
	    }
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
}
