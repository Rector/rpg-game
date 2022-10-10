package ru.kir.rpg.game.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import ru.kir.rpg.game.game.units.Unit;
import ru.kir.rpg.game.helpers.Assets;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    public enum CellType {
        GRASS(1), WATER(2), TREE(99);

        private int complexity;

        CellType(int complexity) {
            this.complexity = complexity;
        }

        public int getComplexity() {
            return complexity;
        }
    }

    public enum DropType {
        NONE, GOLD
    }

    private class Cell {
        CellType type;
        DropType dropType;
        int dropPower;

        int index;

        public Cell() {
            type = CellType.GRASS;
            dropType = DropType.NONE;
            index = 0;
        }

        public void changeType(CellType to) {
            if (type == CellType.GRASS) {
                type = to;
                if (type == CellType.TREE) {
                    index = MathUtils.random(4);
                }
            }
        }

    }

    public static final int CELLS_X = 32;
    public static final int CELLS_Y = 32;

    public static final int CELL_WIDTH = 60;
    public static final int CELL_HEIGHT = 60;
    public static final int WORLD_WIDTH = CELL_WIDTH * CELLS_X;
    public static final int WORLD_HEIGHT = CELL_HEIGHT * CELLS_Y;
    public static final int FOREST_PERCENTAGE = 5;
    public static final int WATER_PERCENTAGE = 10;

    public int getCellsX() {
        return CELLS_X;
    }

    public int getCellsY() {
        return CELLS_Y;
    }

    private Cell[][] data;

    private List<Berry> listBerry;

    private TextureRegion grassTexture;
    private TextureRegion goldTexture;
    private TextureRegion[] treesTextures;

    private TextureRegion waterTexture;
    private TextureRegion berryTexture;

    public GameMap() {
        this.listBerry = new ArrayList<>();

        this.data = new Cell[CELLS_X][CELLS_Y];
        for (int i = 0; i < CELLS_X; i++) {
            for (int j = 0; j < CELLS_Y; j++) {
                this.data[i][j] = new Cell();
            }
        }
        int treesCount = (int) ((CELLS_X * CELLS_Y * FOREST_PERCENTAGE) / 100.0f);
        for (int i = 0; i < treesCount; i++) {
            this.data[MathUtils.random(0, CELLS_X - 1)][MathUtils.random(0, CELLS_Y - 1)].changeType(CellType.TREE);
        }

        int waterCount = (int) ((CELLS_X * CELLS_Y * WATER_PERCENTAGE) / 100.0f);
        for (int i = 0; i < waterCount; i++) {
            this.data[MathUtils.random(0, CELLS_X - 1)][MathUtils.random(0, CELLS_Y - 1)].changeType(CellType.WATER);
        }

        for (int i = 0; i < CELLS_X; i++) {
            for (int j = 0; j < CELLS_Y; j++) {
                if (data[i][j].type == CellType.TREE) {
                    listBerry.add(new Berry(i, j));
                }
            }
        }

        this.grassTexture = Assets.getInstance().getAtlas().findRegion("grass");
        this.goldTexture = Assets.getInstance().getAtlas().findRegion("chest").split(60, 60)[0][0];
        this.treesTextures = Assets.getInstance().getAtlas().findRegion("trees").split(60, 90)[0];
        this.waterTexture = Assets.getInstance().getAtlas().findRegion("water");
        this.berryTexture = Assets.getInstance().getAtlas().findRegion("berry");

    }

    public List<Berry> getListBerry() {
        return listBerry;
    }

    public CellType getCellType(int cx, int cy) {
        return data[cx][cy].type;
    }

    public boolean isCellPassable(int cx, int cy) {
        if (cx < 0 || cx > getCellsX() - 1 || cy < 0 || cy > getCellsY() - 1) {
            return false;
        }
        if (data[cx][cy].type == CellType.TREE) {
            return false;
        }

        return true;
    }

    public void renderGround(SpriteBatch batch, int cx, int cy) {
        batch.draw(grassTexture, cx * CELL_WIDTH, cy * CELL_HEIGHT);
        if (data[cx][cy].type == CellType.WATER) {
            batch.draw(waterTexture, cx * CELL_WIDTH, cy * CELL_HEIGHT);
        }
    }

    public void renderObjects(SpriteBatch batch, int cx, int cy) {
        if (data[cx][cy].type == CellType.TREE) {
            batch.draw(treesTextures[data[cx][cy].index], cx * CELL_WIDTH, cy * CELL_HEIGHT);
        }

        for (int k = 0; k < listBerry.size(); k++) {
            if (cx == listBerry.get(k).getBerryX() && cy == listBerry.get(k).getBerryY() && listBerry.get(k).isVisible()) {
                batch.draw(berryTexture, cx * CELL_WIDTH + 15, cy * CELL_HEIGHT + 25);
            }
        }

        if (data[cx][cy].dropType == DropType.GOLD) {
            batch.draw(goldTexture, cx * CELL_WIDTH, cy * CELL_HEIGHT);
        }
    }

    public void generateBerries() {
        for (int i = 0; i < listBerry.size(); i++) {
            boolean chanceGenerate = MathUtils.random() < 0.5F;
            if (chanceGenerate && !listBerry.get(i).isVisible()) {
                listBerry.get(i).activeVisible();
            }

        }
    }

    public void generateDrop(int cellX, int cellY, int power) {
        if (MathUtils.random() < 0.5f) {
            DropType randomDropType = DropType.GOLD;

            if (randomDropType == DropType.GOLD) {
                int goldAmount = power + MathUtils.random(power, power * 3);
                data[cellX][cellY].dropType = randomDropType;
                data[cellX][cellY].dropPower = goldAmount;
            }
        }
    }

    public boolean hasDropInCell(int cellX, int cellY) {
        return data[cellX][cellY].dropType != DropType.NONE;
    }

    public void checkAndTakeDrop(Unit unit) {
        Cell currentCell = data[unit.getCellX()][unit.getCellY()];
        if (currentCell.dropType == DropType.NONE) {
            return;
        }
        if (currentCell.dropType == DropType.GOLD) {
            unit.addGold(currentCell.dropPower);
        }
        currentCell.dropType = DropType.NONE;
        currentCell.dropPower = 0;
    }

}
