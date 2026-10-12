package bank;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
03.10 этап 3: репозиторий в памяти.
храним всё в обычном ArrayList.
по заданию выбрал: повторный save с тем же id заменяет старый объект.
findById кидает EntityNotFoundException если ничего нет.
 */
public class InMemoryRepository<ID, T extends Identifiable<ID>>
        implements Repository<ID, T> {

    private final List<T> values = new ArrayList<>();

    @Override
    public void save(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Нельзя сохранить null");
        }
        // ищем старый объект с таким же id
        for (int i = 0; i < values.size(); i++) {
            if (Objects.equals(values.get(i).getId(), value.getId())) {
                values.set(i, value); // нашли такой же id, заменяем
                return;
            }
        }
        // такого id ещё не было, просто добавляем
        values.add(value);
    }

    @Override
    public T findById(ID id) {
        // идём по списку и сравниваем id
        for (T value : values) {
            if (Objects.equals(value.getId(), id)) {
                return value;
            }
        }
        throw new EntityNotFoundException("Не нашли сущность с id: " + id);
    }

    @Override
    public boolean existsById(ID id) {
        for (T value : values) {
            if (Objects.equals(value.getId(), id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return values.size();
    }
}
