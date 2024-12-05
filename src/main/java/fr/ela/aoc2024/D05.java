package fr.ela.aoc2024;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class D05 extends AoC {

    record Rule(int value, List<Integer> before, List<Integer> after) implements Comparable<Rule> {

        public int compareTo(Rule o) {
            if (this.before.contains(o.value)) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    Map<Integer, Rule> loadRules(List<String> list) {
        Map<Integer, List<Integer>> afters = new HashMap<>();
        Map<Integer, List<Integer>> befores = new HashMap<>();
        Set<Integer> values = new HashSet<>();
        list.stream().map(l -> l.split("\\|"))
                .forEach(rule -> {
                    int before = Integer.parseInt(rule[0]);
                    int after = Integer.parseInt(rule[1]);
                    values.add(before);
                    values.add(after);
                    afters.computeIfAbsent(before, ArrayList::new).add(after);
                    befores.computeIfAbsent(after, ArrayList::new).add(before);
                });
        return values.stream().map(v -> new Rule(v, befores.getOrDefault(v, List.of()), afters.getOrDefault(v, List.of())))
                .collect(Collectors.toMap(Rule::value, Function.identity()));
    }

    boolean isOrdered(List<Integer> pages, Map<Integer, Rule> rules) {
        for (int i = 0; i < pages.size(); i++) {
            Rule rule = rules.get(pages.get(i));
            for (int j = 0; j < i; j++) {
                if (!rule.before.contains(pages.get(j))) {
                    return false;
                }
            }
            for (int j = i + 1; j < pages.size(); j++) {
                if (!rule.after.contains(pages.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    private <T> T findMiddle(List<T> list) {
        return list.get((list.size() - 1) / 2);
    }

    private List<Integer> toIntegers(String s) {
        return Arrays.stream(s.split(",")).map(Integer::parseInt).toList();
    }

    private List<Integer> sort(List<Integer> input, Map<Integer, Rule> rules) {
        // Sort the Pages, find the middle
        List<Rule> sort = new ArrayList<>(input.stream().map(rules::get).toList());
        sort.sort(Comparator.naturalOrder());
        return sort.stream().map(Rule::value).toList();
    }

    void solve(Path path) {
        var input = splitOnEmptyLines(path);
        var rules = loadRules(input.get(0));
        Map<Boolean, List<List<Integer>>> inputs = input.get(1).stream().map(this::toIntegers)
                .collect(Collectors.groupingBy(l -> isOrdered(l, rules)));

        var ordered = inputs.get(Boolean.TRUE).stream()
                .mapToInt(this::findMiddle)
                .sum();
        System.out.println("Part 1 : " + ordered);
        var sorted = inputs.get(Boolean.FALSE).stream()
                .mapToInt(list -> findMiddle(sort(list, rules)))
                .sum();
        System.out.println("Part 2 : " + sorted);
    }

    @Override
    public void run() {
        solve(getTestInputPath());
        solve(getInputPath());
    }
}