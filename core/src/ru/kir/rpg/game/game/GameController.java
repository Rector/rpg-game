package ru.kir.rpg.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import lombok.Data;
import ru.kir.rpg.game.helpers.Assets;
import ru.kir.rpg.game.screens.ScreenManager;

@Data
public class GameController {
    public static final int INITIAL_MONSTERS_COUNT = 1;
    public static final int TURNS_COUNT = 5;

    private SpriteBatch batch;
    private Stage stage;
    private ArmourController armourController;
    private WeaponController weaponController;
    private InfoController infoController;
    private UnitController unitController;
    private EffectController effectController;
    private GameMap gameMap;

    private Vector2 mouse;
    private Vector2 pressedMouse;

    private int cursorX, cursorY;
    private int round;
    private float worldTimer;

    public GameController(SpriteBatch batch) {
        this.batch = batch;
        this.stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        this.mouse = new Vector2(0, 0);
        this.pressedMouse = new Vector2(0, 0);
        this.gameMap = new GameMap();
        this.armourController = new ArmourController(this);
        this.weaponController = new WeaponController(this);
        this.effectController = new EffectController();
        this.unitController = new UnitController(this);
        this.infoController = new InfoController();
        this.unitController.init(INITIAL_MONSTERS_COUNT);
        this.round = 1;
        this.createGui();
        this.stage.addActor(unitController.getHero().getGuiGroup());
        this.stage.addActor(unitController.getHero().getActionGroup());
    }

    public void roundUp() {
        round++;
        unitController.startRound();
        if (round % 3 == 0) {
            unitController.createMonsterInRandomCell();
        }

        if (round > 1) {
            gameMap.generateBerries();
        }
    }

    public boolean isCellEmpty(int cx, int cy) {
        return gameMap.isCellPassable(cx, cy) && unitController.isCellFree(cx, cy);
    }

    public void update(float dt) {
        worldTimer += dt;
        checkMouse();
        unitController.update(dt);
        infoController.update(dt);
        effectController.update(dt);

        stage.act(dt);
    }

    public void checkMouse() {
        mouse.set(Gdx.input.getX(), Gdx.input.getY());
        ScreenManager.getInstance().getViewport().unproject(mouse);

        if (Gdx.input.isTouched() && Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            float camX = ScreenManager.getInstance().getCamera().position.x;
            float camY = ScreenManager.getInstance().getCamera().position.y;

            camX += pressedMouse.x - mouse.x;
            camY += pressedMouse.y - mouse.y;

            if (camX < ScreenManager.HALF_WORLD_WIDTH) {
                camX = ScreenManager.HALF_WORLD_WIDTH;
            }

            if (camX > GameMap.WORLD_WIDTH - ScreenManager.HALF_WORLD_WIDTH) {
                camX = GameMap.WORLD_WIDTH - ScreenManager.HALF_WORLD_WIDTH;
            }

            if (camY < ScreenManager.HALF_WORLD_HEIGHT) {
                camY = ScreenManager.HALF_WORLD_HEIGHT;
            }

            if (camY > GameMap.WORLD_HEIGHT - ScreenManager.HALF_WORLD_HEIGHT) {
                camY = GameMap.WORLD_HEIGHT - ScreenManager.HALF_WORLD_HEIGHT;
            }

            mouse.x += pressedMouse.x - mouse.x;
            mouse.y += pressedMouse.y - mouse.y;

            ScreenManager.getInstance().pointCameraTo(camX, camY);
        }

        cursorX = (int) (mouse.x / GameMap.CELL_WIDTH);
        cursorY = (int) (mouse.y / GameMap.CELL_HEIGHT);

        pressedMouse.set(mouse);
    }

    public void createGui() {
        stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());

        BitmapFont font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf");

        TextButton.TextButtonStyle menuBtnStyle = new TextButton.TextButtonStyle(
                skin.getDrawable("smButton"), null, null, font24);

        final TextButton btnEndTurn = new TextButton("End turn", menuBtnStyle);
        btnEndTurn.setPosition(0, 0);

        final TextButton btnGoToMenu = new TextButton("Menu", menuBtnStyle);
        btnGoToMenu.setPosition(140, 0);

        btnEndTurn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                unitController.getHero().tryToEndTurn();
            }
        });

        btnGoToMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
            }
        });

        Group menuGroup = new Group();
        menuGroup.addActor(btnEndTurn);
        menuGroup.addActor(btnGoToMenu);
        menuGroup.setPosition(20, ScreenManager.WORLD_HEIGHT - 60);

        stage.addActor(menuGroup);
        skin.dispose();
    }

}
