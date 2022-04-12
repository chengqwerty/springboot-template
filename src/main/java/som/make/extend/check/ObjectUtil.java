package som.make.extend.check;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ObjectUtil {

    /**
     * 获取一个对象的一级全部属性
     * @param obj 要获取属性的对象
     * @param fields 要放入属性的数组
     * @return
     */
    public static Field[] getSupperClassProperties(Object obj, Field[] fields) {
        for (Class<?> classTemp = obj.getClass(); classTemp != Object.class; classTemp = classTemp.getSuperclass()) {
            try {
                Field[] fieldsTemp = classTemp.getDeclaredFields();
                fields = Arrays.copyOf(fields, fields.length + fieldsTemp.length);
                System.arraycopy(fieldsTemp, 0, fields, fields.length - fieldsTemp.length, fieldsTemp.length);
            } catch (Exception e) {
                //我們只是這樣寫而已
            }
        }
        return fields;
    }

}
