package fr.ela.aoc2024.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Grid<N> {

    Map<Position, N> map;
    final int width;
    final int height;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        map = new HashMap<>();
    }

    public static <K> Grid<K> parseCharactersGrid(List<String> lines, Function<Character, K> mapper) {
        return parseCharactersGrid(lines, mapper, (p,k) -> {});
    }

    public static <K> Grid<K> parseCharactersGrid(List<String> lines, Function<Character, K> mapper, BiConsumer<Position, K> consumer) {
        int height = lines.size();
        int width = lines.get(0).length();
        Grid<K> grid = new Grid<>(width, height);
        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < width; x++) {
                K k = mapper.apply(line.charAt(x));
                if (k != null) {
                    Position p = new Position(x,y);
                    grid.map.put(p, k);
                    consumer.accept(p, k);
                }
            }
        }
        return grid;
    }

    public List<Position> getPositionsOf(N value) {
        return streamPositionsOf(value).toList();
    }

    public Stream<Position> streamPositionsOf(N value) {
        return map.entrySet().stream().filter(e -> e.getValue().equals(value)).map(Map.Entry::getKey);
    }


    public Stream<Position> stream() {
        return map.keySet().stream();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int size() {
        return map.size();
    }

    public N get(int x, int y) {
        return get(new Position(x, y));
    }

    public N get(Position pos) {
        return map.get(pos);
    }

    public void put(Position pos, N value) {
        map.put(pos, value);
    }

    public void put(Position pos, N value, BiFunction<N,N,N> aggregator) {
        N n = get(pos);
        if (n == null) {
            put(pos, value);
        } else {
            put(pos, aggregator.apply(n, value));
        }
    }

    public boolean inBounds(Position pos) {
        return pos.x() >= 0 && pos.y() >= 0 && pos.x() < width && pos.y() < height;
    }

    public boolean isBottomRightCorner(Position position) {
        return position.y() == height - 1 && position.x() == width - 1;
    }

    public List<Position> cardinals(Position position) {
        return position.cardinalsList().stream().filter(this::inBounds).toList();
    }

    public List<Position> cardinalsIf(Position position, Predicate<Position> pred) {
        return position.cardinals().filter(this::inBounds).filter(pred).toList();
    }

    public List<Position> to(Position from, Set<Position> path) {
        return from.cardinals().filter(this::contains).filter(p -> !path.contains(p)).toList();
    }

    public String toString(Function<N, Character> mapper) {
        List<char[]> lines = draw(mapper);
        return lines.stream().map(String::new).collect(Collectors.joining("\n"));
    }

    public List<char[]> draw(Function<N, Character> mapper) {
        List<char[]> sb = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            char[] line = new char[width];
            for (int x = 0; x < width; x++) {
                line[x] = mapper.apply(get(x, y));
            }
            sb.add(line);
        }
        return sb;
    }

    public void remove(Position start) {
        map.remove(start);
    }

    public boolean contains(Position pos) {
        return map.containsKey(pos);
    }

    public boolean isOnTheEdge(Position p) {
        return p.x() == 0 || p.y() == 0 || p.x() == width - 1 || p.y() == height - 1;
    }

    public void clear() {
        map.clear();
    }
}
