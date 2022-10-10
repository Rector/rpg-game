package ru.kir.rpg.game.game;

import lombok.Data;

@Data
public class Weapon {

    public enum Type {
        SPEAR, SWORD, MACE, AXE, BOW
    }

    private Type type;
    private String title;
    private int level;
    private int damage;
    private int radius;
    private int fxIndex;

    public Weapon(String line) {
        String[] tokens = line.split(",");
        this.title = tokens[0].trim();
        this.level = Integer.parseInt(tokens[1].trim());
        this.type = Type.valueOf(tokens[2].trim());
        this.damage = Integer.parseInt(tokens[3].trim());
        this.radius = Integer.parseInt(tokens[4].trim());
    }

}

