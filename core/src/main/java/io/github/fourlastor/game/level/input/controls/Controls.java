package io.github.fourlastor.game.level.input.controls;

import com.badlogic.gdx.Input;

public interface Controls {
    Control left();

    Control right();

    Control jump();
    Control up();

    enum Setup implements Controls {
        P1(Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.SPACE, Input.Keys.UP);

        private final KeysControl left;
        private final KeysControl right;
        private final KeysControl jump;
        private final KeysControl up;

        Setup(int leftKey, int rightKey, int attackKey, int upKey) {
            left = new KeysControl(leftKey);
            right = new KeysControl(rightKey);
            jump = new KeysControl(attackKey);
            up = new KeysControl(upKey);
        }

        @Override
        public Control left() {
            return left;
        }

        @Override
        public Control right() {
            return right;
        }

        @Override
        public Control jump() {
            return jump;
        }

        @Override
        public Control up() {
            return up;
        }
    }
}
