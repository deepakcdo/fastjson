package com.fastjson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Created by Deepak on 13/06/2015.
 */
public class JsonPerformance {

    private Map<String, Object> testdata = new HashMap<>();
    private FastJson fastJson = new FastJson();
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() {
        testdata.put("String", "Name");
        testdata.put("long", System.currentTimeMillis());
        testdata.put("double", 34.5);
        testdata.put("double2", 3.0);
        testdata.put("boolean_f", false);
        testdata.put("boolean_t", true);
        testdata.put("Stringwithcomma", "Name, Lastname");
        testdata.put("StringwithspeechMark", "Name, \"Lastname\"");
        testdata.putAll(System.getenv());
//        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, false);
    }

    @Test
    public void runTest() {
        runTests("Jackson", this::readValue, this::writeValue);
        runTests("FastJson", fastJson::toMap, fastJson::toJson);
    }

    private Map<String, Object> readValue(String input) {
        try {
            return mapper.readValue(input, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String writeValue(Map<String, Object> input) {
        try {
            return mapper.writeValueAsString(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void runTests(String name, Function<String, Map<String, Object>> decodeFunction,
                          Function<Map<String, Object>, String> encodeFunction) {

        String testJson = encodeFunction.apply(testdata);
        Map<String, Object> testDecoded = decodeFunction.apply(testJson);
//        assertEquals(testdata, testDecoded);
        testDecoded.entrySet().forEach(e -> {
            Object oldValue = testdata.get(e.getKey());
            assertEquals("Comparing " + e.getKey(), oldValue, e.getValue());
        });
        IntStream intStream = IntStream.rangeClosed(0, 1001000);
        LongSummaryStatistics encodeStats = new LongSummaryStatistics();
        LongSummaryStatistics decodeStats = new LongSummaryStatistics();
        intStream.forEach(i -> {
            long startTime = System.nanoTime();
            String json = encodeFunction.apply(testdata);
            long timeToCreateJson = System.nanoTime();
            Map<String, Object> decoded = decodeFunction.apply(json);
            long timeToCreateMap = System.nanoTime();
            if (i > 1000) {
                encodeStats.accept((timeToCreateJson - startTime) / 1000);
                decodeStats.accept((timeToCreateMap - timeToCreateJson) / 1000);
            }
        });
        System.out.println(name + " Encoding " + encodeStats);
        System.out.println(name + " Decoding " + decodeStats);
    }

}
