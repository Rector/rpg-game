package ru.kir.rpg.game.game;

import com.badlogic.gdx.math.MathUtils;
import ru.kir.rpg.game.game.units.Unit;

public class BattleCalc {
    public static int attack(Unit attacker, Unit target) {
        int out = attacker.getCurrentWeapon().getDamage();
        out -= target.getArmour().getGenericDefence();
        out -= target.getArmour().getResistance().get(attacker.getCurrentWeapon().getType());
        if (out < 0) {
            out = 0;
        }
        return out;
    }

    public static boolean rollCounterAttack(Unit attacker, Unit target) {
        return MathUtils.random() < 0.25f;
    }

    public static int checkCounterAttack(Unit attacker, Unit target) {
        return attack(target, attacker);
    }

}

