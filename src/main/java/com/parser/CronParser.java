package com.parser;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * CronParser parses a cron expression and expands each field to show the times at which it will run.
 */
public class CronParser {

    private static final Map<FieldType, int[]> ALL_VALUES = new HashMap<>();

    static {
        ALL_VALUES.put(FieldType.MINUTE, IntStream.range(0, 60).toArray());
        ALL_VALUES.put(FieldType.HOUR, IntStream.range(0, 24).toArray());
        ALL_VALUES.put(FieldType.DAY_OF_MONTH, IntStream.rangeClosed(1, 31).toArray());
        ALL_VALUES.put(FieldType.MONTH, IntStream.rangeClosed(1, 12).toArray());
        ALL_VALUES.put(FieldType.DAY_OF_WEEK, IntStream.rangeClosed(0, 7).toArray());
    }

    public static void main(String[] args) {
        final String cronConfig = checkNotNull(args[0]);
        System.out.println(expand(cronConfig));
    }

    /**
     * Expands each field of given cron expression to show the times at which it will run.
     *
     * @param cronConfig Cron schedule expression.
     * @return a table with the field name taking first 14 columns and the times as a space separated
     *         field following it.
     */
    static String expand(final String cronConfig) {
        checkTrue(isValid(cronConfig), "Invalid cron format " + cronConfig);

        return parse(cronConfig)
                .map(CronParser::format)
                .collect(Collectors.joining("\n"));
    }

    static boolean isValid(final String cronConfig) {
        final String[] fields = cronConfig.split(" ");
        return fields.length == 6;
    }

    static Stream<Map.Entry<FieldType, String>> parse(final String cronConfig) {
        return cronConfigPerField(cronConfig)
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), expandField(e.getKey(), e.getValue())));
    }

    private static Stream<Map.Entry<FieldType, String>> cronConfigPerField(final String cronConfig) {
        final String[] configs = cronConfig.split(" ");
        final FieldType[] fieldTypes = FieldType.values();
        return IntStream.range(0, fieldTypes.length)
                .mapToObj(i -> new AbstractMap.SimpleEntry<>(fieldTypes[i], configs[i]));
    }

    static String expandField(final FieldType fieldType, final String cronConfig) {
        if (fieldType.equals(FieldType.COMMAND)) {
            return cronConfig;
        }

        final String[] parts = cronConfig.split("/", 2);
        checkTrue(parts.length <= 2, "Invalid cron format, more than one '/' used " + cronConfig);
        final int stepSize = parts.length == 2 ? parseInt(parts[1], fieldType) : 1;
        final Stream<Integer> validEntries =
                validEntries(allPossibleEntries(parts[0], fieldType), stepSize);
        return validEntries.map(Object::toString).collect(Collectors.joining(" "));
    }

    static int[] allPossibleEntries(final String cronConfig, final FieldType fieldType) {
        if (cronConfig.equals("*")) {
            return ALL_VALUES.get(fieldType);
        }
        return Arrays.stream(cronConfig.split(","))
                .flatMap(config -> entries(config, fieldType))
                .mapToInt(i -> i)
                .toArray();
    }

    static Stream<Integer> entries(final String cronConfig, final FieldType fieldType) {
        if (cronConfig.contains("-")) {
            final String[] rangeStartAndEnd = cronConfig.split("-", 2);
            return IntStream.rangeClosed(parseInt(rangeStartAndEnd[0], fieldType),
                    parseInt(rangeStartAndEnd[1], fieldType)).boxed();
        } else {
            return IntStream.of(parseInt(cronConfig, fieldType)).boxed();
        }
    }

    static Integer parseInt(final String value, final FieldType fieldType) {
        final int val = Integer.parseInt(value);
        final int[] allValues = ALL_VALUES.get(fieldType);
        if (val >= allValues[0] && val <= allValues[allValues.length - 1]) {
            return val;
        }
        throw new RuntimeException("Invalid value " + value + " for field " + fieldType.getName());
    }

    static Stream<Integer> validEntries(final int[] possibleEntries, final int stepSize) {
        return IntStream.range(0, possibleEntries.length)
                .filter(i -> i % stepSize == 0)
                .map(i -> possibleEntries[i])
                .boxed();
    }

    private static String format(final Map.Entry<FieldType, String> entry) {
        return String.format("%-14s", entry.getKey().getName()) + entry.getValue();
    }

    private static <T> T checkNotNull(T input) {
        if (input != null) {
            return input;
        } else {
            throw new NullPointerException("invalid null object found");
        }
    }

    private static void checkTrue(final boolean predicate, final String errorMessage) {
        if (!predicate) {
            throw new RuntimeException(errorMessage);
        }
    }

    public enum FieldType {
        MINUTE ("minute"),
        HOUR ("hour"),
        DAY_OF_MONTH ("day of month"),
        MONTH ("month"),
        DAY_OF_WEEK ("day of week"),
        COMMAND ("command");

        final String name;

        FieldType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
