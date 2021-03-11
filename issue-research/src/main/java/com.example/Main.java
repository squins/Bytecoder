package com.example;

import com.badlogic.gdx.utils.compression.lzma.Base;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static class BaseClass {
        public static String staticField = "staticFieldValue";
        public static boolean booleanField = true;
        public static byte byteField = (byte) 10;
        public static char charField = 'c';
        public static double doubleField = 1d;
        public static float floatField = 1f;
        public static long longField = 1L;
        public static short shortField = (short) 1;
        public static int intField = 1;
    }

    public static class ReflectionTarget extends BaseClass  {
        public String objectField = "objectFieldValue";
    }

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, ReflectionException {
        MyClassWithField myClassWithField = new MyClassWithField();
        BaseClass baseClass = new BaseClass();
        BaseClass.staticField = "staticFieldValue";

        Object clazz = Integer.class;

        System.out.println("This line will crash:");
        System.out.println("Clazz instanceof: " + (clazz instanceof String));

        System.out.println("This line will crash too");
        System.out.println("Clazz hashcode " + clazz.hashCode());

        System.out.println("Calling BaseClass.class.getField()");

        final java.lang.reflect.Field f = BaseClass.class.getField("staticField");

        System.out.println(f.getName());

        System.out.println(f.getType() != null);

        System.out.println("Calling MyClassWithField.class.getField");

        java.lang.reflect.Field deprecatedField = MyClassWithField.class.getField("deprecatedField");
        System.out.println("getField works");
        System.out.println("Deprecated: " + deprecatedField.isAnnotationPresent(Deprecated.class));

        System.out.println("Calling jvm getField");

        java.lang.reflect.Field myStringListField = MyClassWithField.class.getField("myStringList");
//        System.out.println("getGenericType: " + myStringListField.getGenericType());

        System.out.println("Calling libgdx getField");

        Field gdxStringListField = ClassReflection.getField(MyClassWithField.class, "myStringList");
        System.out.println("Got gdx field, name: " + gdxStringListField.getName() + "type: " + gdxStringListField.getType().getName());
        System.out.println("Type: "  + gdxStringListField.getType());
//        System.out.println("ElementType: " + gdxStringListField.getElementType(0));

        System.out.println("Getting declared fields");
        Field[] declaredFields = ClassReflection.getDeclaredFields(ArrayList.class);
        System.out.println(Arrays.toString(declaredFields));

        MyClassWithField myObject = myClassWithField;

        java.lang.reflect.Field myField = MyClassWithField.class.getField("myField");
        myField.set(myObject, "myValue");
        System.out.println("Value: " + myField.get(myObject));


        System.out.println(Integer.class.getSuperclass());

        System.out.println("Should be static (expected: true): " + Modifier.isStatic(MyClassWithField.class.getField("staticField").getModifiers()));
        System.out.println("isSynthetic (expected: false): "  + myField.isSynthetic());
        System.out.println("isTransient (expected: true): " + Modifier.isTransient(MyClassWithField.class.getField("transientField").getModifiers()));
        System.out.println("isArray (expected: true): " + new String[0].getClass().isArray());
        System.out.println("isArray (expected: false): " + new String().getClass().isArray());
        System.out.println("isPrimitive (expected: true) " + int.class.isPrimitive());
        System.out.println("isPrimitive (expected: false) " + Integer.class.isPrimitive());
        System.out.println("isPrimitive (expected: false) " + ArrayList.class.isPrimitive());

        String[] strings = {"hi"};

        System.out.println("ComponentType: " + strings.getClass().getComponentType());

    }
}
class MyClassWithField {
    public String myField;
    public List<String> myStringList = new ArrayList<>();
    public static boolean staticField;
    public transient boolean transientField;

    @Deprecated
    public boolean deprecatedField;
}