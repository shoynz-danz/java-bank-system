package bank;

import java.lang.reflect.Method;

// 14.11 этап 11: инспектор аннотаций через Reflection
public class AnnotationInspector {

    public void inspect(Class<?> type) {
        System.out.println("Класс: " + type.getName());
        System.out.println("@Audited: " + type.isAnnotationPresent(Audited.class));

        System.out.println("Declared methods:");
        for (Method method : type.getDeclaredMethods()) {
            System.out.println("  - " + method.getName());
        }

        System.out.println("Методы с @RequiresRole:");
        for (Method method : type.getDeclaredMethods()) {
            RequiresRole role = method.getAnnotation(RequiresRole.class);
            if (role != null) {
                System.out.println("  " + method.getName() + " -> " + role.value());
            }
        }
    }
}