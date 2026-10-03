package bank;

/*
03.10 GENERICS
Box, но с типом. Теперь класс не хранит просто Object,
а что-то конкретное: String, Integer, номер счёта и т.д.

За счёт этого компилятор сам проверяет, что мы туда положили,
и get() отдаёт нужный тип без приведения.
 */
public class BoxGeneric<T> {

    private T value;

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}