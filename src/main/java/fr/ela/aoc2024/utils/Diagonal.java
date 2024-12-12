package fr.ela.aoc2024.utils;

import java.util.stream.Stream;

public enum Diagonal {

    NW(-1, -1, Direction.NORTH, Direction.WEST),
    NE(1, -1, Direction.NORTH, Direction.EAST),
    SW(-1, 1, Direction.SOUTH, Direction.WEST),
    SE(1, 1, Direction.SOUTH, Direction.EAST);

    final int xOffset;
    final int yOffset;
    final Direction vertical;
    final Direction horizontal;

    Diagonal(int xOffset, int yOffset, Direction vertical, Direction horizontal) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.vertical = vertical;
        this.horizontal = horizontal;
    }

    public Position move(Position from) {
        return move(from, 1);
    }

    public Position move(Position from, int amount) {
        return new Position(from.x() + amount * xOffset, from.y() + amount * yOffset);
    }

    public Stream<Direction> directions() {
        return Stream.of(vertical, horizontal);
    }
}
