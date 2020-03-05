package com.parser;

import com.parser.CronParser.FieldType;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class CronParserTest {

    @Test
    public void testParse() {
        final Map<FieldType, String> expected = new HashMap<>();
        expected.put(FieldType.MINUTE, "0 15 30 45");
        expected.put(FieldType.HOUR, "0");
        expected.put(FieldType.DAY_OF_MONTH, "1 15");
        expected.put(FieldType.MONTH, "1 2 3 4 5 6 7 8 9 10 11 12");
        expected.put(FieldType.DAY_OF_WEEK, "1 2 3 4 5");
        expected.put(FieldType.COMMAND, "/usr/bin/find");
        assertEquals(expected,
                CronParser.parse("*/15 0 1,15 * 1-5 /usr/bin/find")
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    @Test(expected = RuntimeException.class)
    public void testParseInvalidCommand() {
        // dayofMonth should be within 1-31 so 45 is invalid value here.
        final String result = CronParser.expand("*/15 0 1,45 * 1-5 /usr/bin/find");
    }

    @Test
    public void testParseField() {
        assertEquals(
                IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList()),
                CronParser.entries("1-10", FieldType.DAY_OF_MONTH).collect(Collectors.toList()));
        assertEquals(
                IntStream.rangeClosed(1, 1).boxed().collect(Collectors.toList()),
                CronParser.entries("1", FieldType.MINUTE).collect(Collectors.toList()));
    }

    @Test
    public void testMembers() {
        assertArrayEquals(
                IntStream.rangeClosed(1, 10).toArray(),
                CronParser.allPossibleEntries("1-10", FieldType.DAY_OF_MONTH));
        assertArrayEquals(
                IntStream.rangeClosed(1, 10).toArray(),
                CronParser.allPossibleEntries("1,2,3,4,5,6,7,8,9,10", FieldType.DAY_OF_MONTH));
        assertArrayEquals(
                new int[] {1, 2, 3, 11, 12, 15},
                CronParser.allPossibleEntries("1-3,11,12,15", FieldType.DAY_OF_MONTH));
        assertArrayEquals(
                IntStream.rangeClosed(0, 7).toArray(),
                CronParser.allPossibleEntries("*", FieldType.DAY_OF_WEEK));
    }

    @Test
    public void testSchedule() {
        assertEquals(
                "1 2 3",
                CronParser.expandField(FieldType.DAY_OF_MONTH, "1-3"));
        assertEquals(
                "1 3",
                CronParser.expandField(FieldType.DAY_OF_MONTH, "1-3/2"));
    }

    @Test
    public void testMaterialize() {
        assertEquals(
                Arrays.asList(1, 3),
                CronParser.validEntries(new int[] { 1, 2, 3 }, 2).collect(Collectors.toList()));
        assertEquals(
                Arrays.asList(1, 3, 5),
                CronParser.validEntries(new int[] { 1, 3, 5 }, 1).collect(Collectors.toList()));
    }

    @Test
    public void testisValid() {
        assertFalse(CronParser.isValid("1 * * * *"));
    }
}
