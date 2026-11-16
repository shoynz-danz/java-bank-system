package bank;

// 14.11 этап 9: DTO для ответа с курсом валюты
public record RateDto(
        String currency,
        double value
) {
}