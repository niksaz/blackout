package ru.spbau.blackout.reflectutils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import static ru.spbau.blackout.reflectutils.ReflectUtils.getAllFields;

public class Finder<T> {

    private static final String PROJECT_PACKAGE_PREFIX = "ru.spbau.blackout";

    private final Class<T> baseClass;
    private final Collection<T> store;
    private final Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());

    public Finder(Class<T> baseClass, Collection<T> store) {
        this.baseClass = baseClass;
        this.store = store;
    }

    public Finder(Class<T> baseClass) {
        this(baseClass, new ArrayList<>());
    }

    public Collection<T> getStore() { return store; }
    public Set<Object> getVisited() { return visited; }

    public void add(Object root) {
        if (root == null || visited.contains(root)) {
            return;
        }

        visited.add(root);

        if (baseClass.isInstance(root)) {
            store.add(baseClass.cast(root));
        }

        if (root instanceof Collection) {
            for (Object obj : (Collection) root) {
                add(obj);
            }
            return;
        }

        if (root instanceof Object[]) {
            for (Object obj : (Object[]) root) {
                add(obj);
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
                add(field.get(root));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException(e); // FIXME
            }
        }
    }
}
