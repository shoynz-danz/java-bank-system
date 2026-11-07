package bank;

// 07.11 этап 7: Decorator (считает, сколько всего было уведомлений)
public class CountingNotificationDecorator implements NotificationService {

    private final NotificationService delegate;
    private int count = 0;

    public CountingNotificationDecorator(NotificationService delegate) {
        this.delegate = delegate;
    }

    @Override
    public void notify(String message) {
        count++;
        delegate.notify(message);
    }

    public int getCount() {
        return count;
    }
}