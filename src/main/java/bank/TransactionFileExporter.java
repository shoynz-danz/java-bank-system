package bank;

import bank.domain.Transaction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// 14.11 этап 2: экспорт транзакций в файл формата id;from;to;amount;status
public class TransactionFileExporter {

    public void export(Path path, List<Transaction> transactions) throws IOException {
        // создаём папки, если путь вложенный
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        List<String> lines = new ArrayList<>();
        lines.add("id;from;to;amount;status");

        List<String> dataLines = transactions.stream()
                .map(TransactionDto::fromDomain)
                .map(dto -> dto.id() + ";" + dto.from() + ";" + dto.to() + ";" + dto.amount() + ";" + dto.status())
                .toList();
        lines.addAll(dataLines);

        Files.write(path, lines);
    }
}