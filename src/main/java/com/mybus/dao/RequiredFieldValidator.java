package com.mybus.dao;

import com.mybus.annotations.RequiresValue;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by srinikandula on 12/10/16.
 */
public class RequiredFieldValidator {
    public static List<String> validateModel(Object data, Class className) {
        List<String> errors = new ArrayList<>();
        Map<String, PropertyDescriptor> pdCache = new HashMap<>();
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(className).getPropertyDescriptors()) {
                if (pd.getReadMethod() != null && !"class".equals(pd.getName()))
                    pdCache.put(pd.getName(), pd);
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        for(Field field : className.getDeclaredFields()){
            String name = field.getName();
            Annotation annotation = field.getAnnotation(RequiresValue.class);
            if(annotation != null) {
                try {
                    if(pdCache.get(name).getReadMethod().invoke(data) == null){
                        errors.add(String.format("%s is required", name));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return errors;
    }
}
