package ru.kir.rpg.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArmourController {
    private GameController gc;
    private Map<Integer, List<Armour>> armours;

    public Armour getRandomArmourByLevel(int level) {
        List<Armour> vars = armours.get(level);
        return vars.get(MathUtils.random(vars.size() - 1));
    }

    public ArmourController(GameController gc) {
        this.gc = gc;
        this.armours = new HashMap<>();
        try (BufferedReader reader = Gdx.files.internal("data/armours.csv").reader(8192)) {
            reader.readLine();
            String line = null;
            while ((line = reader.readLine()) != null) {
                Armour armour = new Armour(line);
                if (!this.armours.containsKey(armour.getLevel())) {
                    this.armours.put(armour.getLevel(), new ArrayList<Armour>());
                }
                this.armours.get(armour.getLevel()).add(armour);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to read file 'data/armours.csv'");
        }
    }

}
