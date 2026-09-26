package bank;

public record AccountNumber(String value) {
    public AccountNumber {
        if (value == null || !value.matches("\\d{10}")) {
            throw new IllegalArgumentException(
                    "Account number must contain 10 digits"
            );
        }
    }
}