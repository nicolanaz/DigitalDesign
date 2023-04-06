package com.digdes.school;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaSchoolStarter {
    private List<Map<String, Object>> table = new ArrayList<>();

    public JavaSchoolStarter() {}

    //todo WHERE cases
    public List<Map<String, Object>> execute(String request) throws Exception {
        String command = request.split(" ")[0].toUpperCase();

        switch (command) {
            case "INSERT": {
                String values = request.substring(14);
                String[] properties = values.split("\\s*,\\s*");

                Map<String, Object> columns = new HashMap<>();
                for (String property : properties) {
                    getPair(property, columns);
                }
                table.add(columns);
                break;
            }
            case "SELECT": {
                if (containsWhere(request)) {
                    return getWhere(request);
                } else {
                    return table;
                }
            }
            case "UPDATE": {
                String values = request.substring(14);
                if (containsWhere(request)) {
                    List<Map<String, Object>> columns = getWhere(request);
                    values = values.split("(?i)\\bwhere\\b")[0];
                    update(columns, values);
                } else {
                    update(table, values);
                }
                break;
            }
            case "DELETE": {
                if (containsWhere(request)) {
                    List<Map<String, Object>> columns = getWhere(request);
                    delete(columns);
                } else {
                    table = new ArrayList<>();
                }
                break;
            }
            default: throw new Exception();
        }

        return null;
    }

    private void getPair(String value, Map<String, Object> map) throws Exception {
        String[] property = value.split("\\s*=\\s*");
        if (property[1].equals("null")) {
            return;
        }

        switch (property[0].substring(1, property[0].length() - 1).toUpperCase()) {
            case "ID":
                map.put("'id'", Long.parseLong(property[1]));
                break;
            case "AGE":
                map.put("'age'", Long.parseLong(property[1]));
                break;
            case "LASTNAME":
                map.put("'lastName'", property[1].substring(1, property[1].length() - 1));
                break;
            case "COST":
                map.put("'cost'", Double.parseDouble(property[1]));
                break;
            case "ACTIVE":
                map.put("'active'", Boolean.parseBoolean(property[1]));
                break;
            default: throw new Exception();
        }
    }

    private List<Map<String, Object>> getWhere(String query) throws Exception {
        String where = query.split("\\s*(?i)where\\s*")[1];

        if (where.contains("and")) {
            String[] pairs = where.split(" and ");
            return getWhereAND(pairs);
        } else {
            String[] pairs = where.split(" or ");
            return getWhereOR(pairs);
        }
    }

    private List<Map<String, Object>> getWhereOR(String[] pairs) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> column : table) {
            boolean isCorrect = false;

            for (String pair : pairs) {
                String[] keyAndValue = pair.split("\\s*(>=|<=|=|!=|>|<|like|ilike)\\s*");

                String key = keyAndValue[0];
                String whereValue = keyAndValue[1];
                Object valueFromTable = getByKey(column, key);
                String operator =
                        pair.toLowerCase().contains("ilike") ? "ilike" :
                                pair.toLowerCase().contains("like") ? "like" :
                                        pair.replaceAll(".*?([=!><]+)=*.*", "$1");
                try {
                    switch (operator) {
                        case "=": {
                            if (valueFromTable instanceof Double) {
                                isCorrect = valuesEquals(valueFromTable, Double.parseDouble(whereValue));
                            } else if (valueFromTable instanceof Long) {
                                isCorrect = valuesEquals(valueFromTable, Long.parseLong(whereValue));
                            } else {
                                isCorrect = valuesEquals(valueFromTable, whereValue);
                            }
                            break;
                        }
                        case "!=": {
                            if (valueFromTable instanceof Double) {
                                isCorrect = valuesNotEquals(valueFromTable, Double.parseDouble(whereValue));
                            } else if (valueFromTable instanceof Long) {
                                isCorrect = valuesNotEquals(valueFromTable, Long.parseLong(whereValue));
                            } else {
                                isCorrect = valuesNotEquals(valueFromTable, whereValue);
                            }
                            break;
                        }
                        case ">": {
                            long x = (Long) valueFromTable;
                            long y = Long.parseLong(whereValue);
                            isCorrect = x > y;
                            break;
                        }
                        case "<": {
                            long x = (Long) valueFromTable;
                            long y = Long.parseLong(whereValue);
                            isCorrect = x < y;
                            break;
                        }
                        case ">=": {
                            long x = (Long) valueFromTable;
                            long y = Long.parseLong(whereValue);
                            isCorrect = x >= y;
                            break;
                        }
                        case "<=": {
                            long x = (Long) valueFromTable;
                            long y = Long.parseLong(whereValue);
                            isCorrect = x <= y;
                            break;
                        }
                        case "like": {
                            isCorrect = like((String) valueFromTable, whereValue);
                            break;
                        }
                        case "ilike": {
                            isCorrect = ilike((String) valueFromTable, whereValue);
                        }
                    }
                } catch (Exception e) {
                    throw new Exception();
                }

                if (isCorrect) {
                    break;
                }
            }
            if (isCorrect) {
                result.add(column);
            }
        }

        return result;
    }

    private List<Map<String, Object>> getWhereAND(String[] pairs) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> column : table) {
            boolean isCorrect = true;

            for (String pair : pairs) {
                String[] keyAndValue = pair.split("\\s*(>=|<=|=|!=|>|<|like|ilike)\\s*");

                String key = keyAndValue[0];
                String whereValue = keyAndValue[1];
                Object valueFromTable = getByKey(column, key);
                String operator =
                        pair.contains("ilike") ? "ilike" :
                                pair.contains("like") ? "like" :
                                        pair.replaceAll(".*?([=!><]+)=*.*", "$1");
                try {
                    switch (operator) {
                        case "=": {
                            if (valueFromTable instanceof Double) {
                                isCorrect = valuesEquals(valueFromTable, Double.parseDouble(whereValue));
                            } else if (valueFromTable instanceof Long) {
                                isCorrect = valuesEquals(valueFromTable, Long.parseLong(whereValue));
                            } else {
                                isCorrect = valuesEquals(valueFromTable, whereValue);
                            }
                            break;
                        }
                        case "!=": {
                            if (valueFromTable instanceof Double) {
                                isCorrect = valuesNotEquals(valueFromTable, Double.parseDouble(whereValue));
                            } else if (valueFromTable instanceof Long) {
                                isCorrect = valuesNotEquals(valueFromTable, Long.parseLong(whereValue));
                            } else {
                                isCorrect = valuesNotEquals(valueFromTable, whereValue);
                            }
                            break;
                        }
                        case ">": {
                            long x = (Long) valueFromTable;
                            long y = Long.parseLong(whereValue);
                            isCorrect = x > y;
                            break;
                        }
                        case "<": {
                            long x = (Long) valueFromTable;
                            long y = Long.parseLong(whereValue);
                            isCorrect = x < y;
                            break;
                        }
                        case ">=": {
                            long x = (Long) valueFromTable;
                            long y = Long.parseLong(whereValue);
                            isCorrect = x >= y;
                            break;
                        }
                        case "<=": {
                            long x = (Long) valueFromTable;
                            long y = Long.parseLong(whereValue);
                            isCorrect = x <= y;
                            break;
                        }
                        case "like": {
                            isCorrect = like((String) valueFromTable, whereValue);
                            break;
                        }
                        case "ilike": {
                            isCorrect = ilike((String) valueFromTable, whereValue);
                        }
                    }
                } catch (Exception e) {
                    throw new Exception();
                }
                if (!isCorrect) {
                    break;
                }
            }
            if (isCorrect) {
                result.add(column);
            }
        }

        return result;
    }

    private void update(List<Map<String, Object>> toUpdate, String values) {
        String[] pairs = values.split("\\s*,\\s*");
        try {
            for (Map<String, Object> column : toUpdate) {
                for (String pair : pairs) {
                    if (pair.contains("null")) {
                        String key = pair.split("\\s*=\\s*")[0];
                        column.remove(key);
                        continue;
                    }
                    getPair(pair, column);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void delete(List<Map<String, Object>> toDelete) {
        for (Map<String, Object> line : toDelete) {
            table.remove(line);
        }
    }

    private boolean valuesEquals(Object o1, Object o2) {
        return o1.equals(o2);
    }

    private boolean valuesNotEquals(Object o1, Object o2) {
        return !valuesEquals(o1, o2);
    }

    private boolean like(String str, String regex) {
        String regexJavaStyle = regex.substring(1, regex.length() - 1).replaceAll("%", ".*");
        return str.matches(regexJavaStyle);
    }

    private boolean ilike(String str, String regex) {
        return like(str.toLowerCase(), regex.toLowerCase());
    }

    private boolean containsWhere(String query) {
        String[] splited = query.split(" ");

        for (String word : splited) {
            if (word.equalsIgnoreCase("where")) {
                return true;
            }
        }

        return false;
    }

    private Object getByKey(Map<String, Object> columns, String key) {
        for (String keyFromTable : columns.keySet()) {
            if (key.equalsIgnoreCase(keyFromTable)) {
                return columns.get(keyFromTable);
            }
        }
        return false;
    }
}
