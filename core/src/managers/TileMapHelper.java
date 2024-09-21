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

import game.GameProj;
import objects.enemies.Pedro;
import objects.player.Player;

public class TileMapHelper {
	private TiledMap map1, map2;
	private GameProj gameP;
	private static final float PPM = 100.0f;
	
	public TileMapHelper(GameProj gameP) {
		this.gameP = gameP;
	}
	
	public OrthogonalTiledMapRenderer setupMap(int mapNum) {
//		map1 = new TmxMapLoader().load("maps/Map0.tmx");
//		parseMapObjects(map1.getLayers().get("CollisionLayer").getObjects());
//		parseMapObjects(map1.getLayers().get("Level2").getObjects());
//		
//		map2 = new TmxMapLoader().load("maps/Map1.tmx");
//		parseMapObjects(map2.getLayers().get("CollisionLayer").getObjects());
		
		switch(mapNum) {
		case 1:
			map1 = new TmxMapLoader().load("maps/Map0.tmx");
			parseMapObjects(map1.getLayers().get("CollisionLayer").getObjects());
			parseMapObjects(map1.getLayers().get("Level2").getObjects());
			return new OrthogonalTiledMapRenderer(map1);
		case 2:
			map2 = new TmxMapLoader().load("maps/Map1.tmx");
			parseMapObjects(map2.getLayers().get("CollisionLayer").getObjects());
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

				    Player player = new Player(rectangle.getWidth(), rectangle.getHeight(), body, rectangle.getX(), rectangle.getY(), gameP.getWorld());
				    body.setUserData(player);
				    gameP.setPlayer(player);
				}
				
				if (rectangleName.equals("pedro")) {
				    Body body = BodyHelperService.createBody(
				        rectangle.getX() + rectangle.getWidth() / 2,
				        rectangle.getY() + rectangle.getHeight() / 2,
				        rectangle.getWidth(),
				        rectangle.getHeight(),
				        false,
				        gameP.getWorld());

				    Pedro pedro = new Pedro(rectangle.getWidth(), rectangle.getHeight(), body, rectangle.getX(), rectangle.getY());
				    body.setUserData(pedro);

				    gameP.setPedro(pedro);
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
