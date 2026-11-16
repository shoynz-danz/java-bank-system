package bank;

import bank.domain.AccountNumber;
import bank.domain.Transaction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// 14.11 этап 3: импорт транзакций из текстового файла
public class TransactionFileImporter {

    public List<Transaction> load(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        List<Transaction> result = new ArrayList<>();

        // начинаем с 1, чтобы пропустить заголовок
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }

            String[] parts = line.split(";");
            if (parts.length != 5) {
                throw new IllegalArgumentException("Некорректная строка: " + line);
            }

            long id = Long.parseLong(parts[0].trim());
            AccountNumber from = new AccountNumber(parts[1].trim());
            TransactionType type = parseType(parts[2].trim());
            double amount = Double.parseDouble(parts[3].trim());
            TransactionStatus status = TransactionStatus.valueOf(parts[4].trim());

            result.add(new Transaction(id, type, from, amount, status));
        }
        return result;
    }

    private TransactionType parseType(String raw) {
        for (TransactionType type : TransactionType.values()) {
            if (type.name().equals(raw)) {
                return type;
            }
        }
        // если в колонке to был номер счёта (как в примере), считаем переводом
        return TransactionType.TRANSFER;
    }
}