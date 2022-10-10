package ru.kir.rpg.game.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface MapElement {
    void render(SpriteBatch batch, BitmapFont font);

    int getCellX();

    int getCellY();

}
