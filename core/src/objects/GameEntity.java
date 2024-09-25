package objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

import config.Storage;
import objects.player.PlayerMage;

public abstract class GameEntity {
	
	protected float x, y, velX, velY, speed;
	protected float width, height;
	protected Body body;
	protected int hp, maxHP, mana, maxMana;
	private boolean isStopped = false;
    private float stopDuration = 0.5f;
    private float stopTimer = 0f;
    private float originalVelX, originalVelY; 
	
	public GameEntity(float width, float height, Body body) {
		this.x = body.getPosition().x;
		this.y = body.getPosition().y;
		this.width = width;
		this.height = height;
		this.body = body;
		this.velX = 0;
		this.velY = 0;
		this.speed = 0;
		this.hp = 0;
		this.maxHP = 0;
	}
	
	public void setMana(int mana, int maxMana) {
		this.maxMana = maxMana;
		this.mana = Math.min(mana, maxMana);
	}
	
	public void loseMana(int num) {
		mana = Math.max(mana - num, 0);
	}
	
    public void setHealth(int hp, int maxHP) {
        this.maxHP = maxHP;
        this.hp = Math.min(hp, maxHP);
    }

    public void takeDamage(int amount) {
        hp = Math.max(hp - amount, 0);
        if (hp == 0) {
            onDeath();
        }
    }

    public void heal(int amount) {
        hp = Math.min(hp + amount, maxHP);
    }

    protected void onDeath() {
    	
    }
    
    public int getMana() {
    	return mana;
    }
    
    public int getMaxMana() {
    	return maxMana;
    }

    public int getHealth() {
        return hp;
    }

    public int getMaxHealth() {
        return maxHP;
    }
    
    public void stopEntity() {
        if (!isStopped) {
            originalVelX = velX;
            originalVelY = velY;
            velX = 0;
            velY = 0;
            isStopped = true;
            stopTimer = 0f;
        }
    }
    
    public boolean isStopped() {
        return isStopped;
    }

    public void updateStopTimer(float deltaTime) {
        if (isStopped) {
            stopTimer += deltaTime;
            if (stopTimer >= stopDuration) {
                velX = originalVelX;
                velY = originalVelY;
                isStopped = false;
            }
        }
    }
    
    public void drawHealthBar(SpriteBatch batch) {
        float healthBarWidth = width * 2f;
        float healthBarHeight = 0.3f * height;
        float healthPercentage = (float) hp / maxHP;

        float healthBarX = body.getPosition().x * 100 - healthBarWidth / 2;
        float healthBarY = body.getPosition().y * 100 + height * 0.75f;

        batch.setColor(Color.BLACK);
        batch.draw(TextureManager.getPixel(), healthBarX, healthBarY, healthBarWidth, healthBarHeight);

        batch.setColor(Color.RED);
        batch.draw(TextureManager.getPixel(), healthBarX, healthBarY, healthBarWidth * healthPercentage, healthBarHeight);

        batch.setColor(Color.WHITE);

        BitmapFont font = Storage.getInstance().getFont();
        
        font.getData().setScale(0.5f);

        String healthText = hp + " / " + maxHP;
        GlyphLayout layout = new GlyphLayout(font, healthText);

        font.draw(batch, healthText, healthBarX + (healthBarWidth - layout.width) / 2, healthBarY + healthBarHeight - 2);
    }
    
    public void drawManaBar(SpriteBatch batch) {
        float barWidth = width * 2f;
        float barHeight = 0.2f * height;
        float manaPercentage = (float) mana / maxMana;

        float barX = body.getPosition().x * 100 - barWidth / 2;
        float manaBarY = body.getPosition().y * 100 + height * 0.52f;

        batch.setColor(Color.BLACK);
        batch.draw(TextureManager.getPixel(), barX, manaBarY, barWidth, barHeight);

        batch.setColor(Color.BLUE);
        batch.draw(TextureManager.getPixel(), barX, manaBarY, barWidth * manaPercentage, barHeight);

        batch.setColor(Color.WHITE);

        BitmapFont font = Storage.getInstance().getFont();
        font.getData().setScale(0.5f);

        String manaText = mana + " / " + maxMana;
        GlyphLayout manaLayout = new GlyphLayout(font, manaText);

        font.draw(batch, manaText, barX + (barWidth - manaLayout.width) / 2, manaBarY + barHeight - 2);
    }

    
    public static class TextureManager {
        private static Texture pixel;

        public static Texture getPixel() {
            if (pixel == null) {
                Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
                pixmap.setColor(Color.WHITE);
                pixmap.fill();
                pixel = new Texture(pixmap);
                pixmap.dispose();
            }
            return pixel;
        }
    }
	
	public abstract void update(float delta);
	
	public abstract void render(SpriteBatch batch);
	
	public Body getBody() {
		return body;
	}
}
