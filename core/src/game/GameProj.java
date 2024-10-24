package game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import config.GameScreen;
import config.Storage;
import managers.TileMapHelper;
import objects.Coin;
import objects.GameEntity;
import objects.attacks.ArcherAttacks;
import objects.attacks.MeleeAttacks;
import objects.attacks.SpellAttacks;
import objects.enemies.BoarBoss;
import objects.enemies.Mlem;
import objects.enemies.Peepee;
import objects.player.PlayerArcher;
import objects.player.PlayerMage;
import objects.player.PlayerMelee;

public class GameProj implements Screen, ContactListener {
    private Viewport vp, hudViewport;
    private OrthographicCamera camera, hudCamera;
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
    private PlayerArcher playerArcher;
    private Mlem mlem, mlem2;
    private Peepee peepee, peepee2, peepee3, peepee4;
    private BoarBoss boarBoss;
    private GameScreen gameScreen;
    private boolean changeCharCollision, adventureCollision;
    private Label coinLabel;
    private Texture coinTex;
    
    public GameProj(Viewport viewport, Game game, GameScreen gameScreen) {
    	this.gameScreen = gameScreen;
        this.vp = viewport;
        stage = new Stage();
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

        hudCamera = new OrthographicCamera();
        hudViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), hudCamera);
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera = (OrthographicCamera) vp.getCamera();
        camera.setToOrtho(false, viewport.getWorldWidth(), viewport.getWorldHeight());
    
        coinTex = Storage.assetManager.get("items/CoinHUD.png", Texture.class);
        coinLabel = new Label("x", storage.labelStyle);
        coinLabel.setFontScale(0.8f);
        coinLabel.setPosition(55, Gdx.graphics.getHeight() - 48);
        stage.addActor(coinLabel);       
    }
    
    private void cameraUpdate() {
        Vector3 position = camera.position;

        float playerX = 0, playerY = 0;

        if (Storage.getPlayerChar() == 1) {
            playerX = playerMelee.getBody().getPosition().x * 100.0f;
            playerY = playerMelee.getBody().getPosition().y * 100.0f;
        } else if (Storage.getPlayerChar() == 2) {
            playerX = playerMage.getBody().getPosition().x * 100.0f;
            playerY = playerMage.getBody().getPosition().y * 100.0f;
        } else {
            playerX = playerArcher.getBody().getPosition().x * 100.0f;
            playerY = playerArcher.getBody().getPosition().y * 100.0f;
        }

        int mapWidth = mapHelper.getCurrentMapWidth();
        int mapHeight = mapHelper.getCurrentMapHeight(); 

        float cameraHalfWidth = camera.viewportWidth * camera.zoom / 2f;
        float cameraHalfHeight = camera.viewportHeight * camera.zoom / 2f;

        position.x = Math.round(Math.max(cameraHalfWidth, Math.min(playerX, mapWidth - cameraHalfWidth)) * 10) / 10f;
        position.y = Math.round(Math.max(cameraHalfHeight, Math.min(playerY, mapHeight - cameraHalfHeight)) * 10) / 10f;

        camera.zoom = 0.8f;
        camera.position.set(position);
        camera.update();
    }

    
    private void renderGameOver() {
    	Storage.setLevelNum(0);
    	gameScreen.switchToNewState(GameScreen.HOME);
    }
    
    private void drawInteractablePolygons() {
    	if (changeCharCollision) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            Gdx.gl.glLineWidth(3.0f);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 1, 0, 1);

            MapObject changeCharObject = mapHelper.getMapObjectByName("changeChar");
            if (changeCharObject instanceof PolygonMapObject) {
                float[] vertices = ((PolygonMapObject) changeCharObject).getPolygon().getTransformedVertices();
                for (int i = 0; i < vertices.length; i += 2) {
                    shapeRenderer.line(vertices[i], vertices[i + 1], vertices[(i + 2) % vertices.length], vertices[(i + 3) % vertices.length]);
                }
            }
            shapeRenderer.end();
            
            Gdx.gl.glLineWidth(1.0f);

            batch.begin();
            storage.font.setColor(1, 1, 0, 1);

            if (changeCharObject instanceof PolygonMapObject) {
                PolygonMapObject polygon = (PolygonMapObject) changeCharObject;
                float[] vertices = polygon.getPolygon().getTransformedVertices();
                
                float centerX = 0;
                float centerY = 0;
                for (int i = 0; i < vertices.length; i += 2) {
                    centerX += vertices[i];
                    centerY += vertices[i + 1];
                }
                centerX /= (vertices.length / 2);
                centerY /= (vertices.length / 2);

                storage.font.getData().setScale(0.6f);
                storage.font.draw(batch, "Press E to change \ncharacter", centerX - 100, centerY + 120, 200f, 1, true);
            }
            batch.end();
        }
        
        if (adventureCollision) { 
            shapeRenderer.setProjectionMatrix(camera.combined);
            Gdx.gl.glLineWidth(3.0f);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 1, 0, 1);

            MapObject adventureObject = mapHelper.getMapObjectByName("adventure");
            if (adventureObject instanceof PolygonMapObject) {
                float[] vertices = ((PolygonMapObject) adventureObject).getPolygon().getTransformedVertices();
                for (int i = 0; i < vertices.length; i += 2) {
                    shapeRenderer.line(vertices[i], vertices[i + 1], vertices[(i + 2) % vertices.length], vertices[(i + 3) % vertices.length]);
                }
            }
            shapeRenderer.end();
            
            Gdx.gl.glLineWidth(1.0f);
            
            batch.begin();
            storage.font.setColor(1, 1, 0, 1);

            if (adventureObject instanceof PolygonMapObject) {
                PolygonMapObject polygon = (PolygonMapObject) adventureObject;
                float[] vertices = polygon.getPolygon().getTransformedVertices();
                
                float centerX = 0;
                float centerY = 0;
                for (int i = 0; i < vertices.length; i += 2) {
                    centerX += vertices[i];
                    centerY += vertices[i + 1];
                }
                centerX /= (vertices.length / 2);
                centerY /= (vertices.length / 2);

                storage.font.getData().setScale(0.6f);
                storage.font.draw(batch, "Press E to interact", centerX - 100, centerY + 60);
            }
            batch.end();
        }
        
        storage.font.setColor(1, 1, 1, 1);
    }
    
    private void renderBackgroundTextures() {
        for (TextureMapObject textureObject : mapHelper.getBgTextures()) {
            Texture texture = textureObject.getTextureRegion().getTexture();
            float x = textureObject.getX();
            float y = textureObject.getY();
            float width = textureObject.getTextureRegion().getRegionWidth();
            float height = textureObject.getTextureRegion().getRegionHeight();

            batch.draw(texture, x, y, width, height);
        }
    }

    @Override
    public void render(float delta) { 
    	if (PlayerMelee.death && !Storage.isPlayerDead()) {
    		playerMelee.checkRespawn(); 
            return;
        }    	
    	else if (PlayerMage.death && !Storage.isPlayerDead()) {
    		playerMage.checkRespawn(); 
            return;
        }
    	else if (PlayerArcher.death && !Storage.isPlayerDead()) {
    		playerArcher.checkRespawn(); 
            return;
        } 
    	
    	if(Storage.isPlayerDead()) {
    		Storage.setPlayerDead(false);
    		renderGameOver();
    	}
    	
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);
        cameraUpdate();
        
        world.step(TIME_STEP, 6, 2);              
              
        mapRenderer.setView(camera);
        if(Storage.getLevelNum() != 3) {
        	mapRenderer.render(new int[]{0});
            batch.setProjectionMatrix(camera.combined); 
            batch.begin();
            renderBackgroundTextures();
            batch.end();
            mapRenderer.render(new int[]{1, 2, 3, 4, 5, 6});
        }
        else
        	mapRenderer.render();
        
        for (Coin coin : mapHelper.getCoins()) {
            coin.update(delta);
            coin.render(batch);
        }
                
        coinLabel.setText("x" + Storage.getPlayerCoins());
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        batch.draw(coinTex, 20, Gdx.graphics.getHeight() - 52, 32, 32);
        batch.end();
                
        if(playerMelee != null)
        	playerMelee.update(delta);
        if(playerMage != null)
        	playerMage.update(delta);
        if(playerArcher != null)
        	playerArcher.update(delta);
        if(peepee != null)
        	peepee.update(delta);
        if(peepee2 != null)
        	peepee2.update(delta);
        if(peepee3 != null)
        	peepee3.update(delta);
        if(peepee4 != null)
        	peepee4.update(delta);
        if(mlem != null)
        	mlem.update(delta);
        if(mlem2 != null)
        	mlem2.update(delta);
        if(boarBoss != null)
        	boarBoss.update(delta);
        
        checkForBodyDestruction();       
        
        batch.setProjectionMatrix(camera.combined); 
        drawInteractablePolygons();
        
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
        if(playerArcher != null)
        	playerArcher.render(batch);
        if(boarBoss != null)
        	boarBoss.render(batch);
        
 
        box2DDebugRenderer.render(world, camera.combined.scl(100.0f));
        
        stage.act(delta);
        stage.getViewport().apply();
        stage.draw(); 
    }

    private void checkForBodyDestruction() {
    	boolean bodyDestroyed = false;   		
    	
    	if (boarBoss != null && boarBoss.shouldDestroy) {
    		bodyDestroyed = true;
            world.destroyBody(boarBoss.getBody());
            boarBoss = null;           
        }
    	if (peepee != null && peepee.shouldDestroy) {
    		bodyDestroyed = true;
            world.destroyBody(peepee.getBody());
            peepee = null;           
        }
        if (peepee2 != null && peepee2.shouldDestroy) {
        	bodyDestroyed = true;
            world.destroyBody(peepee2.getBody());
            peepee2 = null;
        }
        if (peepee3 != null && peepee3.shouldDestroy) {
        	bodyDestroyed = true;
            world.destroyBody(peepee3.getBody());
            peepee3 = null;
        }
        if (peepee4 != null && peepee4.shouldDestroy) {
        	bodyDestroyed = true;
            world.destroyBody(peepee4.getBody());
            peepee4 = null;
        }
        if (mlem != null && mlem.shouldDestroy) {
        	bodyDestroyed = true;
            world.destroyBody(mlem.getBody());
            mlem = null;
        }
        if (mlem2 != null && mlem2.shouldDestroy) {
        	bodyDestroyed = true;
            world.destroyBody(mlem2.getBody());
            mlem2 = null;
        }
        
        if(bodyDestroyed && this.playerMage != null)
        	this.playerMage.resetMana();
	}

	@Override
    public void resize(int width, int height) {
        vp.update(width, height);
        hudViewport.update(width, height);
        hudCamera.setToOrtho(false, width, height);
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
        stage.dispose();
    }

	public World getWorld() {
		return world;
	}
	
	public void setPlayerArcher(PlayerArcher player) {
		this.playerArcher = player;
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
	
	public void setBoarBoss(BoarBoss boar) {
		this.boarBoss = boar;
	}
	
	public SpriteBatch getBatch() {
		return batch;
	}

	@Override
	public void beginContact(Contact contact) {
	    Fixture fixtureA = contact.getFixtureA();
	    Fixture fixtureB = contact.getFixtureB();
	    
	    Body bodyA = fixtureA.getBody();
	    Body bodyB = fixtureB.getBody();
	    
	    boolean isPlayerA, isPlayerB;
	    
	    boolean isMlemA = bodyA.getUserData() instanceof Mlem;
	    boolean isMlemB = bodyB.getUserData() instanceof Mlem;

	    boolean isPeepeeA = bodyA.getUserData() instanceof Peepee;
	    boolean isPeepeeB = bodyB.getUserData() instanceof Peepee;
	    
	    boolean isBoarBossA = bodyA.getUserData() instanceof BoarBoss;
	    boolean isBoarBossB = bodyB.getUserData() instanceof BoarBoss;
	    
	    if (Storage.getPlayerChar() == 1) {
	        isPlayerA = bodyA.getUserData() instanceof PlayerMelee;
	        isPlayerB = bodyB.getUserData() instanceof PlayerMelee;
	    } else if (Storage.getPlayerChar() == 2) {
	        isPlayerA = bodyA.getUserData() instanceof PlayerMage;
	        isPlayerB = bodyB.getUserData() instanceof PlayerMage;
	    }
	    else {
	        isPlayerA = bodyA.getUserData() instanceof PlayerArcher;
	        isPlayerB = bodyB.getUserData() instanceof PlayerArcher;
	    }

	    boolean isLevel2A = "level2".equals(bodyA.getUserData());
	    boolean isLevel2B = "level2".equals(bodyB.getUserData());

	    boolean isLevel1A = "level1".equals(bodyA.getUserData());
	    boolean isLevel1B = "level1".equals(bodyB.getUserData());

	    boolean isDeathA = "death".equals(bodyA.getUserData());
	    boolean isDeathB = "death".equals(bodyB.getUserData());

	    boolean isEWallsA = "eWall".equals(bodyA.getUserData());
	    boolean isEWallsB = "eWall".equals(bodyB.getUserData());
	    
	    boolean isChangeCharA = "changeChar".equals(bodyA.getUserData());
	    boolean isChangeCharB = "changeChar".equals(bodyB.getUserData());
	    
	    boolean isAdventureA = "adventure".equals(fixtureA.getBody().getUserData());
	    boolean isAdventureB = "adventure".equals(fixtureB.getBody().getUserData());
	    
	    boolean isEnemyA = bodyA.getUserData() instanceof GameEntity && bodyA.getType() == BodyDef.BodyType.DynamicBody;
	    boolean isEnemyB = bodyB.getUserData() instanceof GameEntity && bodyB.getType() == BodyDef.BodyType.DynamicBody;

	    boolean isCoinA = bodyA.getUserData() instanceof Coin;
	    boolean isCoinB = bodyB.getUserData() instanceof Coin;
	    
	    boolean isSpellA = bodyA.getUserData() instanceof SpellAttacks;
	    boolean isSpellB = bodyB.getUserData() instanceof SpellAttacks;
	    
	    boolean isWeaponA = bodyA.getUserData() instanceof MeleeAttacks;
	    boolean isWeaponB = bodyB.getUserData() instanceof MeleeAttacks;
	    
	    boolean isArrowA = bodyA.getUserData() instanceof ArcherAttacks;
	    boolean isArrowB = bodyB.getUserData() instanceof ArcherAttacks;
	    
	    boolean isProjectileA = bodyA.getUserData() instanceof BoarBoss;
	    boolean isProjectileB = bodyB.getUserData() instanceof BoarBoss;
	    
	    boolean isBossDoorA = "bossDoor".equals(bodyA.getUserData());
	    boolean isBossDoorB = "bossDoor".equals(bodyB.getUserData());

	    if ((isPlayerA && isCoinB) || (isPlayerB && isCoinA)) {
	        Coin coin = isCoinA ? (Coin) bodyA.getUserData() : (Coin) bodyB.getUserData();
	        coin.collect();
	    }
	    
	    if((isPlayerA && isChangeCharB) || (isPlayerB && isChangeCharA)) {
	    	changeCharCollision = true;
	    }
	    
	    if((isPlayerA && isAdventureB) || (isPlayerB && isAdventureA)) {
	    	adventureCollision = true;
	    }
	    
	    if ((isPlayerA && isLevel2B) || (isPlayerB && isLevel2A)) {
	        Storage.setLevelNum(2);
	        gameScreen.switchToNewState(GameScreen.HOME);
	    }

	    if ((isPlayerA && isLevel1B) || (isPlayerB && isLevel1A)) {
	        Storage.setLevelNum(3);
	        gameScreen.switchToNewState(GameScreen.HOME);
	    }

	    if (((isPlayerA && isDeathB) || (isPlayerB && isDeathA)) && Storage.getPlayerChar() == 1) {
	        PlayerMelee player = isPlayerA ? (PlayerMelee) bodyA.getUserData() : (PlayerMelee) bodyB.getUserData();
            if(!Storage.isInvulnerable())
            	player.die();
	    } else if (((isPlayerA && isDeathB) || (isPlayerB && isDeathA)) && Storage.getPlayerChar() == 2) {
	        PlayerMage player = isPlayerA ? (PlayerMage) bodyA.getUserData() : (PlayerMage) bodyB.getUserData();
            if(!Storage.isInvulnerable())
            	player.die();
	    } else if (((isPlayerA && isDeathB) || (isPlayerB && isDeathA)) && Storage.getPlayerChar() == 3) {
	    	PlayerArcher player = isPlayerA ? (PlayerArcher) bodyA.getUserData() : (PlayerArcher) bodyB.getUserData();
            if(!Storage.isInvulnerable())
            	player.die();
	    }

	    if (isPeepeeA && !isPlayerB && isEWallsB) {
	        ((Peepee) bodyA.getUserData()).reverseDirection();
	    } else if (isPeepeeB && !isPlayerA && isEWallsA) {
	        ((Peepee) bodyB.getUserData()).reverseDirection();
	    }
	    
	    if (((isBoarBossA && isPlayerB) || (isBoarBossB && isPlayerA)) && Storage.getPlayerChar() == 1) {
	        if (boarBoss != null && !boarBoss.death && boarBoss.getProjectile()) {
	            PlayerMelee player = isPlayerA ? (PlayerMelee) bodyA.getUserData() : (PlayerMelee) bodyB.getUserData();
	            if(!Storage.isInvulnerable()) {
	            	player.takeDamage(40);
	            	player.die();
	            }
	        }
	    } else if (((isBoarBossA && isPlayerB) || (isBoarBossB && isPlayerA)) && Storage.getPlayerChar() == 2) {
	        if (boarBoss != null && !boarBoss.death && boarBoss.getProjectile()) {
	            PlayerMage player = isPlayerA ? (PlayerMage) bodyA.getUserData() : (PlayerMage) bodyB.getUserData();
		        if(!Storage.isInvulnerable()) {
	            	player.takeDamage(40);
	            	player.die();
	            }
	        }
	    } else if (((isBoarBossA && isPlayerB) || (isBoarBossB && isPlayerA)) && Storage.getPlayerChar() == 3) {
	    	if (boarBoss != null && !boarBoss.death && boarBoss.getProjectile()) {
	            PlayerArcher player = isPlayerA ? (PlayerArcher) bodyA.getUserData() : (PlayerArcher) bodyB.getUserData();
	            if(!Storage.isInvulnerable()) {
	            	player.takeDamage(40);
	            	player.die();
	            }
	        }
	    }

	    if (((isPeepeeA && isPlayerB) || (isPeepeeB && isPlayerA)) && Storage.getPlayerChar() == 1) {
	        if ((peepee != null && !peepee.death) ||
	            (peepee2 != null && !peepee2.death) ||
	            (peepee3 != null && !peepee3.death) ||
	            (peepee4 != null && !peepee4.death)) {

	            PlayerMelee player = isPlayerA ? (PlayerMelee) bodyA.getUserData() : (PlayerMelee) bodyB.getUserData();
	            if(!Storage.isInvulnerable())
	            	player.die();
	        }
	    } else if (((isPeepeeA && isPlayerB) || (isPeepeeB && isPlayerA)) && Storage.getPlayerChar() == 2) {
	        if ((peepee != null && !peepee.death) ||
	            (peepee2 != null && !peepee2.death) ||
	            (peepee3 != null && !peepee3.death) ||
	            (peepee4 != null && !peepee4.death)) {

	            PlayerMage player = isPlayerA ? (PlayerMage) bodyA.getUserData() : (PlayerMage) bodyB.getUserData();
	            if(!Storage.isInvulnerable())
	            	player.die();
	        }
	    } else if (((isPeepeeA && isPlayerB) || (isPeepeeB && isPlayerA)) && Storage.getPlayerChar() == 3) {
	        if ((peepee != null && !peepee.death) ||
	            (peepee2 != null && !peepee2.death) ||
	            (peepee3 != null && !peepee3.death) ||
	            (peepee4 != null && !peepee4.death)) {

	        	PlayerArcher player = isPlayerA ? (PlayerArcher) bodyA.getUserData() : (PlayerArcher) bodyB.getUserData();
	        	if(!Storage.isInvulnerable())
	            	player.die();
	        }
	    } 
	    
	    if (isBoarBossA && !isPlayerB && isEWallsB) {
	        ((BoarBoss) bodyA.getUserData()).reverseDirection();
	    } else if (isBoarBossB && !isPlayerA && isEWallsA) {
	        ((BoarBoss) bodyB.getUserData()).reverseDirection();
	    }

	    if (isMlemA && !isPlayerB && isEWallsB) {
	        ((Mlem) bodyA.getUserData()).reverseDirection();
	    } else if (isMlemB && !isPlayerA && isEWallsA) {
	        ((Mlem) bodyB.getUserData()).reverseDirection();
	    }

	    if (((isMlemA && isPlayerB) || (isMlemB && isPlayerA)) && Storage.getPlayerChar() == 1) {
	        if ((mlem != null && !mlem.death) || (mlem2 != null && !mlem2.death)) {
	            PlayerMelee player = isPlayerA ? (PlayerMelee) bodyA.getUserData() : (PlayerMelee) bodyB.getUserData();
	            if(!Storage.isInvulnerable())
	            	player.die();
    		}
	    } else if (((isMlemA && isPlayerB) || (isMlemB && isPlayerA)) && Storage.getPlayerChar() == 2) {
	        if ((mlem != null && !mlem.death) || (mlem2 != null && !mlem2.death)) {
	            PlayerMage player = isPlayerA ? (PlayerMage) bodyA.getUserData() : (PlayerMage) bodyB.getUserData();
	            if(!Storage.isInvulnerable())
	            	player.die();
	        }
	    } else if (((isMlemA && isPlayerB) || (isMlemB && isPlayerA)) && Storage.getPlayerChar() == 3) {
	        if ((mlem != null && !mlem.death) || (mlem2 != null && !mlem2.death)) {
	        	PlayerArcher player = isPlayerA ? (PlayerArcher) bodyA.getUserData() : (PlayerArcher) bodyB.getUserData();
	        	if(!Storage.isInvulnerable())
	            	player.die();
	        }
	    }	    
	    
	    if ((isPlayerA && isBossDoorB) || (isPlayerB && isBossDoorA)) {
	    	Storage.setLevelNum(0);
	        gameScreen.switchToNewState(GameScreen.HOME);
	    }
	    
	    if (((isPlayerA && isProjectileB) || (isPlayerB && isProjectileA)) && Storage.getPlayerChar() == 1 && !boarBoss.death) {
	        PlayerMelee player = isPlayerA ? (PlayerMelee) bodyA.getUserData() : (PlayerMelee) bodyB.getUserData();
	        if(!Storage.isInvulnerable()) {
            	player.takeDamage(10);
            	boarBoss.markForRemoval();
            }
	    } else if (((isPlayerA && isProjectileB) || (isPlayerB && isProjectileA)) && Storage.getPlayerChar() == 2 && !boarBoss.death) {
	        PlayerMage player = isPlayerA ? (PlayerMage) bodyA.getUserData() : (PlayerMage) bodyB.getUserData();
	        if(!Storage.isInvulnerable()) {
            	player.takeDamage(10);
            }
	        boarBoss.markForRemoval();
	    } else if (((isPlayerA && isProjectileB) || (isPlayerB && isProjectileA)) && Storage.getPlayerChar() == 3 && !boarBoss.death) {
	    	PlayerArcher player = isPlayerA ? (PlayerArcher) bodyA.getUserData() : (PlayerArcher) bodyB.getUserData();
	    	if(!Storage.isInvulnerable()) {
            	player.takeDamage(10);
            	boarBoss.markForRemoval();
            }    	
	    }

	    if ((isSpellA && isEnemyB && !isPlayerB) || (isSpellB && isEnemyA && !isPlayerA)) {
	        SpellAttacks spell = isSpellA ? (SpellAttacks) bodyA.getUserData() : (SpellAttacks) bodyB.getUserData();

	        if (isEnemyA) {
	            GameEntity enemy = (GameEntity) bodyA.getUserData();
	            spell.dealDamage(enemy);
	        } else if (isEnemyB) {
	            GameEntity enemy = (GameEntity) bodyB.getUserData();
	            spell.dealDamage(enemy);	
	        }

	        spell.markForRemoval();
	    }
	    
	    if ((isArrowA && isEnemyB && !isPlayerB) || (isArrowB && isEnemyA && !isPlayerA)) {
	    	ArcherAttacks arrow = isArrowA ? (ArcherAttacks) bodyA.getUserData() : (ArcherAttacks) bodyB.getUserData();

	        if (isEnemyA) {
	            GameEntity enemy = (GameEntity) bodyA.getUserData();
	            arrow.dealDamage(enemy);
	            if(Storage.getLevelNum() != 3)
	            	enemy.stopEntity();
	        } else if (isEnemyB) {
	            GameEntity enemy = (GameEntity) bodyB.getUserData();
	            arrow.dealDamage(enemy);	
	            if(Storage.getLevelNum() != 3)
	            	enemy.stopEntity();
	        }

	        arrow.markForRemoval();
	    }

	    if ((isWeaponA && isEnemyB && !isPlayerB) || (isWeaponB && isEnemyA && !isPlayerA)) {
	    	MeleeAttacks weapon = isWeaponA ? (MeleeAttacks) bodyA.getUserData() : (MeleeAttacks) bodyB.getUserData();

	        if (isEnemyA) {
	            GameEntity enemy = (GameEntity) bodyA.getUserData();
	            weapon.dealDamage(enemy);
	            if(Storage.getLevelNum() != 3)
	            	enemy.stopEntity();
	        } else if (isEnemyB) {
	            GameEntity enemy = (GameEntity) bodyB.getUserData();
	            weapon.dealDamage(enemy);
	            if(Storage.getLevelNum() != 3)
	            	enemy.stopEntity();
	        }
	    }
	}
	
	@Override
	public void endContact(Contact contact) {
	    Fixture fixtureA = contact.getFixtureA();
	    Fixture fixtureB = contact.getFixtureB();

	    Body bodyA = fixtureA.getBody();
	    Body bodyB = fixtureB.getBody();

	    boolean isPlayerA = bodyA.getUserData() instanceof PlayerMelee || bodyA.getUserData() instanceof PlayerMage || bodyA.getUserData() instanceof PlayerArcher;
	    boolean isPlayerB = bodyB.getUserData() instanceof PlayerMelee || bodyB.getUserData() instanceof PlayerMage || bodyB.getUserData() instanceof PlayerArcher;

	    boolean isChangeCharA = "changeChar".equals(bodyA.getUserData());
	    boolean isChangeCharB = "changeChar".equals(bodyB.getUserData());
	    
	    boolean isAdventureA = "adventure".equals(fixtureA.getBody().getUserData());
	    boolean isAdventureB = "adventure".equals(fixtureB.getBody().getUserData());

	    if ((isPlayerA && isChangeCharB) || (isPlayerB && isChangeCharA)) {
	        changeCharCollision = false;
	    }
	    
	    if ((isPlayerA && isAdventureB) || (isPlayerB && isAdventureA)) {
	    	adventureCollision = false;
	    }
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	    Fixture fixtureA = contact.getFixtureA();
	    Fixture fixtureB = contact.getFixtureB();
	    
	    Body bodyA = fixtureA.getBody();
	    Body bodyB = fixtureB.getBody();
	    
	    boolean isPlayerA, isPlayerB;
	    
	    if(Storage.getPlayerChar() == 1) {
	    	isPlayerA = fixtureA.getBody().getUserData() instanceof PlayerMelee;
		    isPlayerB = fixtureB.getBody().getUserData() instanceof PlayerMelee;
	    }
	    else if(Storage.getPlayerChar() == 2) {
	    	isPlayerA = fixtureA.getBody().getUserData() instanceof PlayerMage;
		    isPlayerB = fixtureB.getBody().getUserData() instanceof PlayerMage;
	    }
	    else {
	    	isPlayerA = fixtureA.getBody().getUserData() instanceof PlayerArcher;
		    isPlayerB = fixtureB.getBody().getUserData() instanceof PlayerArcher;
	    }	
	    
	    boolean isMlemA = bodyA.getUserData() instanceof Mlem;
	    boolean isMlemB = bodyB.getUserData() instanceof Mlem;

	    boolean isPeepeeA = bodyA.getUserData() instanceof Peepee;
	    boolean isPeepeeB = bodyB.getUserData() instanceof Peepee;
	    
	    boolean isCoinA = bodyA.getUserData() instanceof Coin;
	    boolean isCoinB = bodyB.getUserData() instanceof Coin;
	    
	    boolean isEWallsA = "eWall".equals(fixtureA.getBody().getUserData());
	    boolean isEWallsB = "eWall".equals(fixtureB.getBody().getUserData());
	    
	    boolean isAdventureA = "adventure".equals(fixtureA.getBody().getUserData());
	    boolean isAdventureB = "adventure".equals(fixtureB.getBody().getUserData());
	    
	    boolean isChangeCharA = "changeChar".equals(fixtureA.getBody().getUserData());
	    boolean isChangeCharB = "changeChar".equals(fixtureB.getBody().getUserData());
	    	 	    
	    if ((isPlayerA && isEWallsB) || (isPlayerB && isEWallsA) ||
	    		(isPlayerA && isAdventureB) || (isPlayerB && isAdventureA) ||
	    		(isPlayerA && isChangeCharB) || (isPlayerB && isChangeCharA)) {
	        contact.setEnabled(false);
	    }
	    
	    if ((isMlemA && isCoinB) || (isMlemB && isCoinA) ||
	    		(isPeepeeA && isCoinB) || (isPeepeeB && isCoinA) ||
	    		(isPlayerA && isCoinB) || (isPlayerB && isCoinA)) {
	        contact.setEnabled(false);
	    }
	    
	    if ((isPlayerA && isAdventureB) || (isPlayerB && isAdventureA)) {
	    	if(Gdx.input.isKeyJustPressed(Input.Keys.E)) {
	    		contact.setEnabled(true);
	    		Storage.setLevelNum(1);
		        gameScreen.switchToNewState(GameScreen.HOME);
	    	}	    		
	    }
	    
	    if ((isPlayerA && isChangeCharB) || (isPlayerB && isChangeCharA)) {
	    	if(Gdx.input.isKeyJustPressed(Input.Keys.E)) {
	    		contact.setEnabled(true);
	    		if(Storage.getPlayerChar() == 1) {
	    			Storage.setPlayerChar(2);
	    			gameScreen.switchToNewState(GameScreen.HOME);
	    		}
	    		else if(Storage.getPlayerChar() == 2){
	    			Storage.setPlayerChar(3);
	    			gameScreen.switchToNewState(GameScreen.HOME);
	    		}
	    		else if(Storage.getPlayerChar() == 3){
	    			Storage.setPlayerChar(1);
	    			gameScreen.switchToNewState(GameScreen.HOME);
	    		}
	    	}	    		
	    }
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
}
