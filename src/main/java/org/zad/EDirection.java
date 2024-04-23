package org.zad;

public enum EDirection {
    IDLE,
    DOWN,
    UP;

    public static EDirection getDirection(int number) {
        if (number > 0) {
            return UP;
        } else if (number < 0) {
            return DOWN;
        } else {
            return IDLE;
        }
    }

    @Override
    public String toString() {
        return switch (this) {
            case UP -> "UP";
            case DOWN -> "DOWN";
            case IDLE -> "IDLE";
        };
    }

    public String toChar() {
        return switch (this) {
            case UP -> "^";
            case DOWN -> "v";
            case IDLE -> "-";
        };
    }
}
