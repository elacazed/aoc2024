package fr.ela.aoc2024;

import fr.ela.aoc2024.utils.Grid;
import fr.ela.aoc2024.utils.Pair;
import fr.ela.aoc2024.utils.Position;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class D08 extends AoC {

    public record Beacons(Grid<Character> grid, Set<Character> frequencies) {

        private Set<Position> antiNodes() {
            return frequencies.stream()
                    .map(c -> antiNodes(c))
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
        }

        private Set<Position> antiNodes(Character frequency) {
            List<Position> antennas = grid.getPositionsOf(frequency);
            int size= antennas.size();
            Set<Position> antiNodes = new HashSet<>();
            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    Pair<Position, Position> an = antiNodes(antennas.get(i), antennas.get(j));
                    if (grid.inBounds(an.key())) {
                        antiNodes.add(an.key());
                    }
                    if (grid.inBounds(an.value())) {
                        antiNodes.add(an.value());
                    }
                }
            }
            return antiNodes;
        }

        private Pair<Position, Position> antiNodes(Position p1, Position p2) {
            int dx = p2.x() - p1.x();
            int dy = p2.y() - p1.y();
            return new Pair<>(
                    new Position(p1.x() - dx, p1.y() - dy),
                    new Position(p1.x() + 2 * dx, p1.y() + 2 * dy));
        }
    }


    public Beacons loadGrid(Path inputPath) {
        final Set<Character> list = new HashSet<>();
        Grid<Character> grid = Grid.parseCharactersGrid(list(inputPath),
                c -> {
                    if (c == '.') {
                        return null;
                    } else {
                        list.add(c);
                        return c;
                    }
                });
        return new Beacons(grid, list);
    }


    public void solve(Path inputPath, String s, long expected1, long expected2) {
        System.out.println("--- " + s + " ----");
        Beacons beacons = loadGrid(inputPath);
        long time = System.currentTimeMillis();
        int res = beacons.antiNodes().size();
        time = System.currentTimeMillis() - time;
        System.out.println("Part 1 (" + expected1 + ") : " + res + " - " + time);
        time = System.currentTimeMillis();

        time = System.currentTimeMillis() - time;
        System.out.println("Part 2 (" + expected2 + ") : " + res + " - " + time);
    }


    @Override
    public void run() {
        solve(getTestInputPath(), "Test", 14, 0);
        solve(getInputPath(), "Real", 0, 0);
    }
}
