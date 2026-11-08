package bank.ports;

@FunctionalInterface
public interface NotificationService {
    void notify(String message);
}