package fr.ela.aoc2024;

import java.util.stream.IntStream;

public class D09 extends AoC {

    static final char[] CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private class FileSystem {
        final int[] blocks;
        final String blocksList;

        public FileSystem(String s) {
            int size = s.chars().map(c -> c - '0').sum();
            blocks = new int[size];
            this.blocksList = s;
            boolean file = true;
            int offset = 0;
            int id = 0;
            for (char c : s.toCharArray()) {
                int length = c - '0';
                for (int i = 0; i < length; i++) {
                    if (file) {
                        blocks[offset + i] = id;
                    } else {
                        blocks[offset + i] = -1;
                    }
                }
                if (file) {
                    id++;
                }
                offset += length;
                file = !file;
            }
        }

        public String toString() {
            char[] line = new char[blocks.length];
            for (int i = 0; i < blocks.length; i++) {
                int c = blocks[i];
                line[i] = c == -1 ? '.' : CHARS[c % 62];
            }
            return new String(line);
        }

        public void defrag() {
            int left = 0;
            int right = blocks.length - 1;
            while (left < right) {
                while (blocks[left] != -1) {
                    left++;
                }
                while (blocks[right] == -1) {
                    right--;
                }
                if (left < right) {
                    int temp = blocks[left];
                    blocks[left] = blocks[right];
                    blocks[right] = temp;
                } else {
                    break;
                }
            }
        }


        long checksum() {
            defrag();
            return IntStream.range(0, blocks.length)
                    .filter(i -> blocks[i] > 0)
                    .mapToLong(i -> (long)(i * blocks[i]))
                    .sum();
        }
    }



    public void solve(String input, String s, long expected1, long expected2) {
        System.out.println("--- " + s + " ----");
        long time = System.currentTimeMillis();
        FileSystem fs = new FileSystem(input);
        long res = fs.checksum();
        time = System.currentTimeMillis() - time;
        System.out.println("Part 1 (" + expected1 + ") : " + res + " - " + time);
        time = System.currentTimeMillis();
        time = System.currentTimeMillis() - time;
        System.out.println("Part 2 (" + expected2 + ") : " + res + " - " + time);
    }


    @Override
    public void run() {
        solve("2333133121414131402", "Test", 1928, 0L);
        solve(readFile(getInputPath()), "Real", 6435922584968L, 0L);
    }
}
