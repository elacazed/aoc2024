package fr.ela.aoc2024;

import fr.ela.aoc2024.utils.Diagonal;
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

    // la diagonale d'une position est un coin si :
    // - il n'y a rien dans les cotés adjacents de la diagonale, qu'il y ait ou pas qqchoe dans la diagnale
    //      oo?  Xo?
    //      oX?  oX?
    //      ???  ???
    //   Sont des coins
    // - ou s'il n'y a rien dans la diagonale, ET qu'il y a des éléments dans les deux cotés adjacents.
    //      oX?
    //      XX?
    //      ???
    //   est un coin.
    private boolean isCorner(Position p, Diagonal d, Set<Position> positions) {
        if (d.directions().noneMatch(dir -> positions.contains(dir.move(p)))) {
            return true;
        }
        if (!positions.contains(d.move(p)) && d.directions().allMatch(dir -> positions.contains(dir.move(p)))) {
            return true;
        }
        return false;
    }

    private Long corners(Position position, Set<Position> plot) {
        return Arrays.stream(Diagonal.values()).filter(d -> isCorner(position, d, plot)).count();
    }

    // on a autant de cotés que de coins.
    private long getNumberOfSides(final Set<Position> plot) {
        return plot.stream().mapToLong(p -> corners(p, plot)).sum();
    }

    class Garden {
        final Map<Character, Set<Position>> plantsPositions;
        final Grid<Character> grid;
        final Map<Character, List<Set<Position>>> plots;

        public Garden(List<String> input) {
            this.plantsPositions = new HashMap<>();
            grid = Grid.parseCharactersGrid(input, Function.identity(), (p, c) -> plantsPositions.computeIfAbsent(c, HashSet::new).add(p));
            plots = computePlots();
        }

        public Map<Character, List<Set<Position>>> getAllPlots() {
            return plots;
        }

        private Map<Character, List<Set<Position>>> computePlots() {
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
            List<Position> neighbors = p.cardinalsList().stream().filter(n -> positions.contains(n) && !plot.contains(n)).toList();
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
            long common = plot.stream().mapToLong(p -> p.cardinalsList().stream().filter(plot::contains).count()).sum();
            return total - common;
        }

        public Long discountCost(Set<Position> plot) {
            long sides = getNumberOfSides(plot);
            return plot.size() * sides;
        }

        public Long discountCost() {
            return getAllPlots().values().stream().flatMap(List::stream).mapToLong(this::discountCost).sum();
        }

        private Long fencingCost(Set<Position> plot) {
            long value = plot.size() * perimeter(plot);
            return value;
        }

        private long fencingCost() {
            return getAllPlots().values().stream().flatMap(List::stream).mapToLong(this::fencingCost).sum();
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
        res = garden.discountCost();
        time = System.currentTimeMillis() - time;
        System.out.println("Part 2 (" + expected2 + ") : " + res + " - " + time);
    }

    @Override
    public void run() {
        solve(list(getTestInputPath()), "Test", 1930, 1206);
        solve(list(getInputPath()), "Real", 1446042, 902742);
    }
}

