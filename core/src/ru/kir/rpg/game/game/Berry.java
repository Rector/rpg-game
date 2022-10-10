package ru.kir.rpg.game.game;

import lombok.Getter;

@Getter
public class Berry {
    private int satisfyingHunger;
    private boolean visible;
    private int berryX;
    private int berryY;

    public Berry(int berryX, int berryY) {
        this.berryX = berryX;
        this.berryY = berryY;
        this.satisfyingHunger = 5;
        this.visible = false;
    }

    public void deactivateVisible() {
        visible = false;
    }

    public void activeVisible() {
        visible = true;
    }

}
