package managers;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
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
import objects.enemies.Mlem;
import objects.enemies.Peepee;
import objects.player.PlayerMage;
import objects.player.PlayerMelee;

public class TileMapHelper {
	private TiledMap map1, map2;
	private GameProj gameP;
	private static final float PPM = 100.0f;
	
	public TileMapHelper(GameProj gameP) {
		this.gameP = gameP;
	}
	
	public OrthogonalTiledMapRenderer setupMap(int mapNum) {
		switch(mapNum) {
		case 1:
			map1 = new TmxMapLoader().load("maps/Map0.tmx");
			parseMapObjects(map1.getLayers().get("CollisionLayer").getObjects());
			parseMapObjects(map1.getLayers().get("Level2").getObjects());
			parseMapObjects(map1.getLayers().get("EnemyWalls").getObjects());
			return new OrthogonalTiledMapRenderer(map1);
		case 2:
			map2 = new TmxMapLoader().load("maps/Map1.tmx");
			parseMapObjects(map2.getLayers().get("CollisionLayer").getObjects());
			parseMapObjects(map2.getLayers().get("Level1").getObjects());
			parseMapObjects(map2.getLayers().get("EnemyWalls").getObjects());
			parseMapObjects(map2.getLayers().get("Death").getObjects());
			return new OrthogonalTiledMapRenderer(map2);
		default:
			return new OrthogonalTiledMapRenderer(map1);
		}
	}

	private void parseMapObjects(MapObjects objects) {
		for(MapObject object : objects) {
			if(object instanceof PolygonMapObject) {
				createStaticBody((PolygonMapObject) object);			
			}
			
			if(object instanceof RectangleMapObject) {
				Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
				String rectangleName = object.getName();
				
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
				    
				    if(Storage.getPlayerChar() == 1) {
					    playerMelee = new PlayerMelee(rectangle.getWidth(), rectangle.getHeight(), body, rectangle.getX(), rectangle.getY(), gameP.getWorld());
					    body.setUserData(playerMelee);
					    gameP.setPlayerMelee(playerMelee);
				    }
				    else {
					    playerMage = new PlayerMage(rectangle.getWidth(), rectangle.getHeight(), body, rectangle.getX(), rectangle.getY(), gameP.getWorld());
					    body.setUserData(playerMage);
					    gameP.setPlayerMage(playerMage);
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
			}
		}
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
