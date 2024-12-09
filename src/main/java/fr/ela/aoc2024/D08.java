package fr.ela.aoc2024;

import fr.ela.aoc2024.utils.Grid;
import fr.ela.aoc2024.utils.Position;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class D08 extends AoC {

    public record Beacons(Grid<Character> grid, Set<Character> frequencies) {

        private Set<Position> antiNodes(boolean allPositions) {
            return frequencies.stream()
                    .map(c -> antiNodes(c, allPositions))
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
        }

        private Set<Position> antiNodes(Character frequency, boolean allPositions) {
            List<Position> antennas = grid.getPositionsOf(frequency);
            int size = antennas.size();
            Set<Position> antiNodes = new HashSet<>();
            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    antiNodes.addAll(antiNodes(antennas.get(i), antennas.get(j), allPositions));
                }
            }
            return antiNodes;
        }

        private List<Position> antiNodes(Position p1, Position p2, boolean allPositions) {
            int dx = p2.x() - p1.x();
            int dy = p2.y() - p1.y();
            List<Position> result = new ArrayList<>();
            int start = 1;
            int end = 2;
            if (allPositions) {
                start = Math.min(Math.abs(grid.getWidth() / dx), Math.abs(grid.getHeight() / dy));
                end = start;
            }

            for (int i = -1 * start; i <= end; i++) {
                Position next = new Position(p1.x() + i * dx, p1.y() + i * dy);
                if ((allPositions || i == -1 || i == 2) && grid.inBounds(next)) {
                    result.add(next);
                }
            }
            return result;
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
        int res = beacons.antiNodes(false).size();
        time = System.currentTimeMillis() - time;
        System.out.println("Part 1 (" + expected1 + ") : " + res + " - " + time);
        time = System.currentTimeMillis();
        res = beacons.antiNodes(true).size();
        time = System.currentTimeMillis() - time;
        System.out.println("Part 2 (" + expected2 + ") : " + res + " - " + time);
    }


    @Override
    public void run() {
        solve(getTestInputPath(), "Test", 14, 34);
        solve(getInputPath(), "Real", 357, 1266);
    }
}
