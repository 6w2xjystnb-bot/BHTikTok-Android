package com.bhtiktok.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;
import de.robv.android.xposed.XposedHelpers;

public class ClassFinder {

    public static Class<?> findClassByMethod(ClassLoader cl, String packagePrefix, String methodName, Class<?>... paramTypes) {
        try {
            Object pathList = XposedHelpers.getObjectField(cl, "pathList");
            Object[] dexElements = (Object[]) XposedHelpers.getObjectField(pathList, "dexElements");
            for (Object element : dexElements) {
                DexFile dexFile = (DexFile) XposedHelpers.getObjectField(element, "dexFile");
                if (dexFile == null) continue;
                Enumeration<String> entries = dexFile.entries();
                while (entries.hasMoreElements()) {
                    String className = entries.nextElement();
                    if (!className.startsWith(packagePrefix)) continue;
                    try {
                        Class<?> cls = Class.forName(className, false, cl);
                        Method m = cls.getDeclaredMethod(methodName, paramTypes);
                        if (m != null) {
                            return cls;
                        }
                    } catch (Throwable t) { }
                }
            }
        } catch (Throwable t) { }
        return null;
    }

    public static List<Class<?>> findClassesByMethod(ClassLoader cl, String packagePrefix, String methodName, Class<?>... paramTypes) {
        List<Class<?>> result = new ArrayList<>();
        try {
            Object pathList = XposedHelpers.getObjectField(cl, "pathList");
            Object[] dexElements = (Object[]) XposedHelpers.getObjectField(pathList, "dexElements");
            for (Object element : dexElements) {
                DexFile dexFile = (DexFile) XposedHelpers.getObjectField(element, "dexFile");
                if (dexFile == null) continue;
                Enumeration<String> entries = dexFile.entries();
                while (entries.hasMoreElements()) {
                    String className = entries.nextElement();
                    if (!className.startsWith(packagePrefix)) continue;
                    try {
                        Class<?> cls = Class.forName(className, false, cl);
                        Method m = cls.getDeclaredMethod(methodName, paramTypes);
                        if (m != null) {
                            result.add(cls);
                        }
                    } catch (Throwable t) { }
                }
            }
        } catch (Throwable t) { }
        return result;
    }

    public static Class<?> findClassByField(ClassLoader cl, String packagePrefix, String fieldName, String fieldType) {
        try {
            Object pathList = XposedHelpers.getObjectField(cl, "pathList");
            Object[] dexElements = (Object[]) XposedHelpers.getObjectField(pathList, "dexElements");
            for (Object element : dexElements) {
                DexFile dexFile = (DexFile) XposedHelpers.getObjectField(element, "dexFile");
                if (dexFile == null) continue;
                Enumeration<String> entries = dexFile.entries();
                while (entries.hasMoreElements()) {
                    String className = entries.nextElement();
                    if (!className.startsWith(packagePrefix)) continue;
                    try {
                        Class<?> cls = Class.forName(className, false, cl);
                        cls.getDeclaredField(fieldName);
                        return cls;
                    } catch (Throwable t) { }
                }
            }
        } catch (Throwable t) { }
        return null;
    }
}
