package ru.spbau.blackout.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;


/**
 * Reflection utility.
 */
public final class ReflectUtils {  // FIXME: create tests
    private ReflectUtils() {}


    private static final String PROJECT_PACKAGE_PREFIX = "ru.spbau.blackout";


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
     * {@see #getAllFields(Class, Collection)}
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
    public static <T> void findAllImpls(Object root, Class<T> baseClass, Collection<? super T> store) {
        Finder<T> finder = new Finder<>(baseClass, store);
        finder.find(root);
    }

    /**
     * {@see #getAllImpls(Object, Class, Collection)}
     */
    public static <T> List<T> findAllImpls(Object root, Class<T> baseClass) {
        List<T> store = new ArrayList<>();
        findAllImpls(root, baseClass, store);
        return store;
    }


    private static final class Finder<T> {
        Class<T> baseClass;
        Collection<? super T> store;
        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());

        public Finder(Class<T> baseClass, Collection<? super T> store) {
            this.baseClass = baseClass;
            this.store = store;
        }

        void find(Object root) {
            if (root == null || visited.contains(root)) {
                return;
            }

            visited.add(root);

            if (baseClass.isInstance(root)) {
                store.add(baseClass.cast(root));
            }

            if (root instanceof Collection) {
                for (Object obj : (Collection) root) {
                    find(obj);
                }
                return;
            }

            if (root instanceof Object[]) {
                for (Object obj : (Object[]) root) {
                    find(obj);
                }
                return;
            }

            Class<?> rootClass = root.getClass();

            if (!rootClass.getPackage().getName().startsWith(PROJECT_PACKAGE_PREFIX)) {
                return;
            }

            for (Field field : getAllFields(rootClass)) {
                field.setAccessible(true);

                try {
                    find(field.get(root));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e); // FIXME
                }
            }
        }
    }
}
