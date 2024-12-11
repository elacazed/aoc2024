package fr.ela.aoc2024.utils;

import java.util.Collection;
import java.util.Deque;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Path<K, V extends Comparable<V>> implements Comparable<Path<K, V>> {
    private final Deque<K> path;
    private final V cost;

    public Path(Deque<K> path, V cost) {
        this.path = path;
        this.cost = cost;
    }

    public V cost() {
        return cost;
    }

    public Stream<K> stream() {
        return path.stream();
    }

    public Collection<K> path() {
        return path;
    }

    public K start() {
        return path.getFirst();
    }

    public K end() {
        return path.getLast();
    }

    @Override
    public int compareTo(Path<K, V> o) {
        return this.cost.compareTo(o.cost);
    }

    public String toString() {
        return path.stream().map(Objects::toString).collect(Collectors.joining("->")) + " [" + cost + "]";
    }
}
