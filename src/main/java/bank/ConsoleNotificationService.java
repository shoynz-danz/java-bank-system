package bank;

import bank.ports.NotificationService;

public class ConsoleNotificationService implements NotificationService {
    @Override
    public void notify(String message) {
        System.out.println(message);
    }
}