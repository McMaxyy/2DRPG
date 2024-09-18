package config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class Storage {
    private static Storage instance = null;
    public Skin skin;
    public TextButton.TextButtonStyle buttonStyle;
    public LabelStyle labelStyle;
    public TextFieldStyle textStyle;
    public BitmapFont font;
    public static AssetManager assetManager = new AssetManager();
    private static boolean newLoad = true;    
    
    public static synchronized Storage getInstance()  {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }
    
    public Storage() {
        skin = new Skin(Gdx.files.internal("buttons/newskin/newskin.json"));    
        if(newLoad) {
            newLoad = false;
            loadAssets();
        }        
    }    
    
    public static void loadAssets() {
        // Walking animations
        assetManager.load("character/Walking/Minotaur_01_Walking_000.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_001.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_002.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_003.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_004.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_005.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_006.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_007.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_008.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_009.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_010.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_011.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_012.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_013.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_014.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_015.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_016.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_017.png", Texture.class);
       
        // Idle animations
        assetManager.load("character/Idle/Minotaur_01_Idle_000.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_001.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_002.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_003.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_004.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_005.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_006.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_007.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_008.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_009.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_010.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_011.png", Texture.class);

        // Jumping animations
        assetManager.load("character/Jump Loop/Minotaur_01_Jump Loop_000.png", Texture.class);
        assetManager.load("character/Jump Loop/Minotaur_01_Jump Loop_001.png", Texture.class);
        assetManager.load("character/Jump Loop/Minotaur_01_Jump Loop_002.png", Texture.class);
        assetManager.load("character/Jump Loop/Minotaur_01_Jump Loop_003.png", Texture.class);
        assetManager.load("character/Jump Loop/Minotaur_01_Jump Loop_004.png", Texture.class);
        assetManager.load("character/Jump Loop/Minotaur_01_Jump Loop_005.png", Texture.class);

        // Load pre-generated bitmap font files (.fnt and .png)
        assetManager.load("fonts/Cascadia.fnt", BitmapFont.class);    
        
        assetManager.finishLoading();
    }
    
    public void createFont() {
        // Load the BitmapFont from the asset manager instead of generating it
        font = assetManager.get("fonts/Cascadia.fnt", BitmapFont.class);
        
        // Load textures for button styles
        Texture borderTextureUp = new Texture(Gdx.files.internal("buttons/newskin/newskin_data/textbutton.9.png"));
        Texture borderTextureDown = new Texture(Gdx.files.internal("buttons/newskin/newskin_data/textbutton-down.9.png"));
        
        NinePatch borderPatchUp = new NinePatch(borderTextureUp, 1, 1, 1, 1);
        NinePatch borderPatchDown = new NinePatch(borderTextureDown, 1, 1, 1, 1);
      
        // Set the styles for TextButton, Label, and TextField
        buttonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        buttonStyle.up = new NinePatchDrawable(borderPatchUp);
        buttonStyle.down = new NinePatchDrawable(borderPatchDown);
        buttonStyle.font = font;
        
        labelStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
        labelStyle.font = font;  
        
        textStyle = new TextField.TextFieldStyle(skin.get(TextField.TextFieldStyle.class));
        textStyle.font = font;  
    }
}
