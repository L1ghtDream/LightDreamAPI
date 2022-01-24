package dev.lightdream.api.managers;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.HashMap;

public class KeyDeserializerManager extends KeyDeserializer {

    private final HashMap<String, Class<?>> clazzes;

    public KeyDeserializerManager(HashMap<String, Class<?>> clazzes) {
        this.clazzes = clazzes;
    }

    @SneakyThrows
    @Override
    public Object deserializeKey(String key, DeserializationContext context) {
        String className = key.split("\\{")[0];

        if (!clazzes.containsKey(className)) {
            throw new InvalidParameterException("Class '" + className + "' has not been initialized for key deserialization");
        }

        Class<?> clazz = clazzes.get(className);

        for (Method method : clazz.getMethods()) {
            if (method.getName().equals("deserialize")) {
                return method.invoke(clazz.newInstance(), key);
            }
        }

        throw new InvalidParameterException("Class '" + className + "' does not ave any method deserialize!");
    }
}


