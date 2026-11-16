package bank;

import bank.domain.AccountNumber;
import bank.domain.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionFileTest {

    @Test
    void exportCreatesFileWithHeaderAndLines(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("nested/transactions.csv");
        TransactionFileExporter exporter = new TransactionFileExporter();

        List<Transaction> transactions = List.of(
                new Transaction(1L, TransactionType.TRANSFER, new AccountNumber("1111111111"), 1000.0, TransactionStatus.SUCCESS)
        );

        exporter.export(file, transactions);

        assertTrue(Files.exists(file));
        List<String> lines = Files.readAllLines(file);
        assertEquals(2, lines.size());
        assertEquals("id;from;to;amount;status", lines.get(0));
        assertEquals("1;1111111111;TRANSFER;1000.0;SUCCESS", lines.get(1));
    }
}