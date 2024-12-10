package fr.ela.aoc2024;

import fr.ela.aoc2024.utils.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

public class D09 extends AoC {

    static final char[] CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public class FileSystemElement {
        int position;
        int size;

        public FileSystemElement(int position, int size) {
            this.position = position;
            this.size = size;
        }

        int nextElementPosition() {
            return position + size;
        }

        public String toString() {
            return position + "." + size;
        }
    }

    public class Hole extends FileSystemElement implements Comparable<Hole> {
        public Hole(int position, int size) {
            super(position, size);
        }

        @Override
        public int compareTo(Hole o) {
            int s = this.size - o.size;
            return s == 0 ? this.position - o.position : s;
        }
    }

    public class File extends FileSystemElement {
        final int id;

        public File(int id, int position, int size) {
            super(position, size);
            this.id = id;
        }

        public String toString() {
            return "[" + id + "]" + position + "." + size;
        }
    }

    private class FileSystem {
        final String filesystem;
        final int[] blocks;
        final List<File> files;
        final List<Hole> holes;

        public FileSystem(String s) {
            this.filesystem = s;
            files = new ArrayList<>(filesystem.length() / 2 + 1);
            holes = new ArrayList<>(filesystem.length() / 2 + 1);
            int size = s.chars().map(c -> c - '0').sum();
            blocks = new int[size];
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
                    File f = new File(id, offset, length);
                    files.add(f);
                    id++;
                } else {
                    Hole hole = new Hole(offset, length);
                    holes.add(hole);
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

        private Pair<Hole, Hole> surroundingHoles(File file) {
            for (int i = 1; i <= holes.size(); i++) {
                Hole before = holes.get(i - 1);
                if (file.position == before.nextElementPosition()) {
                    return new Pair<Hole, Hole>(before, i == holes.size() ? null : holes.get(i));
                }
            }
            throw new IllegalStateException();
        }

        public void defrag2() {
            for (int i = files.size() - 1; i > 1; i--) {
                File file = files.get(i);
                Iterator<Hole> holesIterator = holes.iterator();
                var hole = holesIterator.next();
                while (holesIterator.hasNext() && hole.size < file.size) {
                    hole = holesIterator.next();
                }
                if ((hole.position >= file.position)) {
                    continue;
                }
                if (hole.size >= file.size) {
                    Pair<Hole, Hole> surrounding = surroundingHoles(file);
                    surrounding.key().size += file.size;
                    if (surrounding.value() != null) {
                        surrounding.key().size = surrounding.key().size + file.size + surrounding.value().size;
                        surrounding.value().size = 0;
                    }
                    for (int j = 0; j < file.size; j++) {
                        blocks[hole.position + j] = file.id;
                        blocks[file.position + j] = -1;
                    }
                    hole.position = hole.position + file.size;
                    hole.size = hole.size - file.size;
                }
            }
        }

        long checksum() {
            return IntStream.range(0, blocks.length)
                    .filter(i -> blocks[i] > 0)
                    .mapToLong(i -> (long) (i * blocks[i]))
                    .sum();
        }
    }


    public void solve(String input, String s, long expected1, long expected2) {
        System.out.println("--- " + s + " ----");
        long time = System.currentTimeMillis();
        FileSystem fs = new FileSystem(input);
        fs.defrag();
        long res = fs.checksum();
        time = System.currentTimeMillis() - time;
        System.out.println("Part 1 (" + expected1 + ") : " + res + " - " + time);
        time = System.currentTimeMillis();
        fs = new FileSystem(input);
        fs.defrag2();
        res = fs.checksum();
        time = System.currentTimeMillis() - time;
        System.out.println("Part 2 (" + expected2 + ") : " + res + " - " + time);
    }


    @Override
    public void run() {
        solve("23331331214141314023", "Test", 1928, 2858L);
        solve(readFile(getInputPath()), "Real", 6435922584968L, 6469636832766L);
    }
}
