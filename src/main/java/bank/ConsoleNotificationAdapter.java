package bank;

// 07.11 этап 5: Adapter (адаптер для порта NotificationService)
public class ConsoleNotificationAdapter implements NotificationService {

    @Override
    public void notify(String message) {
        System.out.println(message);
    }
}