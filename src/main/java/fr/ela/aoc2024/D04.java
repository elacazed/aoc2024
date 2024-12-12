package fr.ela.aoc2024;


import fr.ela.aoc2024.utils.Diagonal;
import fr.ela.aoc2024.utils.Grid;
import fr.ela.aoc2024.utils.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public class D04 extends AoC {
    static Pattern XMAS = Pattern.compile("XMAS");
    static Pattern SAMX = Pattern.compile("SAMX");

    private long countXMas(String s) {
        return count(XMAS, s) + count(SAMX, s);
    }

    private long count(Pattern pattern, String s) {
        return pattern.matcher(s).results().count();
    }

    private List<String> allStrings(List<String> list) {
        int size = list.get(0).length();
        List<String> all = new ArrayList<>(list.size() * 3 + size * 3);
        all.addAll(list);
        for (int i = 0; i < size; i++) {
            int len = size - i;
            char[] col = new char[size];
            char[] diag = new char[len];
            char[] diag2 = new char[len];
            char[] diag3 = new char[len];
            char[] diag4 = new char[len];
            for (int j = 0; j < size; j++) {
                col[j] = list.get(j).charAt(i);
                if (j < len) {
                    diag[j] = list.get(j).charAt(i + j);
                    diag2[j] = list.get(j).charAt(size - 1 - (i + j));
                    diag3[j] = list.get(i + j).charAt(j);
                    diag4[j] = list.get(i + j).charAt(size - 1 - j);
                }
            }
            all.add(new String(diag));
            all.add(new String(diag2));
            if (i > 0) {
                all.add(new String(diag3));
                all.add(new String(diag4));
            }
            all.add(new String(col));
        }
        return all;
    }

    public long countCrossesMas(List<String> lines) {
        final Grid<Character> grid = Grid.parseCharactersGrid(lines, Function.identity());
        return grid.getPositionsOf('A').stream()
                .filter(p -> !grid.isOnTheEdge(p))
                .filter(p -> isCrossMas(grid, p)).count();
    }

    boolean isCrossMas(Grid<Character> grid, Position position) {
        return grid.get(position) == 'A' &&
                isMas(grid, position, Diagonal.NW, Diagonal.SE) && isMas(grid, position, Diagonal.NE, Diagonal.SW);
    }

    boolean isMas(Grid<Character> grid, Position position, Diagonal n, Diagonal s) {
        char nchar = grid.get(n.move(position));
        return switch (nchar) {
            case 'M' -> grid.get(s.move(position)) == 'S';
            case 'S' -> grid.get(s.move(position)) == 'M';
            default -> false;
        };
    }

    @Override
    public void run() {
        List<String> testLines = list(getTestInputPath());
        long xmas = allStrings(testLines).stream().mapToLong(this::countXMas).sum();
        System.out.println("Test XMAS count = " + xmas);
        System.out.println("Test X-MAS count = " + countCrossesMas(testLines));

        List<String> lines = list(getInputPath());
        xmas = allStrings(lines).stream().mapToLong(this::countXMas).sum();
        System.out.println("XMAS count = " + xmas);
        System.out.println("X-MAS count = " + countCrossesMas(lines));
    }
}
