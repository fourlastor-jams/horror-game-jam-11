package io.github.fourlastor.game.level.physics;

public class Bits {

    public enum Category {
        PLAYER,
        PLAYER_FOOT,
        SPIKE,
        GROUND;
        public final short bits;

        Category() {
            this.bits = (short) (1 << ordinal());
        }
    }

    public enum Mask {
        PLAYER(Category.GROUND, Category.SPIKE),
        PLAYER_FOOT(Category.GROUND),
        SPIKE(Category.PLAYER),
        GROUND(Category.PLAYER, Category.PLAYER_FOOT);

        public final short bits;

        Mask(Category... categories) {
            short bits = 0;
            for (Category category : categories) {
                bits |= category.bits;
            }
            this.bits = bits;
        }
    }
}
