package bank;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonStorageTest {

    @Test
    void saveAccountsWritesJsonFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("accounts.json");
        JsonStorage storage = new JsonStorage();

        List<AccountDto> accounts = List.of(
                new AccountDto("0000000001", "Ivan", 1000.0, "DebitAccount")
        );

        storage.saveAccounts(file, accounts);

        assertTrue(Files.exists(file));
        String json = Files.readString(file);
        assertTrue(json.contains("0000000001"));
        assertTrue(json.contains("Ivan"));
        assertTrue(json.contains("DebitAccount"));
    }

    // 14.11 этап 7: round-trip и проверка ошибок

    @Test
    void roundTripEmptyList(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("empty.json");
        JsonStorage storage = new JsonStorage();
        List<AccountDto> source = List.of();

        storage.saveAccounts(file, source);
        List<AccountDto> restored = storage.loadAccounts(file);

        assertEquals(source, restored);
    }

    @Test
    void roundTripMultipleAccountTypes(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("accounts.json");
        JsonStorage storage = new JsonStorage();
        List<AccountDto> source = List.of(
                new AccountDto("0000000001", "Ivan", 1000.0, "DebitAccount"),
                new AccountDto("0000000002", "Petr", 5000.0, "SavingsAccount"),
                new AccountDto("0000000003", "Anna", -500.0, "CreditAccount")
        );

        storage.saveAccounts(file, source);
        List<AccountDto> restored = storage.loadAccounts(file);

        assertEquals(source, restored);
    }

    @Test
    void loadInvalidJsonThrowsException(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("broken.json");
        Files.writeString(file, "{broken json");
        JsonStorage storage = new JsonStorage();

        assertThrows(IOException.class, () -> storage.loadAccounts(file));
    }

    @Test
    void loadMissingFileThrowsException(@TempDir Path tempDir) {
        Path missing = tempDir.resolve("no-such-file.json");
        JsonStorage storage = new JsonStorage();

        assertThrows(IOException.class, () -> storage.loadAccounts(missing));
    }
}