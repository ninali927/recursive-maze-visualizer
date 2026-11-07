package app;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * A helper class to inspect the maze data structures using reflection.
 * This allows the visualization backend to access data without requiring
 * the core classes to expose public getters for implementation details.
 */
public class ReflectionHelper {

    // Cache fields to avoid repeated lookups
    private static final Map<String, Field> fieldCache = new HashMap<>();

    /**
     * Gets a private or protected field from an object.
     * @param obj The object to inspect.
     * @param fieldName The name of the field to get.
     * @return The Field object, made accessible.
     * @throws NoSuchFieldException If the field doesn't exist.
     */
    private static Field getField(Object obj, String fieldName) throws NoSuchFieldException {
        String key = obj.getClass().getName() + "." + fieldName;
        if (fieldCache.containsKey(key)) {
            return fieldCache.get(key);
        }
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        fieldCache.put(key, field);
        return field;
    }

    /**
     * Reads the value of a field from an object.
     * @param obj The object to inspect.
     * @param fieldName The name of the field.
     * @return The value of the field.
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = getField(obj, fieldName);
            return field.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Reflection error getting field '" + fieldName + "'", e);
        }
    }

    /**
     * Reads the integer value of a field from an object.
     */
    public static int getIntField(Object obj, String fieldName) {
        return (int) getFieldValue(obj, fieldName);
    }

    /**
     * Reads the boolean value of a field from an object.
     */
    public static boolean getBooleanField(Object obj, String fieldName) {
        return (boolean) getFieldValue(obj, fieldName);
    }

    /**
     * Extracts all relevant data from a MazeCell object using reflection.
     * @param cell The MazeCell object (passed as a generic Object).
     * @return A map containing the cell's data (row, col, isWall, isVisited).
     */
    public static Map<String, Object> getCellData(Object cell) {
        Map<String, Object> data = new HashMap<>();
        data.put("row", getIntField(cell, "row"));
        data.put("col", getIntField(cell, "col"));
        data.put("isWall", getBooleanField(cell, "isWall"));
        data.put("isVisited", getBooleanField(cell, "isVisited"));
        return data;
    }
}
