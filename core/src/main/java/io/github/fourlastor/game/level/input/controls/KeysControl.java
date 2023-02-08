package io.github.fourlastor.game.level.input.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntArray;

class KeysControl implements Control {

    private final IntArray keys;

    public KeysControl(int... keys) {
        this.keys = new IntArray(keys);
    }

    public boolean pressed() {
        for (int i = 0; i < keys.size; i++) {
            if (Gdx.input.isKeyPressed(keys.get(i))) {
                return true;
            }
        }
        return false;
    }
}
