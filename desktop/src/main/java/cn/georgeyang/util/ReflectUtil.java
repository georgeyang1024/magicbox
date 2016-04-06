package cn.georgeyang.util;

import android.support.v4.util.LruCache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 反射工具
 * Created by george.yang on 2015/9/18.
 */
public class ReflectUtil {
    private static LruCache cache;
    /**
     * 获取反射的属性的值
     *
     * @param annotation
     * @param annName
     * @return
     */
    public static String getVlaueFromAnnotation(Annotation annotation, String annName) {
        if (cache==null) {
            cache = new LruCache(500);
        }
        String annotationString = annotation.toString();
        String cacheKey = "getVlaueByColumnAnnotation:" + annotationString.hashCode() + "," + annName;
        String ret = (String) cache.get(cacheKey);
        if (ret==null || "".equals(ret)) {
            String pattern = annName + "=(.*?),";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(annotation.toString());
            if (m.find()) {
                ret = m.group();
                ret = ret.substring(annName.length() + 1, ret.length() - 1);
            }
            cache.put(cacheKey,ret);
        }
        return ret;
    }

    public static String getValueByClassAnntation (Class clazz,String annClassName,String key) {
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        return getValueInAnntationList(annotations,annClassName,key);
    }

    public static Field findFiledWithName (Class clazz,String filedName) {
       Field[] fields = getAllFiedFromClassAndSuper(clazz, false);
        if (fields!=null) {
            for (Field field:fields) {
                field.setAccessible(true);
                if (field.getName().equals(filedName)) {
                    return field;
                }
            }
        }
        return null;
    }

    /**
     * 获取泛型实际的类:BaseClass<？> 取到？的类的Type(T)
     * @param clazz
     * @return
     */
    public static Type getTypeFromInterface(Class clazz) {
        if (cache==null) {
            cache = new LruCache(500);
        }
        Type ret = (Type) cache.get(clazz.getName());
        if (ret!=null) {
            return ret;
        }

        //获取全部Interface
        Type[] types = clazz.getGenericInterfaces();
        for (Type type:types) {
            //如果该Interface有<T>
            if (type instanceof ParameterizedType) {
                //获取Interface中<K,V>的第一个K
                Type actualType = ((ParameterizedType)type).getActualTypeArguments()[0];
                ret  = actualType;
                break;
            }
        }
        if (ret==null) {
            ret = String.class;
        }

        cache.put(clazz.getName(),ret);
        return ret;
    }

    public static Field findFiledWithName (Field[] fields,String filedName) {
        if (fields!=null) {
            for (Field field:fields) {
                field.setAccessible(true);
                if (field.getName().equals(filedName)) {
                    return field;
                }
            }
        }
        return null;
    }

    public static void callBooleanMethod (Object object,String methodName,boolean bool) {
        try {
            Method setShowHintMethod = object.getClass().getDeclaredMethod(methodName, boolean.class);
            setShowHintMethod.setAccessible(true);
            setShowHintMethod.invoke(object, new Object[]{bool});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    /**
//     * 按注入類名和 找到注入的
//     * @param fields
//     * @param annClassName
//     * @param key
//     * @return
//     */
//    public static Field findFiledWithAnntationName (Field[] fields,String annClassName,String key) {
//        if (fields!=null) {
//            for (Field field:fields) {
//                field.setAccessible(true);
//                Annotation[] annotations = field.getDeclaredAnnotations();
//
//                if (field.getName().equals(filedName)) {
//                    return field;
//                }
//            }
//        }
//        return null;
//    }


    public static String getValueByFieldAnntation (Field field,String annClassName,String key) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        return getValueInAnntationList(annotations,annClassName,key);
    }

    /**
     * 從一堆字段，按注入類(@Column(name = "test"))、匹配一個字段
     * @param fields
     * @param annClassName class name from "Column"
     * @param key name of "name"
     * @param value name of "test"
     * @return
     */
    public static Field findFieldWithAnntationAndValue (Field[] fields,String annClassName,String key,String value) {
        if (fields==null) {
            return null;
        }
        for (Field field:fields) {
            Annotation[] annotations = field.getDeclaredAnnotations();
            String ret = getValueInAnntationList(annotations,annClassName,key);
            if (ret.equals(value)) {
                return field;
            }
        }
        return null;
    }

    public static Field[] getAllNoStaticFiedFromClassAndSuper (Class clazz) {
        return getAllFiedFromClassAndSuper(clazz,false);
    }

    public static Field[] getAllFiedFromClassAndSuper (Class clazz,boolean needStatic) {
        ArrayList<Field> fields = new ArrayList<>();
        if (clazz!=null) {
            Field[] classFields = clazz.getDeclaredFields();
            if (classFields!=null) {
                for (Field field:classFields) {
                    boolean isStatic = Modifier.isStatic(field.getModifiers());
                    if (isStatic && !needStatic) {
                        continue;
                    }
                    fields.add(field);
                }
            }

            Field[] superFields = getAllFiedFromClassAndSuper(clazz.getSuperclass(), needStatic);
            if (superFields!=null) {
                for (Field field:superFields) {
                    boolean isStatic = Modifier.isStatic(field.getModifiers());
                    if (isStatic && !needStatic) {
                        continue;
                    }
                    fields.add(field);
                }
            }
        }
        return fields.toArray(new Field[fields.size()]);
    }

    public static String getValueInAnntationList (Annotation[] annotations,String annClassName,String key) {
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                String annName = annotation.toString();
                if (annName.indexOf(annClassName) != 0) {
                    return getVlaueFromAnnotation(annotation,key);
                }
            }
        }
        return "";
    }
}
