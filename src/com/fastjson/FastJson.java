package com.fastjson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deepak on 13/06/2015.
 */
public class FastJson {
    private final char NAME_VALUE_SEPARATOR = ':';
    private final char FIELD_SEPARATOR = ',';
    private final char MESSAGE_START = '{';
    private final char MESSAGE_END = '}';
    private final char FIELD_HEADER = '"';
    private final String FIELD_END = "" + FIELD_SEPARATOR + FIELD_HEADER;
    private final String NULL_VALUE = "null";
    private final String BOOLEAN_TRUE = "true";
    private final String BOOLEAN_FALSE = "false";
    private final String DECIMAL_POINT = ".";
    private final String ESCAPE_FIELD_HEADER = "\"";
    private final String ESCAPE_FIELD_HEADER_REPLACE = "\\\"";

    public Map<String, Object> toMap(String jsonString) {
        int index = 1;
        final int length = jsonString.length() - 1;
        Map<String, Object> mapOfFields = new HashMap<>();
        while (index < length - 1) {
            final int endTag = jsonString.indexOf(NAME_VALUE_SEPARATOR, index);
            int endValue = jsonString.indexOf(FIELD_END, endTag + 1);
            if (endValue == -1) {
                endValue = jsonString.indexOf(MESSAGE_END, endTag + 1);
            }
            String key = (String) jsonString.subSequence(index + 1, endTag - 1);
            String value = (String) jsonString.subSequence(endTag + 1, endValue);
            Object objValue = null;
            if (value.charAt(0) == (FIELD_HEADER)) {
                objValue = value.subSequence(1, value.length() - 1);
                String strValue = objValue.toString();
                if (objValue.equals(NULL_VALUE)) {
                    objValue = null;
                } else if (strValue.contains(ESCAPE_FIELD_HEADER)) {
                    strValue = strValue.replace(ESCAPE_FIELD_HEADER_REPLACE, ESCAPE_FIELD_HEADER);
                    objValue = strValue;
                }

            } else if (value.equals(BOOLEAN_FALSE)) {
                objValue = Boolean.FALSE;
            } else if (value.equals(BOOLEAN_TRUE)) {
                objValue = Boolean.TRUE;
            } else if (value.contains(DECIMAL_POINT)) {
                objValue = Double.parseDouble(value);
            } else {
                objValue = Long.parseLong(value);
            }
            mapOfFields.put(key, objValue);
            index = endValue + 1;
        }
        return mapOfFields;
    }

    public String toJson(Map<String, Object> map) {
        StringBuilder builder = new StringBuilder();
        builder.append(MESSAGE_START);
        map.entrySet().forEach(e -> {
            builder.append(FIELD_HEADER).append(e.getKey()).append(FIELD_HEADER).append(NAME_VALUE_SEPARATOR);
            Object value = e.getValue();
            if (value != null) {
                if (value instanceof Number || value instanceof Boolean) {
                    builder.append(value);
                } else {
                    String strValue = value.toString();
                    if (strValue.contains(ESCAPE_FIELD_HEADER)) {
                        strValue = strValue.replace(ESCAPE_FIELD_HEADER, ESCAPE_FIELD_HEADER_REPLACE);
                    }
                    builder.append(FIELD_HEADER).append(strValue).append(FIELD_HEADER);
                }
            } else {
                builder.append(FIELD_HEADER).append(NULL_VALUE).append(FIELD_HEADER);
            }
            builder.append(FIELD_SEPARATOR);

        });
        builder.deleteCharAt(builder.length() - 1);
        builder.append(MESSAGE_END);
        return builder.toString();
    }
}