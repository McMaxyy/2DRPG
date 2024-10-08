package objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

import config.Storage;

public class Coin {
    private Body body;
    private boolean collected;
    private Texture texture;

    public Coin(Body body) {
        this.body = body;
        this.collected = false;
        this.texture = Storage.assetManager.get("items/Coin.png", Texture.class);
    }

    public void update(float delta) {
    }

    public void render(SpriteBatch batch) {
        if (!collected) {
        	batch.begin();
            batch.draw(texture, body.getPosition().x * 100 - 16, body.getPosition().y * 100 - 16, 32, 32);
            batch.end();
        }
    }

    public void collect() {
        this.collected = true;
        this.body.setUserData(null);
        Storage.setPlayerCoins(Storage.getPlayerCoins() + 1);
        
        for (Fixture fixture : this.body.getFixtureList()) {
            fixture.setSensor(true);
        }
    }

    public boolean isCollected() {
        return collected;
    }
    
    public Body getBody() {
        return body;
    }
}
