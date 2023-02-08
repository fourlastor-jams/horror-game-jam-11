package io.github.fourlastor.game.level.input.controls;

import com.badlogic.gdx.Input;

public interface Controls {
    Control left();

    Control right();

    Control jump();

    enum Setup implements Controls {
        P1(Input.Keys.A, Input.Keys.D, Input.Keys.SPACE);

        private final KeysControl left;
        private final KeysControl right;
        private final KeysControl jump;

        Setup(int leftKey, int rightKey, int attackKey) {
            left = new KeysControl(leftKey);
            right = new KeysControl(rightKey);
            jump = new KeysControl(attackKey);
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
    }
}
