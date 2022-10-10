package ru.kir.rpg.game.game;


import ru.kir.rpg.game.game.units.Monster;
import ru.kir.rpg.game.helpers.ObjectPool;

public class MonsterController extends ObjectPool<Monster> {
    private GameController gc;

    public MonsterController(GameController gc) {
        this.gc = gc;
    }

    @Override
    protected Monster newObject() {
        return new Monster(gc);
    }

    public Monster activate(int cellX, int cellY) {
        return getActiveElement().activate(cellX, cellY);
    }

    public Monster getMonsterInCell(int cellX, int cellY) {
        for (int i = 0; i < getActiveList().size(); i++) {
            Monster m = getActiveList().get(i);
            if (m.getCellX() == cellX && m.getCellY() == cellY) {
                return m;
            }
        }
        return null;
    }

    public void update(float dt) {
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);
        }
        checkPool();
    }

}