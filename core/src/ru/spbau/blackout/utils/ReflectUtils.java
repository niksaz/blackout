package ru.spbau.blackout.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * Reflection utility.
 */
public final class ReflectUtils {  // FIXME: create tests
    private ReflectUtils() {}


    /**
     * Returns all fields including inherited.
     */
    public static void getAllFields(Class<?> type, Collection<Field> store) {
        store.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(type.getSuperclass(), store);
        }
    }

    /**
     * Returns all fields including inherited.
     */
    public static List<Field> getAllFields(Class<?> type) {
        List<Field> store = new ArrayList<>();
        getAllFields(type, store);
        return store;
    }


    /**
     * Recursively finds all fields or containing elements which are instances of <code>baseClass</code>.
     * Finds only in collections, arrays and classes in this project.
     */
    public static <T> void findAllImpls(Object root, Class<T> baseClass, Collection<T> store) {
        Finder<T> finder = new Finder<>(baseClass, store);
        finder.add(root);
    }

    /**
     * Recursively finds all fields or containing elements which are instances of <code>baseClass</code>.
     * Finds only in collections, arrays and classes in this project.
     */
    public static <T> List<T> findAllImpls(Object root, Class<T> baseClass) {
        List<T> store = new ArrayList<>();
        findAllImpls(root, baseClass, store);
        return store;
    }
}
