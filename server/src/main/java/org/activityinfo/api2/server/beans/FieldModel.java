package org.activityinfo.api2.server.beans;

/**
 * Internal model of an instance field
 */
public class FieldModel {

    private final String fieldName;
    private final String getterMethodName;
    private final String setterMethodName;
    private final String fieldIdConstantName;

    public FieldModel(String getterMethodName) {
        this.getterMethodName = getterMethodName;
        this.setterMethodName = "set" + isGetter(getterMethodName);
        this.fieldName = lowerCase(isGetter(getterMethodName));
        this.fieldIdConstantName = constantCase(fieldName);
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getGetterMethodName() {
        return getterMethodName;
    }

    public String getSetterMethodName() {
        return setterMethodName;
    }

    public String getFieldIdConstantName() {
        return fieldIdConstantName;
    }

    public static String isGetter(String methodName) {
        String fieldName = "";
        if(methodName.startsWith("get")) {
            fieldName = methodName.substring("get".length());
        } else if(methodName.startsWith("is")) {
            fieldName = methodName.substring("is".length());
        }
        if(fieldName.length() > 0 && Character.isLowerCase(fieldName.charAt(0))) {
            return fieldName;
        } else {
            return null;
        }
    }

    private static String constantCase(String fieldName) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i!= fieldName.length();++i) {
            char ch = fieldName.charAt(i);
            if(i > 0) {
                char lastChar = fieldName.charAt(i-1);
                if(Character.isLowerCase(ch) != Character.isLowerCase(lastChar)) {
                    sb.append('_');
                }
            }
            sb.append(Character.toUpperCase(fieldName.charAt(i)));
        }
        return sb.toString();
    }


    private static String lowerCase(String fieldName) {
        return Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

}
