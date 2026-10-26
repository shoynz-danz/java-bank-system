package bank;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.function.Consumer;

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

    // 24.10 этап 6: преобразование объектов без Stream API
    public static <T, R> List<R> map(
            List<T> source,
            Function<? super T, ? extends R> mapper) {
        List<R> result = new ArrayList<>();
        for (T value : source) {
            result.add(mapper.apply(value));
        }
        return result;
    }

    // 24.10 этап 7: действие над каждым элементом через Consumer
    public static <T> void forEach(
            List<T> source,
            Consumer<? super T> action) {
        for (T value : source) {
            action.accept(value);
        }
    }
}


