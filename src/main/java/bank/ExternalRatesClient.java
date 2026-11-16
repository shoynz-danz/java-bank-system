package bank;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

// 14.11 этап 8: простой HTTP-клиент с проверкой статуса
public class ExternalRatesClient {

    private final HttpClient client = HttpClient.newHttpClient();

    public String get(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() / 100 != 2) {
            throw new IllegalStateException("HTTP error: " + response.statusCode());
        }

        return response.body();
    }

    private final ObjectMapper mapper = new ObjectMapper();

    // 14.11 этап 9: преобразование HTTP-ответа (JSON) в RateDto
    public RateDto getRate(String url) throws IOException, InterruptedException {
        String body = get(url);
        return mapper.readValue(body, RateDto.class);
    }
}