package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        // read file from resources with a two column list
        Path path = (Paths.get("src/main/resources/input.txt"));
        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();
        // split each line by 3 spaces and convert to numbers
        Files.readAllLines(path).forEach(s -> {
            left.add(Integer.valueOf(s.split(" {3}")[0]));
            right.add(Integer.valueOf(s.split(" {3}")[1]));
        });

        // calculate total distance by comparing each row of the sorted lists
        int totalDistance = 0;
        left.sort(Comparator.naturalOrder());
        right.sort(Comparator.naturalOrder());
        for (int i = 0; i < left.size(); i++) {
            totalDistance += Math.abs(left.get(i) - right.get(i));
        }

        System.out.printf("Total distance: %d", totalDistance);
    }
}