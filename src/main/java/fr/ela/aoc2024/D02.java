package fr.ela.aoc2024;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class D02 extends AoC {

    record Report(List<Integer> ints, boolean increase) {

        public Report(List<Integer> ints) {
            this(ints, ints.get(0) < ints.get(1));
        }

        public Report(String s) {
            this(Arrays.stream(s.split("\\s")).map(Integer::parseInt).toList());
        }

        public boolean isSafe() {
            Iterator<Integer> it = ints.iterator();
            int prev = it.next();
            while (it.hasNext()) {
                int next = it.next();
                boolean safe = safe(prev, next);
                if (!safe) {
                    return false;
                }
                prev = next;
            }
            return true;
        }

        private Report remove(int index) {
            List<Integer> list = new ArrayList<>(ints);
            list.remove(index);
            return new Report(list);
        }

        private boolean isSafeSkippingOne() {
            return IntStream.range(0, ints.size()).mapToObj(this::remove).anyMatch(Report::isSafe);
        }

        private boolean safe(int prev, int next) {
            if (prev == next) {
                return false;
            }
            return increase == next > prev && Math.abs(prev - next) <= 3;
        }

    }

    @Override
    public void run() {
        Map<Boolean, List<Report>> testReports = stream(getTestInputPath(), Report::new).collect(Collectors.groupingBy(Report::isSafe));
        long safe = testReports.get(Boolean.TRUE).size();
        System.out.println("Test part one : " + safe);

        safe += testReports.get(Boolean.FALSE).stream().filter(Report::isSafeSkippingOne).count();
        System.out.println("Test part two : " + safe);

        Map<Boolean, List<Report>> reports = stream(getInputPath(), Report::new).collect(Collectors.groupingBy(Report::isSafe));
        safe = reports.get(Boolean.TRUE).size();
        System.out.println("Part one : " + safe);
        safe += reports.get(Boolean.FALSE).stream().filter(Report::isSafeSkippingOne).count();
        System.out.println("Part two : " + safe);


        //System.out.println("Part one : "+reports.stream().filter(r -> r.isSafe(true)).count());
    }
}
