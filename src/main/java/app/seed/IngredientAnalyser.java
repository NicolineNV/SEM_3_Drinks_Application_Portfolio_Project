package app.seed;

import app.exceptions.SeedException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;

public class IngredientAnalyser {

    private static final Logger logger = LoggerFactory.getLogger(IngredientAnalyser.class);
    private static final Path RAW_DIR = Path.of("database", "seed", "raw");
    private static final Path OUTPUT = Path.of("database", "seed", "ingredient-candidates.csv");

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void analyze() {
        try {
            Map<String, Integer> counts = new HashMap<>();
            Set<String> seenDrinkIds = new HashSet<>();

            try (DirectoryStream<Path> files = Files.newDirectoryStream(RAW_DIR, "*.json")) {
                for (Path file : files) {
                    JsonNode drinks = objectMapper.readTree(file.toFile()).path("drinks");
                    if (!drinks.isArray()) {
                        continue;
                    }
                    for (JsonNode drink : drinks) {
                        if (!seenDrinkIds.add(drink.path("idDrink").asText())) {
                            continue; // already counted this drink
                        }
                        for (int i = 1; i <= 15; i++) {
                            JsonNode node = drink.path("strIngredient" + i);
                            if (!node.isTextual()) {
                                continue; // null or missing
                            }
                            String name = normalize(node.asText());
                            if (!name.isEmpty()) {
                                counts.merge(name, 1, Integer::sum);
                            }
                        }
                    }
                }
            }

            writeCsv(counts);
            logger.info("Analyzed {} drinks, found {} distinct ingredients. Written to {}",
                    seenDrinkIds.size(), counts.size(), OUTPUT);

        } catch (NoSuchFileException e) {
            throw new SeedException("Raw directory not found. Run FetchRawCocktails first.", e);
        } catch (IOException e) {
            throw new SeedException("Could not read or write seed files", e);
        }
    }

    private void writeCsv(Map<String, Integer> counts) throws IOException {
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(counts.entrySet());
        sorted.sort((a, b) -> {
            int byCount = b.getValue() - a.getValue();
            return byCount != 0 ? byCount : a.getKey().compareTo(b.getKey());
        });

        List<String> lines = new ArrayList<>();
        lines.add("ingredient,count");
        for (Map.Entry<String, Integer> entry : sorted) {
            lines.add("\"" + entry.getKey().replace("\"", "\"\"") + "\"," + entry.getValue());
        }
        Files.write(OUTPUT, lines);
    }

    private String normalize(String raw) {
        return raw.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    public static void main(String[] args) {
        new IngredientAnalyser().analyze();
    }
}
