package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        // use default or input from outside to execute sub method for daily solution
        int dayDefault = 2;
        int day = args.length == 0 ? dayDefault : Integer.parseInt(args[0]);
        switch(day) {
            case 1 -> day1();
            case 2 -> day2();
        }
    }

    private static void day2() throws IOException {
        // read file from resources with a two column list
        Path path = (Paths.get("src/main/resources/day2.txt"));
        List<List<Integer>> list = new ArrayList<>();
        // split each line by 3 spaces and convert to numbers
        Files.readAllLines(path).forEach(s -> list.add(Arrays.stream(s.split(" ")).map(Integer::valueOf)
                .collect(Collectors.toList())));

        // iterate over list of lists
        int saveCount = 0;
        for (List<Integer> l : list) {
            // in case l[0] and l[1] are equal it won't be decreasing, but stops in first inner loop
            boolean increasing = l.get(1) - l.get(0) > 0;
            boolean accept = true;
            for (int i = 1; i < l.size(); i++) {
                int previous = l.get(i - 1);
                int current = l.get(i);
                int diff = current - previous;
                // not safe if diff is 0 or > 3 or increasing/decreasing is broken
                if (diff == 0 || increasing && diff < 0
                        || !increasing && diff > 0
                        || Math.abs(diff) > 3) {
                    accept = false;
                    break;
                }
            }
            // if loop continued, accept is still true, so line was safe
            if (accept) {
                saveCount++;
            }
        }
        System.out.printf("Total safe reports: %d\n", saveCount);
    }

    private static void day1() throws IOException {
        // read file from resources with a two column list
        Path path = (Paths.get("src/main/resources/day1.txt"));
        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();
        // split each line by 3 spaces and convert to numbers
        Files.readAllLines(path).forEach(s -> {
            left.add(Integer.valueOf(s.split(" {3}")[0]));
            right.add(Integer.valueOf(s.split(" {3}")[1]));
        });

        // calculate total distance by comparing each row of the sorted lists and take diff of the numbers
        int totalDistance = 0;
        left.sort(Comparator.naturalOrder());
        right.sort(Comparator.naturalOrder());
        for (int i = 0; i < left.size(); i++) {
            totalDistance += Math.abs(left.get(i) - right.get(i));
        }
        System.out.printf("Total distance: %d\n", totalDistance);

        // calculate total similarity score by comparing each row with the number of occurrences in the other
        int totalSimilarity = 0;
        for (int finalI : left) {
            int count = right.stream().filter(s -> s == finalI).toList().size();
            totalSimilarity += finalI * count;
        }
        System.out.printf("Total similarity: %d\n", totalSimilarity);
    }
}