package fr.ela.aoc2024;

import fr.ela.aoc2024.utils.Grid;
import fr.ela.aoc2024.utils.Pair;
import fr.ela.aoc2024.utils.Path;
import fr.ela.aoc2024.utils.Position;
import fr.ela.aoc2024.utils.Walker;

import java.util.ArrayList;
import java.util.Collection;
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

        private Pair<Long, Long> trailHeadScoreAndRating(Position trailHead) {
            Collection<Path<Position, Integer>> paths = w.findAllPaths(trailHead, p -> grid.get(p) == 9);
            long score = paths.stream()
                    .map(Path::end)
                    .collect(Collectors.toSet())
                    .size();
            long rating = paths.size();
            return new Pair<>(score, rating);
        }

        public Pair<Long,Long> trailHeadScoresAndRatings() {
            return trailHeads.stream()
                    .map(this::trailHeadScoreAndRating)
                    .reduce((p1, p2) -> new Pair<>(p1.key()+p2.key(), p1.value()+p2.value()))
                    .orElse(new Pair(0L,0L));
        }
    }

    public void solve(java.nio.file.Path path, String s, long expected1, long expected2) {
        System.out.println("--- " + s + " ----");
        TopoMap map = new TopoMap(Grid.parseCharactersGrid(list(path), c -> c - '0'));
        long time = System.currentTimeMillis();
        Pair<Long,Long> res = map.trailHeadScoresAndRatings();
        time = System.currentTimeMillis() - time;
        System.out.println("Part 1 (" + expected1 + ") : " + res.key() + " - " + time);
        time = System.currentTimeMillis();
        time = System.currentTimeMillis() - time;
        System.out.println("Part 2 (" + expected2 + ") : " + res.value() + " - " + time);
    }


    @Override
    public void run() {
        solve(getTestInputPath(), "Test", 36, 81);
        solve(getInputPath(), "Real", 794, 1706);
    }
}
