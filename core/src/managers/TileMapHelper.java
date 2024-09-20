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
import objects.player.Player;

public class TileMapHelper {
	private TiledMap tiledMap;
	private GameProj gameP;
	private static final float PPM = 100.0f; // adjust this value based on your game's scaling
	
	public TileMapHelper(GameProj gameP) {
		this.gameP = gameP;
	}
	
	public OrthogonalTiledMapRenderer setupMap() {
		tiledMap = new TmxMapLoader().load("maps/Map0.tmx");
		parseMapObjects(tiledMap.getLayers().get("CollisionLayer").getObjects());
		return new OrthogonalTiledMapRenderer(tiledMap);
	}

	private void parseMapObjects(MapObjects objects) {
		for(MapObject object : objects) {
			if(object instanceof PolygonMapObject) {
				createStaticBody((PolygonMapObject) object);
			}
			
			if(object instanceof RectangleMapObject) {
				Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
				String rectangleName = object.getName();
				
				if(rectangleName.equals("player")) {
					Body body = BodyHelperService.createBody(
							rectangle.getX() + rectangle.getWidth() / 2,
							rectangle.getY() + rectangle.getHeight() / 2,
							rectangle.getWidth(),
							rectangle.getHeight(),
							false,
							gameP.getWorld());
					gameP.setPlayer(new Player(rectangle.getWidth(), rectangle.getHeight(), body));
				}
			}
		}
	}
	
	private void createStaticBody(PolygonMapObject polyMapObject) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		Body body = gameP.getWorld().createBody(bodyDef);
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
	    
	    PolygonShape shape = new PolygonShape(); // Added parentheses
	    shape.set(worldVertices);
	    return shape;
	}
}
