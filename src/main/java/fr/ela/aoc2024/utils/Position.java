package fr.ela.aoc2024.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public record Position(long x, long y) {

    public long distance(Position other) {
        return Math.abs(other.y - y) + Math.abs(other.x - x);
    }

    public long distance() {
        return x + y;
    }

    public List<Position> cardinalsList() {
        return cardinals().toList();
    }

    public Stream<Position> cardinals() {
        return Arrays.stream(Direction.values()).map(d -> d.move(this));
    }

    public Stream<Position> neighbours() {
        return Stream.concat(Arrays.stream(Direction.values()).map(d -> d.move(this)),
                Arrays.stream(Diagonal.values()).map(d -> d.move(this)));
    }

    public List<Position> neighboursList() {
        return neighbours().toList();
    }

    public Position modulo(long xmax, long ymax) {
        return new Position(x % xmax, y % ymax);
    }

    public String toString() {
        return "[" + x + "," + y + "]";
    }
}
