package bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
03.10 этап 3: репозиторий в памяти.
сначала хранили в ArrayList и искали проходом по списку.

новый этап: перевели на Map, id теперь ключ.
повторный save с тем же id сам заменяет старый через put.
findById возвращает null если ничего нет, как обычный get у мапы.
 */
public class InMemoryRepository<ID, T extends Identifiable<ID>>
        implements Repository<ID, T> {

    private final Map<ID, T> values = new HashMap<>();

    @Override
    public void save(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Нельзя сохранить null");
        }
        // put сам заменит старый объект если id уже есть
        values.put(value.getId(), value);
    }

    @Override
    public T findById(ID id) {
        return values.get(id);
    }

    @Override
    public boolean existsById(ID id) {
        return values.containsKey(id);
    }

    @Override
    public boolean deleteById(ID id) {
        return values.remove(id) != null;
    }

    @Override
    public List<T> findAll() {
        // отдаём копию, чтобы снаружи не поменяли наше хранилище
        return new ArrayList<>(values.values());
    }

    @Override
    public int size() {
        return values.size();
    }
}
