package bank;

public record AccountSummary(
        AccountNumber number,
        String type,
        double balance
) {
}