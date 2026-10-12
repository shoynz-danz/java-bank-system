package bank;

/*
03.10 этап 2: общий контракт репозитория.
ID - тип ключа, T - сама сущность.
T extends Identifiable<ID> значит что у T точно есть getId(),
а то репозиторий не знал бы как достать id.
 */
public interface Repository<ID, T extends Identifiable<ID>> {
    void save(T value);

    T findById(ID id);

    boolean existsById(ID id);

    int size();
}
