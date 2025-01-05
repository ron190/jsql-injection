package com.jsql.view.swing.dialog.translate;

import java.util.*;

public class SortedProperties extends Properties {
    @Override
    public synchronized Enumeration keys() {
        Enumeration<Object> keysEnum = super.keys();
        Vector<String> keyList = new Vector<>();
        while (keysEnum.hasMoreElements()) {
            keyList.add((String) keysEnum.nextElement());
        }
        Collections.sort(keyList);
        return keyList.elements();
    }
    @Override
    public Set<java.util.Map.Entry<Object, Object>> entrySet() {
        TreeMap<Object, Object> treeMap = new TreeMap<>();
        Set<Map.Entry<Object, Object>> entrySet = super.entrySet();
        for (Map.Entry<Object, Object> entry : entrySet) {
            treeMap.put(entry.getKey(), entry.getValue());
        }
        return Collections.synchronizedSet(treeMap.entrySet());
    }
}