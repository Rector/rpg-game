package ru.kir.rpg.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ru.kir.rpg.game.RPGGame;
import ru.kir.rpg.game.helpers.Assets;

public class ScreenManager {

    public enum ScreenType {
        MENU, GAME, GAME_OVER
    }

    public static final int WORLD_WIDTH = 1280;
    public static final int HALF_WORLD_WIDTH = WORLD_WIDTH / 2;
    public static final int WORLD_HEIGHT = 720;
    public static final int HALF_WORLD_HEIGHT = WORLD_HEIGHT / 2;

    private RPGGame game;
    private SpriteBatch batch;

    private LoadingScreen loadingScreen;
    private GameScreen gameScreen;
    private MenuScreen menuScreen;

    private GameOverScreen gameOverScreen;

    private Screen targetScreen;
    private Viewport viewport;
    private Camera camera;

    private static ScreenManager ourInstance = new ScreenManager();

    public static ScreenManager getInstance() {
        return ourInstance;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public Camera getCamera() {
        return camera;
    }

    private ScreenManager() {
    }

    public void init(RPGGame game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
        this.camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.gameScreen = new GameScreen(batch);
        this.menuScreen = new MenuScreen(batch);


        this.gameOverScreen = new GameOverScreen(batch);

        this.loadingScreen = new LoadingScreen(batch);
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.apply();
    }

    public void resetCamera() {
        camera.position.set(HALF_WORLD_WIDTH, HALF_WORLD_HEIGHT, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    public void pointCameraTo(float x, float y) {
        camera.position.set(x, y, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    public void changeScreen(ScreenType type) {
        Screen screen = game.getScreen();
        Assets.getInstance().clear();
        if (screen != null) {
            screen.dispose();
        }
        resetCamera();
        Gdx.input.setInputProcessor(null);
        game.setScreen(loadingScreen);
        switch (type) {
            case MENU:
                targetScreen = menuScreen;
                Assets.getInstance().loadAssets(ScreenType.MENU);
                break;
            case GAME:
                targetScreen = gameScreen;
                Assets.getInstance().loadAssets(ScreenType.GAME);
                break;
            case GAME_OVER:
                targetScreen = gameOverScreen;
                Assets.getInstance().loadAssets(ScreenType.GAME_OVER);
        }
    }

    public void goToTarget() {
        game.setScreen(targetScreen);
    }

}
