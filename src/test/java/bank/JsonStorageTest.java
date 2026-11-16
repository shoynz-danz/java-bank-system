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
}