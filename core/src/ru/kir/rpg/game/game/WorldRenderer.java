package ru.kir.rpg.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ru.kir.rpg.game.game.units.Unit;
import ru.kir.rpg.game.helpers.Assets;
import ru.kir.rpg.game.screens.ScreenManager;

import java.util.ArrayList;
import java.util.List;

public class WorldRenderer {
    private GameController gc;
    private SpriteBatch batch;
    private TextureRegion cursorTexture;
    private BitmapFont font18;
    private BitmapFont font24;
    private StringBuilder stringHelper;
    private List<MapElement>[] drawables;
    private float worldTimer;

    private FrameBuffer frameBuffer;
    private TextureRegion frameBufferRegion;
    private ShaderProgram shaderProgram;

    public WorldRenderer(GameController gc, SpriteBatch batch) {
        this.gc = gc;
        this.batch = batch;
        this.cursorTexture = Assets.getInstance().getAtlas().findRegion("cursor");
        this.font18 = Assets.getInstance().getAssetManager().get("fonts/font18.ttf");
        this.font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf");
        this.stringHelper = new StringBuilder();

        this.drawables = new ArrayList[20];
        for (int i = 0; i < drawables.length; i++) {
            drawables[i] = new ArrayList<>();
        }

        this.frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, ScreenManager.WORLD_WIDTH, ScreenManager.WORLD_HEIGHT, false);
        this.frameBufferRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
        this.frameBufferRegion.flip(false, true);
        this.shaderProgram = new ShaderProgram(Gdx.files.internal("shaders/vertex.glsl").readString(), Gdx.files.internal("shaders/fragment.glsl").readString());
        if (!shaderProgram.isCompiled()) {
            throw new IllegalArgumentException("Error compiling shader: " + shaderProgram.getLog());
        }
    }

    public void update(float dt) {
        worldTimer += dt;
    }

    public void render() {
        float camX = ScreenManager.getInstance().getCamera().position.x;
        float camY = ScreenManager.getInstance().getCamera().position.y;

        int left = Math.max(0, (int) (camX - ScreenManager.HALF_WORLD_WIDTH - GameMap.CELL_WIDTH) / GameMap.CELL_WIDTH);
        int right = Math.min(GameMap.CELLS_X, (int) (camX + ScreenManager.HALF_WORLD_WIDTH + GameMap.CELL_WIDTH) / GameMap.CELL_WIDTH);
        int bottom = Math.max(0, (int) (camY - ScreenManager.HALF_WORLD_HEIGHT - GameMap.CELL_HEIGHT) / GameMap.CELL_HEIGHT);
        int top = Math.min(GameMap.CELLS_Y, (int) (camY + ScreenManager.HALF_WORLD_HEIGHT + GameMap.CELL_HEIGHT) / GameMap.CELL_HEIGHT);
        for (int i = 0; i < drawables.length; i++) {
            drawables[i].clear();
        }
        for (int i = 0; i < gc.getUnitController().getAllUnits().size(); i++) {
            Unit unit = gc.getUnitController().getAllUnits().get(i);
            int cx = unit.getCellX();
            int cy = unit.getCellY();
            if (cx >= left && cx < right && cy >= bottom && cy < top) {
                drawables[cy - bottom].add(unit);
            }
        }

        frameBuffer.begin();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        for (int y = top - 1; y >= bottom; y--) {
            for (int x = left; x < right; x++) {
                if ((x + y) % 2 == 1) {
                    batch.setColor(0.98f, 0.98f, 0.98f, 1.0f);
                } else {
                    batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                }
                gc.getGameMap().renderGround(batch, x, y);
            }
        }

        Color cursorColor = Color.WHITE;
        if (gc.getUnitController().getMonsterController().getMonsterInCell(gc.getCursorX(), gc.getCursorY()) != null) {
            cursorColor = Color.RED;
        }
        batch.setColor(cursorColor.r, cursorColor.g, cursorColor.b, 0.5f + 0.1f * (float) Math.sin(gc.getWorldTimer() * 8.0f));
        batch.draw(cursorTexture, gc.getCursorX() * GameMap.CELL_WIDTH, gc.getCursorY() * GameMap.CELL_HEIGHT, GameMap.CELL_WIDTH, GameMap.CELL_HEIGHT);


        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        for (int y = top - 1; y >= bottom; y--) {
            for (int i = 0; i < drawables[y - bottom].size(); i++) {
                drawables[y - bottom].get(i).render(batch, font18);
            }
            for (int x = left; x < right; x++) {
                gc.getGameMap().renderObjects(batch, x, y);
            }
        }
        gc.getEffectController().render(batch);
        gc.getInfoController().render(batch, font18);
        batch.end();
        frameBuffer.end();

        ScreenManager.getInstance().resetCamera();

        batch.begin();
        batch.setShader(shaderProgram);
        shaderProgram.setUniformf(shaderProgram.getUniformLocation("time"), worldTimer);
        shaderProgram.setUniformf(shaderProgram.getUniformLocation("px"), camX / 1280.0f);
        shaderProgram.setUniformf(shaderProgram.getUniformLocation("py"), camY / 720.0f);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.draw(frameBufferRegion, 0, 0);
        batch.end();
        batch.setShader(null);

        gc.getStage().draw();
        ScreenManager.getInstance().pointCameraTo(camX, camY);
    }

}
