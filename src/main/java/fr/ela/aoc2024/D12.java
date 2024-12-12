package fr.ela.aoc2024;

import fr.ela.aoc2024.utils.Grid;
import fr.ela.aoc2024.utils.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class D12 extends AoC {

    private final List<String> smallTest = Arrays.asList("""
            AAAA
            BBCD
            BBCC
            EEEC""".split("\\n"));
    private final List<String> smallTest2 = Arrays.asList("""
            OOOOO
            OXOXO
            OOOOO
            OXOXO
            OOOOO
            """.split("\\n"));

    class Garden {
        Map<Character, Set<Position>> plantsPositions;
        Grid<Character> grid;

        public Garden(List<String> input) {
            this.plantsPositions = new HashMap<>();
            grid = Grid.parseCharactersGrid(input, Function.identity(), (p, c) -> plantsPositions.computeIfAbsent(c, HashSet::new).add(p));
        }

        public Map<Character, List<Set<Position>>> getAllPlots() {
            return plantsPositions.keySet().stream()
                    .collect(Collectors.toMap(Function.identity(), this::getPlots));
        }

        private List<Set<Position>> getPlots(char value) {
            Set<Position> positions = new HashSet<>(plantsPositions.get(value));
            List<Set<Position>> plots = new ArrayList<>();
            while (!positions.isEmpty()) {
                Position p = positions.iterator().next();
                plots.add(getPlot(p, positions));
            }
            return plots;
        }

        private Set<Position> getPlot(Position p, Set<Position> positions) {
            Set<Position> plot = new HashSet<>();
            plot.add(p);
            positions.remove(p);
            List<Position> neighbors = p.cardinals().stream().filter(n -> positions.contains(n) && !plot.contains(n)).toList();
            if (neighbors.isEmpty()) {
                return plot;
            } else {
                neighbors.stream().map(n -> getPlot(n, positions)).forEach(pl -> {
                    plot.addAll(pl);
                    positions.removeAll(pl);
                });
                return plot;
            }
        }

        private Long perimeter(Set<Position> plot) {
            long total = 4 * plot.size();
            long common = plot.stream().mapToLong(p -> p.cardinals().stream().filter(plot::contains).count()).sum();
            return total - common;
        }

        private Long fencingCost(Set<Position> plot) {
            long value = plot.size() * perimeter(plot);
            //System.out.println(grid.get(plot.iterator().next())+" : "+value);
            return value;
        }

        private long fencingCost() {
            return getAllPlots().values().stream().flatMap(List::stream).mapToLong(plot -> fencingCost(plot)).sum();
        }
    }

    public void solve(List<String> input, String step, long expected1, long expected2) {
        System.out.println("--- " + step + " ----");
        long time = System.currentTimeMillis();
        Garden garden = new Garden(input);

        long res = garden.fencingCost();

        time = System.currentTimeMillis() - time;
        System.out.println("Part 1 (" + expected1 + ") : " + res + " - " + time);
        time = System.currentTimeMillis();

        time = System.currentTimeMillis() - time;
        System.out.println("Part 2 (" + expected2 + ") : " + res + " - " + time);
    }

    @Override
    public void run() {
        solve(smallTest, "Small Test", 140, -1);
        //solve(smallTest2, "Small Test", 140, -1);
        solve(list(getTestInputPath()), "Test", 1930, -1);
        solve(list(getInputPath()), "Real", -1, -1);
    }
}

