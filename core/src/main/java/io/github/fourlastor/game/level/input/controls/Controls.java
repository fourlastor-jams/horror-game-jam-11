package io.github.fourlastor.game.level.input.controls;

import com.badlogic.gdx.Input;

public interface Controls {
    Control left();

    Control right();

    Control jump();

    Control up();

    Control down();

    enum Setup implements Controls {
        P1(Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.SPACE, Input.Keys.UP, Input.Keys.DOWN);

        private final KeysControl left;
        private final KeysControl right;
        private final KeysControl jump;
        private final KeysControl up;
        private final KeysControl down;

        Setup(int leftKey, int rightKey, int attackKey, int upKey, int downKey) {
            left = new KeysControl(leftKey);
            right = new KeysControl(rightKey);
            jump = new KeysControl(attackKey);
            up = new KeysControl(upKey);
            this.down = new KeysControl(downKey);
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

        @Override
        public Control down() {
            return down;
        }
    }
}
