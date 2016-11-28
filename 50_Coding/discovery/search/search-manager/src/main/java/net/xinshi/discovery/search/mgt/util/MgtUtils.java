package net.xinshi.discovery.search.mgt.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MgtUtils {
    public MgtUtils() {
    }

    public static Map<String, List<String>> parsePostBody(String qry) throws UnsupportedEncodingException {
        // parse the query
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        String defs[] = qry.split("[&]");
        for (String def : defs) {
            int ix = def.indexOf('=');
            String name;
            String value;
            if (ix < 0) {
                name = def;
                value = "";
            } else {
                name = def.substring(0, ix);
                value = URLDecoder.decode(def.substring(ix + 1), "UTF-8");
            }
            List<String> list = params.get(name);
            if (list == null) {
                list = new ArrayList<String>();
                params.put(name, list);
            }
            list.add(value);
        }
        return params;
    }

    public static void fillValues(Object obj, Map<String, List<String>> params) {
        if (obj != null) {
            Class cls = obj.getClass();
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                fillValue(obj, field, "", params);
            }
        }
    }

    private static void fillValue(Object obj, Field field, String parent, Map<String, List<String>> params) {
        if (field.getType().isPrimitive() || field.getType().getSimpleName().equals("String")) {
            String key = "";
            if (parent!= null && !"".equals(parent.trim())) {
                 key = parent +field.getName().toLowerCase();

            } else {
                key =field.getName().toLowerCase();
            }
            try {
                String v = null;
                List<String> values = params.get(key);
                if (values != null && values.size() > 0) {
                    v = values.get(0);
                }

                if (v != null) {
                    field.setAccessible(true);
                    if (field.getType().getSimpleName().equals("String")) {
                        field.set(obj, v);
                    } else if(field.getType().isPrimitive()) {
                        field.set(obj, Integer.valueOf(v));
                    }
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            field.setAccessible(true);
            try {
                Object v = field.get(obj);
                if (v != null) {
                    for(Field f : v.getClass().getDeclaredFields()) {
                        String name = v.getClass().getSimpleName().toLowerCase();
                        if (name.endsWith("bean")) {
                            name = name.substring(0,name.length() - 4);
                        }
                        fillValue(v, f, parent + name + ".", params);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}