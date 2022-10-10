package ru.kir.rpg.game.game.units;

import com.badlogic.gdx.math.MathUtils;
import lombok.Getter;

@Getter
public class Stats {
    static int[] expTo = {20, 40, 100, 200, 300, 500, 800};

    int level;
    int exp;
    int hp, maxHp;
    int attackPoints, minAttackPoints, maxAttackPoints;
    int movePoints, minMovePoints, maxMovePoints;
    int visionRadius;

    public Stats(int level, int maxHp, int minAttackPoints, int maxAttackPoint, int minMovePoints, int maxMovePoint) {
        this.level = level;
        this.maxHp = maxHp;
        this.hp = this.maxHp;
        this.minAttackPoints = minAttackPoints;
        this.maxAttackPoints = maxAttackPoint;
        this.minMovePoints = minMovePoints;
        this.maxMovePoints = maxMovePoint;
        this.visionRadius = 5;
    }

    public void addExp(int amount) {
        exp += amount;
        if (exp >= expTo[level - 1]) {
            exp -= expTo[level - 1];
            level++;
            maxHp += 2;
            fullRestoreHp();
        }
    }

    public void restorePoints() {
        attackPoints = MathUtils.random(minAttackPoints, maxAttackPoints);
        movePoints = MathUtils.random(minMovePoints, maxMovePoints);
    }

    public void restoreHp(int amount) {
        hp += amount;
        if (hp > maxHp) {
            hp = maxHp;
        }
    }

    public void fullRestoreHp() {
        hp = maxHp;
    }

    public void resetPoints() {
        attackPoints = 0;
        movePoints = 0;
    }

    public boolean doIHaveAnyPoints() {
        return attackPoints > 0 || movePoints > 0;
    }

}
