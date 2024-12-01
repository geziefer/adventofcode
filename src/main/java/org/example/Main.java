package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Path path = (Paths.get("src/main/resources/input.txt"));
        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();
        try {
            Files.readAllLines(path).stream().forEach(s -> {
                left.add(Integer.valueOf(s.split("   ")[0]));
                right.add(Integer.valueOf(s.split("   ")[1]));
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int totalDistance = 0;
        left.sort(Comparator.naturalOrder());
        right.sort(Comparator.naturalOrder());
        for (int i = 0; i < left.size(); i++) {
            totalDistance += Math.abs(left.get(i) - right.get(i));
        }

        System.out.printf("Total distance: %d", totalDistance);
    }
}