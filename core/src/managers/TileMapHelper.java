package managers;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

import config.Storage;
import game.GameProj;
import objects.Coin;
import objects.enemies.BoarBoss;
import objects.enemies.Mlem;
import objects.enemies.Peepee;
import objects.player.PlayerArcher;
import objects.player.PlayerMage;
import objects.player.PlayerMelee;

public class TileMapHelper {
	private TiledMap map0, map1, map2, mapBoss1;
	private GameProj gameP;
	private static final float PPM = 100.0f;
    private ArrayList<Coin> coins;
    private ArrayList<TextureMapObject> bgTextures = new ArrayList<>();
	
	public TileMapHelper(GameProj gameP) {
		this.gameP = gameP;
		this.coins = new ArrayList<Coin>();
	}
	
	public MapObject getMapObjectByName(String objectName) {
        MapObjects objects = null;
        if (map0 != null) {
        	switch(objectName) {
        	case "changeChar":
        		objects = map0.getLayers().get("ChangeChar").getObjects();
        		break;
        	case "adventure":
        		objects = map0.getLayers().get("Adventure").getObjects();
        		break;
        	}          
        }

        if (objects != null) {
            for (MapObject object : objects) {
                if (object.getName() != null && object.getName().equals(objectName)) {
                    return object;
                }
            }
        }
        return null;
    }
	
	public int getCurrentMapWidth() {
        TiledMap map = getCurrentMap();
        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int mapWidth = map.getProperties().get("width", Integer.class);
        return tileWidth * mapWidth;
    }

    public int getCurrentMapHeight() {
        TiledMap map = getCurrentMap();
        int tileHeight = map.getProperties().get("tileheight", Integer.class);
        int mapHeight = map.getProperties().get("height", Integer.class);
        return tileHeight * mapHeight;
    }

    private TiledMap getCurrentMap() {
        if (map0 != null) {
            return map0;
        } else if (map1 != null) {
            return map1;
        } else if (map2 != null){
            return map2;
        } else
        	return mapBoss1;
    }
	
	public OrthogonalTiledMapRenderer setupMap(int mapNum) {
		switch(mapNum) {
		case 0:
			if(map0 == null) {
				map0 = new TmxMapLoader().load("maps/MapVillage.tmx");
				parseMapObjects(map0.getLayers().get("CollisionLayer").getObjects());
				parseMapObjects(map0.getLayers().get("Adventure").getObjects());
				parseMapObjects(map0.getLayers().get("ChangeChar").getObjects());
				parseMapObjects(map0.getLayers().get("BG_Objects1").getObjects());
				parseMapObjects(map0.getLayers().get("BG_Objects2").getObjects());
				parseMapObjects(map0.getLayers().get("BG_Objects3").getObjects());
			}			
			return new OrthogonalTiledMapRenderer(map0);			
		case 1:
			if(map1 == null) {
				map1 = new TmxMapLoader().load("maps/Map0.tmx");
				parseMapObjects(map1.getLayers().get("CollisionLayer").getObjects());
				parseMapObjects(map1.getLayers().get("Level2").getObjects());
				parseMapObjects(map1.getLayers().get("EnemyWalls").getObjects());
				parseMapObjects(map1.getLayers().get("Coins").getObjects());
				parseMapObjects(map1.getLayers().get("BG_Objects").getObjects());
			}						
			return new OrthogonalTiledMapRenderer(map1);
		case 2:
			if(map2 == null) {
				map2 = new TmxMapLoader().load("maps/Map1.tmx");
				parseMapObjects(map2.getLayers().get("CollisionLayer").getObjects());
				parseMapObjects(map2.getLayers().get("Level1").getObjects());
				parseMapObjects(map2.getLayers().get("EnemyWalls").getObjects());
				parseMapObjects(map2.getLayers().get("Death").getObjects());
				parseMapObjects(map2.getLayers().get("Coins").getObjects());
			}			
			return new OrthogonalTiledMapRenderer(map2);
		case 3:
			if(mapBoss1 == null) {
				mapBoss1 = new TmxMapLoader().load("maps/MapBoss1.tmx");
				parseMapObjects(mapBoss1.getLayers().get("CollisionLayer").getObjects());
				parseMapObjects(mapBoss1.getLayers().get("EnemyWalls").getObjects());
				parseMapObjects(mapBoss1.getLayers().get("Death").getObjects());
			}			
			return new OrthogonalTiledMapRenderer(mapBoss1);
		default:
			return new OrthogonalTiledMapRenderer(map1);
		}
	}
	
	public void printLayerIndices() {
	    TiledMap map = getCurrentMap();
	    if (map != null) {
	        for (int i = 0; i < map.getLayers().getCount(); i++) {
	            String layerName = map.getLayers().get(i).getName();
	            System.out.println("Layer index: " + i + " - Layer name: " + layerName);
	        }
	    } else {
	        System.out.println("No map is currently loaded.");
	    }
	}
	
	private void createTextureObject(TextureMapObject textureMapObject) {
	    TextureRegion region = textureMapObject.getTextureRegion();
	    Texture originalTexture = region.getTexture();

	    TextureData textureData = originalTexture.getTextureData();
	    if (!textureData.isPrepared()) {
	        textureData.prepare();
	    }

	    Texture textureWithMipMap = new Texture(textureData);
	    textureWithMipMap.setFilter(Texture.TextureFilter.MipMapNearestLinear, Texture.TextureFilter.Linear);

	    textureWithMipMap.bind();
	    Gdx.gl.glGenerateMipmap(GL20.GL_TEXTURE_2D);

	    TextureRegion newRegion = new TextureRegion(textureWithMipMap, region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight());

	    textureMapObject.setTextureRegion(newRegion);
	    bgTextures.add(textureMapObject);
	}
	
	public ArrayList<TextureMapObject> getBgTextures() {
	    return bgTextures;
	}

	private void parseMapObjects(MapObjects objects) {
		for(MapObject object : objects) {
			
			
			if(object instanceof TextureMapObject) {
				 TextureMapObject textureMapObject = (TextureMapObject) object;
		            if ("bgObjects".equals(textureMapObject.getName())) {
		                createTextureObject(textureMapObject);
		            }				
			}
			
			if(object instanceof PolygonMapObject) {
				createStaticBody((PolygonMapObject) object);			
			}
			
			if(object instanceof RectangleMapObject) {
				Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
				String rectangleName = object.getName();
				
				if (rectangleName.equals("coin")) {
	                createCoin(object);
				}
				
				if (rectangleName.equals("player")) {
				    Body body = BodyHelperService.createBody(
				        rectangle.getX() + rectangle.getWidth() / 2,
				        rectangle.getY() + rectangle.getHeight() / 2,
				        rectangle.getWidth(),
				        rectangle.getHeight(),
				        false,
				        gameP.getWorld());
				    
				    PlayerMelee playerMelee = null;
				    PlayerMage playerMage = null;
				    PlayerArcher playerArcher = null;
				    
				    if(Storage.getPlayerChar() == 1) {
					    playerMelee = new PlayerMelee(rectangle.getWidth(), rectangle.getHeight(), body, rectangle.getX(), rectangle.getY(), gameP.getWorld());
					    body.setUserData(playerMelee);
					    gameP.setPlayerMelee(playerMelee);
				    }
				    else if(Storage.getPlayerChar() == 2){
					    playerMage = new PlayerMage(rectangle.getWidth(), rectangle.getHeight(), body, rectangle.getX(), rectangle.getY(), gameP.getWorld());
					    body.setUserData(playerMage);
					    gameP.setPlayerMage(playerMage);
				    }	
				    else if(Storage.getPlayerChar() == 3){
				    	playerArcher = new PlayerArcher(rectangle.getWidth(), rectangle.getHeight(), body, rectangle.getX(), rectangle.getY(), gameP.getWorld());
					    body.setUserData(playerArcher);
					    gameP.setPlayerArcher(playerArcher);
				    }	
				}
				
				if (rectangleName.equals("peepee")) {
				    Body body = BodyHelperService.createBody(
				        rectangle.getX() + rectangle.getWidth() / 2,
				        rectangle.getY() + rectangle.getHeight() / 2,
				        rectangle.getWidth(),
				        rectangle.getHeight(),
				        false,
				        gameP.getWorld());

				    Peepee peepee = new Peepee(rectangle.getWidth(), rectangle.getHeight(), body, rectangle.getX(), rectangle.getY());
				    body.setUserData(peepee);

				    gameP.setPeepee(peepee);
				}
				
				if (rectangleName.equals("peepee2")) {
				    Body body = BodyHelperService.createBody(
				        rectangle.getX() + rectangle.getWidth() / 2,
				        rectangle.getY() + rectangle.getHeight() / 2,
				        rectangle.getWidth(),
				        rectangle.getHeight(),
				        false,
				        gameP.getWorld());

				    Peepee peepee = new Peepee(rectangle.getWidth(), rectangle.getHeight(), body, rectangle.getX(), rectangle.getY());
				    body.setUserData(peepee);

				    gameP.setPeepee2(peepee);
				}
				
				if (rectangleName.equals("peepee3")) {
				    Body body = BodyHelperService.createBody(
				        rectangle.getX() + rectangle.getWidth() / 2,
				        rectangle.getY() + rectangle.getHeight() / 2,
				        rectangle.getWidth(),
				        rectangle.getHeight(),
				        false,
				        gameP.getWorld());

				    Peepee peepee = new Peepee(rectangle.getWidth(), rectangle.getHeight(), body, rectangle.getX(), rectangle.getY());
				    body.setUserData(peepee);

				    gameP.setPeepee3(peepee);
				}
				
				if (rectangleName.equals("peepee4")) {
				    Body body = BodyHelperService.createBody(
				        rectangle.getX() + rectangle.getWidth() / 2,
				        rectangle.getY() + rectangle.getHeight() / 2,
				        rectangle.getWidth(),
				        rectangle.getHeight(),
				        false,
				        gameP.getWorld());

				    Peepee peepee = new Peepee(rectangle.getWidth(), rectangle.getHeight(), body, rectangle.getX(), rectangle.getY());
				    body.setUserData(peepee);

				    gameP.setPeepee4(peepee);
				}

				if (rectangleName.equals("mlem")) {
				    Body body = BodyHelperService.createBody(
				        rectangle.getX() + rectangle.getWidth() / 2,
				        rectangle.getY() + rectangle.getHeight() / 2,
				        rectangle.getWidth(),
				        rectangle.getHeight(),
				        false,
				        gameP.getWorld());

				    Mlem mlem = new Mlem(rectangle.getWidth(), rectangle.getHeight(), body, rectangle.getX(), rectangle.getY());
				    body.setUserData(mlem);

				    gameP.setMlem(mlem);
				}
				
				if (rectangleName.equals("mlem2")) {
				    Body body = BodyHelperService.createBody(
				        rectangle.getX() + rectangle.getWidth() / 2,
				        rectangle.getY() + rectangle.getHeight() / 2,
				        rectangle.getWidth(),
				        rectangle.getHeight(),
				        false,
				        gameP.getWorld());

				    Mlem mlem = new Mlem(rectangle.getWidth(), rectangle.getHeight(), body, rectangle.getX(), rectangle.getY());
				    body.setUserData(mlem);

				    gameP.setMlem2(mlem);
				}
				
				if (rectangleName.equals("boarBoss")) {
				    Body body = BodyHelperService.createBody(
				        rectangle.getX() + rectangle.getWidth() / 2,
				        rectangle.getY() + rectangle.getHeight() / 2,
				        rectangle.getWidth(),
				        rectangle.getHeight(),
				        false,
				        gameP.getWorld());

				    BoarBoss boar = new BoarBoss(rectangle.getWidth(), rectangle.getHeight(), body, gameP.getWorld());
				    body.setUserData(boar);
				    gameP.setBoarBoss(boar);
				}
			}
		}
	}
	
	public ArrayList<Coin> getCoins() {
        return coins;
    }
	
	private void createCoin(MapObject object) {
        Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
        Body body = BodyHelperService.createBody(
            rectangle.getX() + rectangle.getWidth() / 2,
            rectangle.getY() + rectangle.getHeight() / 2,
            rectangle.getWidth(),
            rectangle.getHeight(),
            false,
            gameP.getWorld()
        );
        
        Coin coin = new Coin(body);
        body.setUserData(coin);
        coins.add(coin);
    }

	private void createStaticBody(PolygonMapObject polyMapObject) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		Body body = gameP.getWorld().createBody(bodyDef);
		
		if("level2".equals(polyMapObject.getName())) {
			body.setUserData("level2");
		}
		if("level1".equals(polyMapObject.getName())) {
			body.setUserData("level1");
		}
		if("death".equals(polyMapObject.getName())) {
			body.setUserData("death");
		}
		if("eWall".equals(polyMapObject.getName())) {
			body.setUserData("eWall");
		}
		if("changeChar".equals(polyMapObject.getName())) {
			body.setUserData("changeChar");
		}
		if("adventure".equals(polyMapObject.getName())) {
			body.setUserData("adventure");
		}
		
		Shape shape = createPolygonShape(polyMapObject);
		body.createFixture(shape, 1000);
		shape.dispose();
	}

	private Shape createPolygonShape(PolygonMapObject polyMapObject) {
	    float[] vertices = polyMapObject.getPolygon().getTransformedVertices();
	    Vector2[] worldVertices = new Vector2[vertices.length / 2];
	    
	    for (int i = 0; i < vertices.length / 2; i++) {
	        Vector2 current = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM); // Fixed typo
	        worldVertices[i] = current;
	    }
	    
	    PolygonShape shape = new PolygonShape();
	    shape.set(worldVertices);
	    return shape;
	}
}
