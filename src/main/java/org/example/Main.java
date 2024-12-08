package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        // use default or input from outside to execute sub method for daily solution
        int dayDefault = 8;
        int day = args.length == 0 ? dayDefault : Integer.parseInt(args[0]);
        switch (day) {
            case 1 -> day1();
            case 2 -> day2();
            case 3 -> day3();
            case 4 -> day4();
            case 5 -> day5();
            case 6 -> day6();
            case 7 -> day7();
            case 8 -> day8();
        }
    }

    private static void day8() throws IOException {
        // read file from resources
        Path path = (Paths.get("src/main/resources/day8.txt"));
        // read everything in a string list and then convert in one 2-dimensional character array just like the file was
        List<String> list = Files.readAllLines(path);
        // array for map with [x(j)=columns][y(i)=rows]
        char[][] antennaMap = new char[list.get(0).length()][list.size()];
        // 2nd map for marking antinodes
        char[][] antinodeMap = new char[list.get(0).length()][list.size()];
        // hash map for each antenna type and its position
        HashMap<Character, List<Point>> antennaPositions = new HashMap<>();
        for (int i = 0; i < antennaMap.length; i++) {
            for (int j = 0; j < antennaMap[0].length; j++) {
                Character c = list.get(i).charAt(j);
                antennaMap[j][i] = c;
                antinodeMap[j][i] = '.';
                // put Point in map in case it is not .
                if (c != '.') {
                    Point point = new Point(j, i);
                    List<Point> points = new ArrayList<>();
                    if (antennaPositions.get(c) != null) {
                        points = antennaPositions.get(c);
                    }
                    points.add(point);
                    antennaPositions.put(c, points);
                }
            }
        }

        int count = 0;
        for (Character c : antennaPositions.keySet()) {
            List<Point> points = antennaPositions.get(c);
            // compare each point with all other points as long as there are at least 2
            for (int i = 0; i < points.size() - 1; i++) {
                for (int j = i + 1; j < points.size(); j++) {
                    // for each pair of points calculate distance and expand in both directions and check if still in map
                    Point p1 = points.get(i);
                    Point p2 = points.get(j);
                    // assume p2 is further right and/or down, then dif will be positive
                    int difX = p2.x - p1.x;
                    int difY = p2.y - p1.y;
                    // when subtracting dif from p1 and adding to p2 it will be out of the 2 points no matter if assumption was right
                    Point anti1 = new Point(p1.x - difX, p1.y - difY);
                    Point anti2 = new Point(p2.x + difX, p2.y + difY);
                    // check if still in map, mark as antinode in case it is not there yet and count
                    if (anti1.x >= 0 && anti1.x < antennaMap.length && anti1.y >= 0 && anti1.y < antennaMap[0].length) {
                        if (antinodeMap[anti1.x][anti1.y] != '#') {
                            count++;
                            antinodeMap[anti1.x][anti1.y] = '#';
                        }
                    }
                    if (anti2.x >= 0 && anti2.x < antennaMap.length && anti2.y >= 0 && anti2.y < antennaMap[0].length) {
                        if (antinodeMap[anti2.x][anti2.y] != '#') {
                            count++;
                            antinodeMap[anti2.x][anti2.y] = '#';
                        }
                    }
                }
            }
        }

        System.out.printf("Total count of unique antinodes: %d\n", count);
    }

    private static void day7() throws IOException {
        // read file from resources
        Path path = (Paths.get("src/main/resources/day7.txt"));
        // read everything in hashmap with result pointing to list of lists of numbers in equation
        // (doubled list is due to possibility of several lists for same key)
        HashMap<Long, List<Long>> equations = new HashMap<>();
        List<String> list = Files.readAllLines(path);
        list.forEach(s -> {
            Long key = Long.valueOf(s.split(": ")[0]);
            String values = s.split(": ")[1];
            List<Long> valueList = Arrays.stream(values.split(" ")).map(Long::valueOf).collect(Collectors.toList());
            equations.put(key, valueList);
        });

        // exclude the duplicates from input to simplify result and manually add result later
        // 188: 9 4 2 -> 9||4*2 is solution
        // 678: 4 3 2 652 2 -> 4*3*2+652+2 is solution
        long sum = 188 + 678;

        // iterate through all equations
        for (Long key : equations.keySet()) {
            List<Long> values = equations.get(key);
            // we need 1 less operator than numbers in equation, and it has 3^n-1 combinations
            int max = (int) Math.pow(3, values.size() - 1) - 1;
            // use ternary value by using modulo from division for choosing either + (0) or * (1) or || (2)
            for (int i = 0; i <= max; i++) {
                Long result = values.get(0);
                for (int j = 1; j < values.size(); j++) {
                    Long nextValue = values.get(j);
                    int operation = (i / (int) Math.pow(3, j - 1)) % 3;
                    switch (operation) {
                        case 0 -> result += nextValue;
                        case 1 -> result *= nextValue;
                        case 2 -> result = Long.valueOf(result.toString().concat(nextValue.toString()));
                    }
                }
                if (result.longValue() == key.longValue()) {
                    sum += key;
                    break;
                }
            }
        }

        System.out.printf("Total sum of correct equations: %d\n", sum);
    }

    private static void day6() throws IOException {
        // read file from resources
        Path path = (Paths.get("src/main/resources/day6.txt"));
        // read everything in a string list and then convert in one 2-dimensional character array just like the file was
        List<String> list = Files.readAllLines(path);
        // define it [x(j)=columns][y(i)=rows]
        char[][] map = new char[list.get(0).length()][list.size()];
        int x = 0;
        int y = 0;
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).length(); j++) {
                char c = list.get(i).charAt(j);
                if (c == '^') {
                    x = j;
                    y = i;
                }
                map[j][i] = c;
            }
        }

        // 1st run to get initially visited positions, use copied array since it gets modified
        List<Integer[]> visitedPositions = new ArrayList<>();
        char[][] copiedMap = Arrays.stream(map).map(char[]::clone).toArray(char[][]::new);
        day6_runMap(x, y, copiedMap, visitedPositions);

        // run per each visited position containing an obstacle
        int count = 0;
        for (Integer[] pos : visitedPositions) {
            copiedMap = Arrays.stream(map).map(char[]::clone).toArray(char[][]::new);
            copiedMap[pos[0]][pos[1]] = '#';
            boolean closedLoop = day6_runMap(x, y, copiedMap, null);
            if (!closedLoop) {
                count++;
            }
        }

        System.out.printf("Total number of distinct positions: %d\n", count);
    }

    private static boolean day6_runMap(int x, int y, char[][] input, List<Integer[]> output) {
        // N, E, W, S
        int[] xDirs = {0, 1, 0, -1};
        int[] yDirs = {-1, 0, 1, 0};
        int currentDir = 0;
        // move guard and turn until he leaves the area, mark and count all positions (assume it will end)
        while (x >= 0 && y >= 0 && x < input.length && y < input[0].length) {
            int newX = x + xDirs[currentDir];
            int newY = y + yDirs[currentDir];
            if (newX >= 0 && newY >= 0 && newX < input.length && newY < input[0].length) {
                char next = input[newX][newY];
                // obstacle
                if (next == '#') {
                    // turn to next direction and circle around if end of dir structure is reached
                    currentDir = currentDir == 3 ? 0 : currentDir + 1;
                    // only turn, don't move, will be done next round, so keep position
                    newX = x;
                    newY = y;
                    // just skip starting position, don't count
                } else //noinspection StatementWithEmptyBody
                    if (next == '^') {
                        // do nothing
                        // first time visit
                    } else if (next == '.') {
                        // store current direction which this position was reached with as bit mask (not meant for printing)
                        input[newX][newY] = (char) (Math.pow(2, currentDir));
                        if (output != null) {
                            output.add(new Integer[]{newX, newY});
                        }
                        // multiple visit
                    } else {
                        byte posValue = (byte) input[newX][newY];
                        byte dirValue = (byte) (Math.pow(2, currentDir));

                        // if bit for dir exists, it means that we have visited this pos already in the same dir
                        if ((posValue & dirValue) > 0) {
                            return false;
                        }
                        input[newX][newY] = (char) (posValue | dirValue);
                    }
            }
            x = newX;
            y = newY;
        }
        // map was left, so no closed path
        return true;
    }

    private static void day5() throws IOException {
        // read file from resources
        Path path = (Paths.get("src/main/resources/day5.txt"));
        List<String> list = Files.readAllLines(path);
        HashMap<Integer, List<Integer>> rules = new HashMap<>();
        List<List<Integer>> updates = new ArrayList<>();
        boolean secondPart = false;
        for (String line : list) {
            // empty line is devider between parts
            if (line.length() == 0) {
                secondPart = true;
                continue;
            }
            // first part: every line is a rule, put it in a hash map
            if (!secondPart) {
                Integer key = Integer.parseInt(line.split("\\|")[0]);
                Integer value = Integer.parseInt(line.split("\\|")[1]);
                List<Integer> valueList = (rules.get(key) == null ? new ArrayList<>() : rules.get(key));
                valueList.add(value);
                rules.put(key, valueList);
                // second part (after empty line) every line is an update, put it in a list of lists
            } else {
                List<Integer> subList = new ArrayList<>();
                Arrays.stream(line.split(",")).forEach(s -> subList.add(Integer.parseInt(s)));
                updates.add(subList);
            }
        }

        Integer sum = 0;
        // examine each element of an update list and check if one of the following elements should have come before
        for (List<Integer> update : updates) {
            boolean wrongOrder = false;
            for (int i = 0; i < update.size() - 1; i++) {
                Integer updatePage = update.get(i);
                for (int j = i + 1; j < update.size(); j++) {
                    Integer otherPage = update.get(j);
                    List<Integer> rulesForUpdate = rules.get(otherPage);
                    if (rulesForUpdate != null) {
                        if (rulesForUpdate.contains(updatePage)) {
                            // swap numbers in list and compare next run against new swapped number
                            wrongOrder = true;
                            //noinspection SuspiciousListRemoveInLoop
                            update.remove(j);
                            update.add(i, otherPage);
                            updatePage = otherPage;
                        }
                    }
                }
            }
            if (wrongOrder) {
                Integer middleValue = update.get(update.size() / 2);
                sum += middleValue;
            }
        }

        System.out.printf("Total sum of middle values: %d\n", sum);
    }

    private static void day4() throws IOException {
        // read file from resources
        Path path = (Paths.get("src/main/resources/day4.txt"));
        // read everything in a string list and then convert in one 2-dimensional character array just like the file was
        List<String> list = Files.readAllLines(path);
        // define it [x(j)=columns][y(i)=rows]
        char[][] chars = new char[list.get(0).length()][list.size()];
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).length(); j++) {
                chars[j][i] = list.get(i).charAt(j);
            }
        }

        int count = 0;
        // iterate over each line and each character in character array and if it is an X search in all directions for XMAS
        for (int i = 1; i < list.size() - 1; i++) {
            for (int j = 1; j < list.get(i).length() - 1; j++) {
                if (chars[j][i] == 'A') {
                    // compare NE/SE and SE/SW for either M or S
                    if (((chars[j + 1][i - 1] == 'M' && chars[j - 1][i + 1] == 'S') || (chars[j + 1][i - 1] == 'S' && chars[j - 1][i + 1] == 'M')) &&
                            ((chars[j + 1][i + 1] == 'M' && chars[j - 1][i - 1] == 'S') || (chars[j + 1][i + 1] == 'S' && chars[j - 1][i - 1] == 'M'))) {
                        count++;
                    }
                }
            }
        }

        System.out.printf("Total count of XMAS: %d\n", count);
    }

    private static void day3() throws IOException {
        // read file from resources
        Path path = (Paths.get("src/main/resources/day3.txt"));
        // read everything in one big string and remove newlines
        String content = Files.readString(path).replace("\n", "");

        // relace everything from a don't() to the next do() to exclude disabled muls
        String shortened = content.replaceAll("don't\\(\\).*?do\\(\\)", "");

        // scan for every mul(aaa,bbb)
        Pattern pattern = Pattern.compile("mul\\(\\d{1,3},\\d{1,3}\\)");
        Matcher matcher = pattern.matcher(shortened);
        int total = 0;
        while (matcher.find()) {
            // parse each number part and multiply
            String element = matcher.group();
            int f1 = Integer.parseInt(element.substring(4, element.indexOf(',')));
            int f2 = Integer.parseInt(element.substring(element.indexOf(',') + 1, element.indexOf(')')));
            total += f1 * f2;
        }
        System.out.printf("Total sum of muls: %d\n", total);
    }

    private static void day2() throws IOException {
        // read file from resources
        Path path = (Paths.get("src/main/resources/day2.txt"));
        List<List<Integer>> list = new ArrayList<>();
        // split each line by spaces and convert to numbers
        Files.readAllLines(path).forEach(s -> list.add(Arrays.stream(s.split(" ")).map(Integer::valueOf)
                .collect(Collectors.toList())));

        // iterate over list of lists
        int saveCount = 0;
        List<List<Integer>> retryList1 = new ArrayList<>();
        List<List<Integer>> retryList2 = new ArrayList<>();
        // find all safe lines, return 2 retry lists
        saveCount += day2_saveCount(Collections.unmodifiableList(list), retryList1, retryList2, true);
        // first retry list contains unsafe lines with deleted report in the given order
        saveCount += day2_saveCount(retryList1, null, null, false);
        // second retry list contains unsafe lines with first report deleted
        saveCount += day2_saveCount(retryList2, null, null, false);
        System.out.printf("Total safe reports: %d\n", saveCount);
    }

    private static int day2_saveCount
            (List<List<Integer>> input, List<List<Integer>> output1, List<List<Integer>> output2,
             boolean damperActive) {
        int saveCount = 0;
        for (List<Integer> l : input) {
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
                    // damper run is first run to determine retry lists
                    if (damperActive) {
                        // first retry list removes current report as it does not fit the order
                        List<Integer> retryList1 = new ArrayList<>(l);
                        retryList1.remove(i);
                        output1.add(retryList1);
                        // second retry list removes first report in case that was the unsafe one
                        List<Integer> retryList2 = new ArrayList<>(l);
                        retryList2.remove(0);
                        output2.add(retryList2);
                    }
                    break;
                }
            }
            // if loop continued, accept is still true, so line was safe
            if (accept) {
                saveCount++;
            }
        }
        return saveCount;
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

    @SuppressWarnings("unused")
    private static void printList(Iterable<?> list) {
        list.forEach(s -> System.out.print(s.toString() + " "));
        System.out.println();
    }

    record Point(int x, int y) {
    }
}

