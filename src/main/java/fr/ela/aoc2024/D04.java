package fr.ela.aoc2024;


import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class D04 extends AoC {
    static Pattern XMAS = Pattern.compile("XMAS");
    static Pattern SAMX = Pattern.compile("SAMX");

    private int countXMas(String s) {
        return count(XMAS, s) + count(SAMX, s);
    }

    private int count(Pattern pattern, String s) {
        Matcher m = pattern.matcher(s);
        int count = 0;
        while (m.find()) {
            count++;
        }
        return count;
    }

    private List<String> allStrings(List<String> list) {
        int cols = list.get(0).length();
        int rows = list.size();
        List<String> all = new ArrayList<>(list.size() * 3 + cols * 3);
        all.addAll(list);
        for (int i = 0; i < cols; i++) {
            char[] col = new char[rows];
            int len = rows - i;
            char[] diag = new char[len];
            char[] diag2 = new char[len];
            for (int j = 0; j < rows; j++) {
                col[j] = list.get(j).charAt(i);
                if (j < len) {
                    diag[j] = list.get(j).charAt(i+j);
                    diag2[j] = list.get(j).charAt(cols - 1 - (i + j));
                }
            }
            all.add(new String(col));
            all.add(new String(diag));
            all.add(new String(diag2));
        }
        for (int i = 1; i < rows; i++) {
            int len = cols - i;
            char[] diag = new char[len];
            char[] diag2 = new char[len];
            for (int j = 0; j < len; j++) {
               diag[j] = list.get(i + j).charAt(j);
               diag2[j] = list.get(i + j).charAt(cols - 1 - j);
            }
            all.add(new String(diag));
            all.add(new String(diag2));
        }
        return all;
    }


    @Override
    public void run() {
        long xmas = allStrings(list(getTestInputPath())).stream().mapToInt(this::countXMas).sum();
        System.out.println("Test XMAS count = "+xmas);
        xmas = allStrings(list(getInputPath())).stream().mapToInt(this::countXMas).sum();
        System.out.println("XMAS count = "+xmas);
    }
}
