package com.mcal.uidesigner;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class PropertyObject {
    private final Map<Class<?>, Object> setterObjects = new HashMap<>();
    public Object obj;

    public PropertyObject(Object obj) {
        this.obj = obj;
    }

    public void setProperty(XmlLayoutProperties.PropertySpec property, Object value) {
        if (value != null) {
            try {
                Object setterObj = this.obj;
                if (property.setterProxyClass != null && (setterObj = this.setterObjects.get(property.setterProxyClass)) == null) {
                    setterObj = property.setterProxyClass.getConstructor(Object.class).newInstance(this.obj);
                    this.setterObjects.put(property.setterProxyClass, setterObj);
                }
                if (property.setterName.endsWith("()")) {
                    Class<?> valueType = property.type.valueType;
                    if (valueType == Enum.class) {
                        valueType = property.constantClass;
                    }
                    setterObj.getClass().getMethod(property.setterName.substring(0, property.setterName.length() - 2), valueType).invoke(setterObj, value);
                    return;
                }
                setterObj.getClass().getField(property.setterName).set(setterObj, value);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    public boolean hasProperty(@NonNull XmlLayoutProperties.PropertySpec property) {
        return property.targetClass != null && property.targetClass.isInstance(this.obj);
    }
}
