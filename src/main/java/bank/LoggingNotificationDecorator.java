package bank;

import bank.ports.NotificationService;

// 07.11 этап 7: Decorator (добавляем логирование перед отправкой)
public class LoggingNotificationDecorator implements NotificationService {

    private final NotificationService delegate;

    public LoggingNotificationDecorator(NotificationService delegate) {
        this.delegate = delegate;
    }

    @Override
    public void notify(String message) {
        System.out.println("[LOG] " + message);
        delegate.notify(message);
    }
}