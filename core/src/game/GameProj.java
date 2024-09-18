package game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import config.GameScreen;
import config.Storage;
import managers.AnimationManager;
import managers.AnimationManager.State;

public class GameProj implements Screen {
    private Viewport vp;
    private OrthographicCamera camera;
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
    private final float moveSpeed = 300f;
    private boolean isJumping = false;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

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

        map = new TmxMapLoader().load("maps/Map0.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        camera = (OrthographicCamera) vp.getCamera();
        camera.setToOrtho(false, viewport.getWorldWidth(), viewport.getWorldHeight());

        createComponents();
    }

    private void createComponents() {
        player = new Rectangle();
        player.x = 100;
        player.y = 100;
        player.width = 120;
        player.height = 90;

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

        animationManager.update(Gdx.graphics.getDeltaTime());
    }
    
    private void handleMapCollisions(float delta) {
        MapLayer collisionLayer = map.getLayers().get("collision");

        if (collisionLayer == null) {
            return; // No collision layer found
        }

        boolean collidedVertically = false;
        boolean collidedHorizontally = false;

        // Small epsilon value to avoid side collisions when standing on top of objects
        float epsilon = 0.1f;

        for (MapObject object : collisionLayer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();

                // Handle vertical collisions first (gravity-related)
                if (!collidedVertically && Intersector.overlaps(getVerticalPlayerRectangle(delta), rect)) {
                    collidedVertically = true;
                    if (playerVelocity.y < 0) { // Falling
                        player.y = rect.y + rect.height;
                        playerVelocity.y = 0;
                        isJumping = false; // Reset jump state
                    } else if (playerVelocity.y > 0) { // Jumping
                        player.y = rect.y - player.height;
                        playerVelocity.y = 0;
                    }
                }

                // Handle horizontal collisions if not falling or jumping significantly
                if (!collidedHorizontally && Intersector.overlaps(getHorizontalPlayerRectangle(delta), rect)) {
                    collidedHorizontally = true;
                    System.out.println("2");
                    // Adjust player position and stop horizontal velocity
                    if (playerVelocity.x > 0) { // Moving right
                        player.x = rect.x - player.width;
                    } else if (playerVelocity.x < 0) { // Moving left
                        player.x = rect.x + rect.width;
                    }
                    playerVelocity.x = 0;
                }
            }
        }
    }
    
//    private void handleMapCollisions(float delta) {
//        MapLayer collisionLayer = map.getLayers().get("collision");
//
//        if (collisionLayer == null) {
//            return; // No collision layer found
//        }
//
//        boolean collidedVertically = false;
//        boolean collidedHorizontally = false;
//
//        for (MapObject object : collisionLayer.getObjects()) {
//            if (object instanceof RectangleMapObject) {
//                Rectangle rect = ((RectangleMapObject) object).getRectangle();
//
//                // Handle horizontal collisions
//                if (!collidedHorizontally && Intersector.overlaps(getHorizontalPlayerRectangle(delta), rect)) {
//                    collidedHorizontally = true;
//                    System.out.println("Horizontal");
//                    // Adjust player position and stop horizontal velocity
//                    if (playerVelocity.x > 0) { // Moving right
//                        player.x = rect.x - player.width ;
//                    } else if (playerVelocity.x < 0) { // Moving left
//                        player.x = rect.x + rect.width;
//                    }
//                    playerVelocity.x = 0;
//                }
//
//                // Handle vertical collisions
//                if (!collidedVertically && Intersector.overlaps(getVerticalPlayerRectangle(delta), rect)) {
//                    collidedVertically = true;
//                    System.out.println("Verical");
//                    if (playerVelocity.y < 0) { // Falling
//                        player.y = rect.y + rect.height;
//                        playerVelocity.y = 0;
//                        isJumping = false; // Reset jump state
//                    } else if (playerVelocity.y > 0) { // Jumping
//                        player.y = rect.y - player.height;
//                        playerVelocity.y = 0;
//                    }
//                }
//            } else if (object instanceof PolygonMapObject) {
//                Polygon polygon = ((PolygonMapObject) object).getPolygon();
//
//                // Handle horizontal polygon collisions
//                if (!collidedHorizontally && Intersector.overlapConvexPolygons(getHorizontalPlayerPolygon(delta), polygon)) {
//                    collidedHorizontally = true;
//                    if (playerVelocity.x > 0) { // Moving right
//                        player.x = polygon.getBoundingRectangle().x - player.width - 0.1f;
//                    } else if (playerVelocity.x < 0) { // Moving left
//                        player.x = polygon.getBoundingRectangle().x + polygon.getBoundingRectangle().width + 0.1f;
//                    }
//                    playerVelocity.x = 0;
//                }
//
//                // Handle vertical polygon collisions
//                if (!collidedVertically && Intersector.overlapConvexPolygons(getVerticalPlayerPolygon(delta), polygon)) {
//                    collidedVertically = true;
//                    if (playerVelocity.y < 0) { // Falling
//                        player.y = polygon.getBoundingRectangle().y + polygon.getBoundingRectangle().height + 0.1f;
//                        playerVelocity.y = 0;
//                        isJumping = false;
//                    } else if (playerVelocity.y > 0) { // Jumping
//                        player.y = polygon.getBoundingRectangle().y - player.height - 0.1f;
//                        playerVelocity.y = 0;
//                    }
//                }
//            }
//        }
//    }
    
    private Rectangle getHorizontalPlayerRectangle(float delta) {
        return new Rectangle(player.x + playerVelocity.x * delta, player.y, player.width, player.height);
    }

    private Rectangle getVerticalPlayerRectangle(float delta) {
        return new Rectangle(player.x, player.y + playerVelocity.y * delta, player.width, player.height);
    }

    private Polygon getHorizontalPlayerPolygon(float delta) {
        float[] vertices = new float[] {
            player.x + playerVelocity.x * delta, player.y, // bottom-left
            player.x + player.width + playerVelocity.x * delta, player.y, // bottom-right
            player.x + player.width + playerVelocity.x * delta, player.y + player.height, // top-right
            player.x + playerVelocity.x * delta, player.y + player.height // top-left
        };
        return new Polygon(vertices);
    }

    // Helper method to convert the player rectangle into a polygon for vertical collisions
    private Polygon getVerticalPlayerPolygon(float delta) {
        float[] vertices = new float[] {
            player.x, player.y + playerVelocity.y * delta, // bottom-left
            player.x + player.width, player.y + playerVelocity.y * delta, // bottom-right
            player.x + player.width, player.y + player.height + playerVelocity.y * delta, // top-right
            player.x, player.y + player.height + playerVelocity.y * delta // top-left
        };
        return new Polygon(vertices);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);

        playerVelocity.y += gravity;

        handleInput();

        player.y += playerVelocity.y * delta;
        player.x += playerVelocity.x * delta;

        handleMapCollisions(delta);

        camera.position.set(player.x + player.width / 2, player.y + player.height / 2, 0);
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        TextureRegion currentFrame = animationManager.getCurrentFrame();
        batch.draw(currentFrame, player.x, player.y, player.width, player.height);
        batch.end();

        // Call debug drawing after the main rendering
        debugDraw(delta);
    }

    private void debugDraw(float delta) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Draw player's bounding box (rectangle)
        shapeRenderer.setColor(1, 0, 0, 1); // Red for the player
        shapeRenderer.rect(player.x, player.y, player.width, player.height);

        // Draw the collision objects from the map
        MapLayer collisionLayer = map.getLayers().get("collision");
        if (collisionLayer != null) {
            shapeRenderer.setColor(0, 1, 0, 1); // Green for map collision objects

            for (MapObject object : collisionLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
                } else if (object instanceof PolygonMapObject) {
                    Polygon polygon = ((PolygonMapObject) object).getPolygon();
                    shapeRenderer.polygon(polygon.getTransformedVertices());
                }
            }
        }

        shapeRenderer.end();
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
        map.dispose();
        mapRenderer.dispose();
    }
}
