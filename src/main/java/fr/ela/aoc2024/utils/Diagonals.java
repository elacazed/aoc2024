package fr.ela.aoc2024.utils;

public enum Diagonals {

    NO(-1, -1),
    NE(1, -1),
    SO(-1, 1),
    SE(1, 1);

    final int xOffset;
    final int yOffset;

    Diagonals(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
    public Position move(Position from) {
        return move(from, 1);
    }

    public Position move(Position from, int amount) {
        return new Position(from.x() + amount * xOffset, from.y() + amount * yOffset);
    }
}
