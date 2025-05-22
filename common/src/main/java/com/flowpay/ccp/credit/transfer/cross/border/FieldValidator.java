package com.flowpay.ccp.credit.transfer.cross.border;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FieldValidator {

    private static final Logger LOG = Logger.getLogger(FieldValidator.class);

    private final List<String> fields;

    public FieldValidator(String fields) {
        this.fields = Arrays.asList(fields.split(","));
    }

    private static Boolean isValid(Class<?> clazz, Object object, List<String> fields)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException,
            NoSuchFieldException {

        if (fields.isEmpty()) {
            return true;
        }


        var current = fields.get(0);
        var remainingFields = fields.subList(1, fields.size());
        LOG.debug("Validating object: " + object);
        LOG.debug("Current field: " + current);

        try {
            if (clazz.isRecord()) {
                var method = clazz.getMethod(current);
                var currentObject = method.invoke(object);
                return isValid(method.getReturnType(), currentObject, remainingFields);
            } else {
                var classField = clazz.getField(current);
                var currentObject = classField.get(object);
                return isValid(classField.getType(), currentObject, remainingFields);
            }
        } catch (Exception e) {
            if (clazz.isRecord()) {
                for (var field : clazz.getFields()) {
                    if (field.isAnnotationPresent(JsonUnwrapped.class)) {
                        var method = clazz.getMethod(field.getName());
                        var unwrapped = method.invoke(object);
                        try {
                            return isValid(method.getReturnType(), unwrapped, fields);
                        } catch (Exception ex) {
                            LOG.debug("Field not found in unwrapped object: " + unwrapped, ex);
                        }
                    }
                }
            } else {
                for (var field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(JsonUnwrapped.class)) {
                        var unwrapped = field.get(object);
                        try {
                            return isValid(field.getType(), unwrapped, fields);
                        } catch (Exception ex) {
                            LOG.debug("Field not found in unwrapped object: " + unwrapped, ex);
                        }
                    }
                }
            }
            throw e;
        }
    }

    public <T> Boolean isValid(T object) {
        try {
            LOG.debug("Validating object: " + object);
            LOG.debug("Required fields: " + fields);
            if (object == null) {
                LOG.debug("Object is null");
                return false;
            }
            for (var field : fields) {
                if (Boolean.FALSE.equals(isValid(object.getClass(), object,
                        new ArrayList<>(Arrays.stream(field.split("\\.")).toList())))) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            LOG.debug("Error validating object: " + object, e);
            return false;
        }
    }
}
