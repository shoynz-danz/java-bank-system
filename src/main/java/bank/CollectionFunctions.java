package bank;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/*
24.10 этап 3: универсальная фильтрация без Stream API.
просто проходим циклом и кидаем подходящие в новый список.
*/
public final class CollectionFunctions {

    private CollectionFunctions() {
        // утилитный класс, не создаём объекты
    }

    public static <T> List<T> filter(
            List<T> source,
            Predicate<? super T> condition) {
        List<T> result = new ArrayList<>();
        for (T value : source) {
            if (condition.test(value)) {
                result.add(value);
            }
        }
        return result;
    }
}