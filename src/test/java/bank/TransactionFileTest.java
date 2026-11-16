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

    @Test
    void importLoadsTransactionsAndSkipsHeader(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("transactions.csv");
        Files.write(file, List.of(
                "id;from;to;amount;status",
                "1;1111111111;2222222222;1000.0;SUCCESS",
                "2;3333333333;DEPOSIT;500.0;REJECTED"
        ));

        TransactionFileImporter importer = new TransactionFileImporter();
        List<Transaction> loaded = importer.load(file);

        assertEquals(2, loaded.size());
        assertEquals(1L, loaded.get(0).id());
        assertEquals(new AccountNumber("1111111111"), loaded.get(0).account());
        assertEquals(1000.0, loaded.get(0).amount());
        assertEquals(TransactionStatus.SUCCESS, loaded.get(0).status());

        assertEquals(TransactionType.DEPOSIT, loaded.get(1).type());
        assertEquals(TransactionStatus.REJECTED, loaded.get(1).status());
    }

    @Test
    void importInvalidLineThrowsException(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("broken.csv");
        Files.write(file, List.of(
                "id;from;to;amount;status",
                "1;1111111111;broken"
        ));

        TransactionFileImporter importer = new TransactionFileImporter();
        assertThrows(IllegalArgumentException.class, () -> importer.load(file));
    }
}