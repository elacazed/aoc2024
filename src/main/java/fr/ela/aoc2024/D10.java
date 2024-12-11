package fr.ela.aoc2024;

import fr.ela.aoc2024.utils.Grid;
import fr.ela.aoc2024.utils.Path;
import fr.ela.aoc2024.utils.Position;
import fr.ela.aoc2024.utils.Walker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class D10 extends AoC {

    public class TopoMap {
        final Grid<Integer> grid;
        final Walker<Position, Integer> w;
        final List<Position> trailHeads;

        public TopoMap(Grid<Integer> grid) {
            this.grid = grid;
            List<Position> heads = new ArrayList<>(grid.getPositionsOf(0));
            heads.sort(Comparator.comparing(Position::y).thenComparing(Position::x));
            this.trailHeads = heads;
            w = Walker.intWalker(this::nextPositions, grid::get);
        }

        List<Position> nextPositions(Position position) {
            int height = grid.get(position);
            return grid.cardinalsIf(position, p -> grid.get(p) == height + 1);
        }

        private long trailHeadScore(Position trailHead) {
            return w.findAllPaths(trailHead, p -> grid.get(p) == 9).stream()
                    .map(Path::end)
                    .collect(Collectors.toSet())
                    .size();
        }

        public long trailHeadScores() {
            return trailHeads.stream()
                    .mapToLong(this::trailHeadScore)
                    .sum();
        }
    }

    public void solve(java.nio.file.Path path, String s, long expected1, long expected2) {
        System.out.println("--- " + s + " ----");
        TopoMap map = new TopoMap(Grid.parseCharactersGrid(list(path), c -> c - '0'));
        long time = System.currentTimeMillis();
        long res = map.trailHeadScores();
        time = System.currentTimeMillis() - time;
        System.out.println("Part 1 (" + expected1 + ") : " + res + " - " + time);
        time = System.currentTimeMillis();
        time = System.currentTimeMillis() - time;
        System.out.println("Part 2 (" + expected2 + ") : " + res + " - " + time);
    }


    @Override
    public void run() {
        solve(getTestInputPath(), "Test", 36, 0);
        solve(getInputPath(), "Real", 0, 0);
    }
}
