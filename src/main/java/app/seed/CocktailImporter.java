package app.seed;

import app.entities.*;
import app.entities.liqueur.Liqueur;
import app.entities.mixer.Mixer;
import app.entities.spirit.Spirit;
import app.entities.syrup.Syrup;
import app.exceptions.SeedException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CocktailImporter {

    private static final Logger logger = LoggerFactory.getLogger(CocktailImporter.class);
    private static final Path SEED_DIR = Path.of("database/seed");
    private static final Path RAW_DIR = SEED_DIR.resolve("raw");
    private static final String[] TAGS = {"Sweet", "Sour", "Bitter", "Salty", "Spicy"};

    private record Canonical(String name, String type, int[] intensities) {}
    private record Skipped(String name, String reason, String detail) {}

    private final EntityManagerFactory emf;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, String> aliases = new HashMap<>();
    private final Map<String, Canonical> canonicals = new HashMap<>();
    private final List<Skipped> skipped = new ArrayList<>();

    public CocktailImporter(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // limit = max number of drinks to IMPORT (to better test)
    public void run(int limit) {
        loadClassification();
        int imported = 0;
        int alreadyThere = 0;

        try (EntityManager em = emf.createEntityManager()) {
            for (Path file : rawFiles()) {
                JsonNode drinks = readJson(file).path("drinks");
                for (JsonNode drink : drinks) {
                    if (imported >= limit) break;
                    String name = drink.path("strDrink").asText().trim();

                    Optional<String> problem = classifyAndSave(em, drink, name);
                    if (problem.isEmpty()) imported++;
                    else if (problem.get().equals("EXISTS")) alreadyThere++;
                }
            }
        }
        writeSkipped();
        logger.info("Import done: {} imported, {} already existed, {} skipped",
                imported, alreadyThere, skipped.size());
    }

    // ---------- One drink ----------

    // Returns empty if saved; otherwise the skip reason (or "EXISTS")
    private Optional<String> classifyAndSave(EntityManager em, JsonNode drink, String name) {
        Map<String, String> byType = new HashMap<>(); // type -> canonical name
        String garnish = null;
        StringBuilder description = new StringBuilder();

        for (int i = 1; i <= 15; i++) {
            String raw = drink.path("strIngredient" + i).asText("").trim();
            if (raw == null || raw.isBlank()) continue;

            String measure = drink.path("strMeasure" + i).asText("").trim();
            description.append("- ").append(measure.isEmpty() ? "" : measure + " ")
                    .append(raw.trim()).append("\n");

            String alias = aliases.get(normalize(raw));
            Canonical c = (alias == null) ? null : canonicals.get(alias);
            if (c == null) return skip(name, "UNCLASSIFIED_INGREDIENT", raw.trim());

            switch (c.type()) {
                case "IGNORE" -> { }
                case "GARNISH" -> { if (garnish == null) garnish = c.name(); }
                default -> {
                    String existing = byType.putIfAbsent(c.type(), c.name());
                    if (existing != null && !existing.equals(c.name())) {
                        return skip(name, "TOO_MANY_OF_TYPE", c.type() + ": " + existing + " + " + c.name());
                    }
                }
            }
        }
        if (byType.isEmpty()) return skip(name, "NO_INGREDIENTS_AFTER_IGNORE", "");

        em.getTransaction().begin();
        try{
            boolean exists = !em.createQuery("SELECT c.id FROM Cocktail c WHERE c.name = :n", Long.class)
                    .setParameter("n", name).getResultList().isEmpty();
            if (exists) {
                em.getTransaction().rollback();
                return Optional.of("EXISTS");
            }

            Cocktail cocktail = new Cocktail(name, description.toString().trim(),
                    instructions(drink), family(em, drink));

            if (byType.containsKey("SPIRIT"))
                cocktail.setSpirit(findOrCreate(em, Spirit.class, byType.get("SPIRIT"),
                        () -> withTags(new Spirit(byType.get("SPIRIT"), null), em, "SPIRIT")));
            if (byType.containsKey("LIQUEUR"))
                cocktail.setLiqueur(findOrCreate(em, Liqueur.class, byType.get("LIQUEUR"),
                        () -> withTags(new Liqueur(byType.get("LIQUEUR"), null), em, "LIQUEUR")));
            if (byType.containsKey("MIXER"))
                cocktail.setMixer(findOrCreate(em, Mixer.class, byType.get("MIXER"),
                        () -> withTags(new Mixer(byType.get("MIXER"), null), em, "MIXER")));
            if (byType.containsKey("SYRUP"))
                cocktail.setSyrup(findOrCreate(em, Syrup.class, byType.get("SYRUP"),
                        () -> withTags(new Syrup(byType.get("SYRUP"), null), em, "SYRUP")));
            if (garnish != null) {
                String g = garnish;
                cocktail.setGarnish(findOrCreate(em, Garnish.class, g, () -> new Garnish(g, null)));
            }

            em.persist(cocktail);
            em.getTransaction().commit();
            return Optional.empty();

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new SeedException("Failed to save cocktail: " + name, e);
        }
    }

    private Optional<String> skip(String name, String reason, String detail) {
        skipped.add(new Skipped(name, reason, detail));
        return Optional.of(reason);
    }

    private String instructions(JsonNode drink) {
        String text = drink.path("strInstructions").asText("").trim();
        return text.isEmpty() ? "No instructions available." : text;
    }

    private CocktailFamily family(EntityManager em, JsonNode drink) {
        String name = drink.path("strAlcoholic").asText("").equalsIgnoreCase("Non alcoholic")
                ? "Mocktails"
                : drink.path("strCategory").asText("Other / Unknown");
        return findOrCreate(em, CocktailFamily.class, name, () -> new CocktailFamily(name, null));
    }

    // ---------- Helpers for find-or-create ----------

    private <T> T findOrCreate(EntityManager em, Class<T> type, String name, Supplier<T> creator) {
        List<T> found = em.createQuery(
                "SELECT e FROM " + type.getSimpleName() + " e WHERE e.name = :n", type)
                .setParameter("n", name).getResultList();

        if (!found.isEmpty()) return found.get(0);
        T created = creator.get();
        em.persist(created);
        return created;
    }

    // Adds the five flavor tags with the intensities from canonical-ingredients.csv
    private <T> T withTags(T ingredient, EntityManager em, String type) {
        String name = switch (ingredient) {
            case Spirit s -> s.getName();
            case Liqueur l -> l.getName();
            case Mixer m -> m.getName();
            case Syrup s -> s.getName();
            default -> throw new SeedException("Unexpected ingredient class: " + ingredient.getClass());
        };

        int[] values = canonicals.get(name).intensities();
        for (int i = 0; i < TAGS.length; i++) {
            if (values[i] == 0) continue;;
            String tagName = TAGS[i];
            FlavorTag tag = findOrCreate(em, FlavorTag.class, tagName, () -> new FlavorTag(tagName));
            switch (ingredient) {
                case Spirit s -> s.addFlavorTag(tag, values[i]);
                case Liqueur l -> l.addFlavorTag(tag, values[i]);
                case Mixer m -> m.addFlavorTag(tag, values[i]);
                case Syrup s -> s.addFlavorTag(tag, values[i]);
                default -> { }
            }
        }
        return ingredient;
    }

    // ---------- reading / writing files ----------

    private void loadClassification() {
        try {
            for (String line : Files.readAllLines(SEED_DIR.resolve("ingredient-aliases.csv")).stream().skip(1).toList()) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", 2);
                aliases.put(p[0].trim(), p[1].trim());
            }
            for (String line : Files.readAllLines(SEED_DIR.resolve("canonical-ingredients.csv")).stream().skip(1).toList()) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                int[] v = new int[5];
                for (int i = 0; i < 5; i++) {
                    String cell = p[2 + i].trim();
                    v[i] = cell.isEmpty() ? 0 : Integer.parseInt(cell);
                }
                canonicals.put(p[0].trim(), new Canonical(p[0].trim(), p[1].trim(), v));
            }
        } catch (IOException | RuntimeException e) {
            throw new SeedException("Could not read classification files in " + SEED_DIR, e);
        }
        logger.info("Loaded {} aliases and {} canonical ingredients", aliases.size(), canonicals.size());
    }

    private List<Path> rawFiles() {
        try (Stream<Path> files = Files.list(RAW_DIR)) {
            return files.filter(f -> f.toString().endsWith(".json")).sorted().toList();
        } catch (IOException e) {
            throw new SeedException("Could not list " + RAW_DIR, e);
        }
    }

    private JsonNode readJson(Path file) {
        try {
            return mapper.readTree(Files.readString(file));
        } catch (IOException e) {
            throw new SeedException("Could not read " + file, e);
        }
    }

    private void writeSkipped() {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(SEED_DIR.resolve("skipped-cocktails.json").toFile(), skipped);
        } catch (IOException e) {
            throw new SeedException("Could not write skipped-cocktails.json", e);
        }
    }

    private static String normalize(String s) {
        return s.trim().toLowerCase().replaceAll("\\s+", " ");
    }
}
