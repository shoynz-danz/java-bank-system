package bank;

/*
10.10 этап 4: заявка на перевод.
просто данные, кто кому и сколько, без логики.
 */
public record TransferRequest(
        AccountNumber from,
        AccountNumber to,
        double amount) {
}
