package com.jsql.view.swing.dialog.translate;

import java.util.*;
import java.util.stream.Collectors;

public class SortedProperties extends Properties {
    @Override
    public synchronized Enumeration<Object> keys() {
        Enumeration<Object> keysEnum = super.keys();
        List<String> keyList = new ArrayList<>();
        while (keysEnum.hasMoreElements()) {
            keyList.add((String) keysEnum.nextElement());
        }
        Collections.sort(keyList);
        return Collections.enumeration(
            keyList.stream().map(s -> (Object) s).toList()
        );
    }
    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        Map<Object, Object> treeMap = new TreeMap<>();
        Set<Map.Entry<Object, Object>> entrySet = super.entrySet();
        for (Map.Entry<Object, Object> entry : entrySet) {
            treeMap.put(entry.getKey(), entry.getValue());
        }
        return Collections.synchronizedSet(treeMap.entrySet());
    }
}