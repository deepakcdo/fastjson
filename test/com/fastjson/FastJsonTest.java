package com.fastjson;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * Created by Deepak on 13/06/2015.
 */
public class FastJsonTest {

    private Map<String, Object> testdata = new HashMap<>();
    private FastJson fastJson = new FastJson();

    @Before
    public void setUp() {
        testdata.put("String", "Name");
        testdata.put("long", 566l);
        testdata.put("double", 34.5);
        testdata.put("double2", 3.0);
        testdata.put("boolean_f", false);
        testdata.put("boolean_t", true);
        testdata.put("Stringwithcomma", "Name, Lastname");
        testdata.put("StringwithspeechMark", "Name, \"Lastname\"");
    }

    @Test
    public void testData() {
        String jsonString = fastJson.toJson(testdata);
        Map<String, Object> objectMap = fastJson.toMap(jsonString);
        objectMap.entrySet().forEach(e -> {
            Object oldValue = testdata.get(e.getKey());
            assertEquals("Comparing " + e.getKey(), oldValue, e.getValue());
        });
    }
}