package bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// 14.11 этап 5: сохранение счетов (DTO) в JSON через Jackson
public class JsonStorage {

    private final ObjectMapper mapper = new ObjectMapper();

    public void saveAccounts(Path path, List<AccountDto> accounts) throws IOException {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(path.toFile(), accounts);
    }

    // 14.11 этап 6: чтение счетов из JSON
    public List<AccountDto> loadAccounts(Path path) throws IOException {
        return mapper.readValue(
                path.toFile(),
                new TypeReference<List<AccountDto>>() {}
        );
    }
}