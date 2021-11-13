package dev.lightdream.api.dto;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.SuperBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Data
@SuperBuilder
public abstract class Serializable {

    private static HashMap<Class<?>, Method> baseParseMethods = null;
    private static HashMap<Class<?>, Object> instances = null;

    public Serializable() {
        if (Serializable.baseParseMethods != null) {
            return;
        }

        HashMap<Class<?>, Method> baseParseMethods = new HashMap<>();
        HashMap<Class<?>, Object> instances = new HashMap<>();

        for (Method method : Double.class.getMethods()) {
            if (method.getName().equals("parseDouble")) {
                baseParseMethods.put(Double.class, method);
                instances.put(Double.class, 0.0);
                break;
            }
        }

        for (Method method : Integer.class.getMethods()) {
            if (method.getName().equals("parseInt")) {
                baseParseMethods.put(Integer.class, method);
                instances.put(Integer.class, 1);
                break;
            }
        }

        for (Method method : Long.class.getMethods()) {
            if (method.getName().equals("parseLong")) {
                baseParseMethods.put(Long.class, method);
                instances.put(Long.class, 1L);
                break;
            }
        }

        for (Method method : Boolean.class.getMethods()) {
            if (method.getName().equals("valueOf")) {
                baseParseMethods.put(Boolean.class, method);
                instances.put(Boolean.class, false);
                break;
            }
        }

        for (Method method : String.class.getMethods()) {
            if (method.getName().equals("valueOf")) {
                baseParseMethods.put(String.class, method);
                instances.put(String.class, "");
                break;
            }
        }

        Serializable.baseParseMethods = baseParseMethods;
        Serializable.instances = instances;
    }

    public abstract String toString();

    @SuppressWarnings("unused")
    @SneakyThrows
    public Object deserialize(String serialized) {
        serialized = serialized.replace(getClass().getSimpleName(), "");
        serialized = "." + serialized + ".";
        serialized = serialized.replace(".{", "");
        serialized = serialized.replace("}.", "");

        String[] params = serialized.split(", ");
        HashMap<String, String> parameters = new HashMap<>();

        for (String param : params) {
            List<String> split = Arrays.asList(param.split("="));
            if (split.size() != 2) {
                continue;
            }
            parameters.put(split.get(0), split.get(1));
        }

        Object obj = this.getClass().getConstructor().newInstance();

        for (Field field : obj.getClass().getFields()) {
            if (!parameters.containsKey(field.getName())) {
                continue;
            }
            String parameter = parameters.get(field.getName());
            Object parsed = null;
            if (Serializable.baseParseMethods.containsKey(field.getType())) {
                parsed = Serializable.baseParseMethods.get(field.getType()).invoke(instances.get(field.getType()), parameter);
            } else {
                for (Method method : field.getType().getMethods()) {
                    if (method.getName().equals("deserialize")) {
                        parsed = method.invoke(field.getType().newInstance(), parameter);
                    }
                }
            }
            field.set(obj, parsed);
        }

        return obj;
    }
}
