package app.seed;

import app.exceptions.SeedException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CocktailDBFetcher {

    private static final Logger logger = LoggerFactory.getLogger(CocktailDBFetcher.class);
    private static final String BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/search.php?f=";
    private static final Path RAW_DIR = Path.of("database", "seed", "raw");
    private static final int THREAD_COUNT = 4;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void fetchAll() {
        try {
            Files.createDirectories(RAW_DIR);

            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
            try {
                List<Future<String>> futures = new ArrayList<>();
                for (char letter = 'a'; letter <= 'z'; letter++) {
                    final char current = letter;
                    futures.add(executor.submit(() -> fetchLetter(current)));
                }

                for (Future<String> future : futures) {
                    try {
                        logger.info(future.get());
                    } catch (ExecutionException e) {
                        logger.error("Fetch failed: {}", e.getCause().getMessage(), e.getCause());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        logger.error("Fetching was interrupted");
                        return;
                    }
                }
            } finally {
                executor.shutdown();
            }
        } catch (IOException e) {
            throw new SeedException("Could not create directories " + e);
        }
    }

    private String fetchLetter(char letter) {
        Path file = RAW_DIR.resolve(letter + ".json");
        if (Files.exists(file)) {
            return letter + ": already cached, skipped";
        }
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + letter)).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new SeedException("TheCocktailDB returned HTTP " + response.statusCode()
                        + " for letter " + letter);
            }

            JsonNode drinks = objectMapper.readTree(response.body()).path("drinks");
            int count = drinks.isArray() ? drinks.size() : 0;

            Files.writeString(file, response.body());
            return letter + ": " + count + " drinks saved";

        } catch (IOException e) {
            throw new SeedException("Could not fetch or save letter " + letter, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SeedException("Fetching letter " + letter + " was interrupted", e);
        }
    }
}
