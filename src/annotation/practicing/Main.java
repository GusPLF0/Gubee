package annotation.practicing;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Person person = new Person("Gustavo", "Lima", "19");
        try {
            checkIfSerializable(person);
            initializeObject(person);
            String jsonString = getJsonString(person);
            System.out.println(jsonString);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private static void checkIfSerializable(Object object) {
        if (Objects.isNull(object)) {
            throw new JsonSerializationException("Object is null!");
        }

        Class<?> myClass = object.getClass();

        if (!myClass.isAnnotationPresent(JsonSerializable.class)) {
            throw new JsonSerializationException("The class "
                    + myClass.getSimpleName()
                    + " is not annotated with JsonSerializable");
        }
    }

    private static void initializeObject(Object object) throws InvocationTargetException, IllegalAccessException {
        Class<?> myClass = object.getClass();

        for (Method method : myClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Init.class)) {
                method.setAccessible(true); // Usado para conseguir invocar o método privado!
                method.invoke(object);
            }
        }

    }

    private static String getJsonString(Object object) throws IllegalAccessException {
            Class<?> myClass = object.getClass();

            Map<String, String> jsonElementsMap = new HashMap<>();

            for (Field field : myClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(JsonElement.class)) {
                    JsonElement annotation = field.getAnnotation(JsonElement.class);

                    String key = annotation.key();
                    if (key.isEmpty()) {
                        jsonElementsMap.put(field.getName(), (String) field.get(object));
                    } else {
                        jsonElementsMap.put(key, (String) field.get(object));
                    }

                }
            }

            String jsonString = jsonElementsMap.entrySet()
                    .stream()
                    .map(entry -> "\"" + entry.getKey() + "\":\""
                            + entry.getValue() + "\"")
                    .collect(Collectors.joining(","));
            return "{" + jsonString + "}";
    }
}
