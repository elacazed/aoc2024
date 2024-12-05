package fr.ela.aoc2024;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class D05 extends AoC {

    record Rule(int value, List<Integer> before, List<Integer> after) {
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

    boolean isOrdered(List<Integer> pages,Map<Integer, Rule> rules) {
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

    private int findMiddle(List<Integer> integers) {
        return integers.get((integers.size() - 1)/2);
    }

    private List<Integer> toIntegers(String s) {
        return Arrays.stream(s.split(",")).map(Integer::parseInt).toList();
    }

    void solve(Path path) {
        var input = splitOnEmptyLines(path);
        var rules = loadRules(input.get(0));

        var ordered = input.get(1).stream().map(this::toIntegers)
                .filter(l -> isOrdered(l, rules))
                .mapToInt(this::findMiddle)
                .sum();
        System.out.println("Part 1 : "+ordered);
    }


    @Override
    public void run() {
        solve(getTestInputPath());
        solve(getInputPath());
    }
}
