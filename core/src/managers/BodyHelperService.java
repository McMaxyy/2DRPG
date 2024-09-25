package managers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class BodyHelperService {

	public static Body createBody(float x, float y, float width, float height, boolean isStatic, World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = isStatic ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(x / 100.0f, y / 100.f);
		bodyDef.fixedRotation = true;
		Body body = world.createBody(bodyDef);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2 / 100.0f, height / 2 / 100.0f);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.friction = 0;
		body.setSleepingAllowed(false);
		body.createFixture(fixtureDef);
		shape.dispose();
		return body;
	}
}
