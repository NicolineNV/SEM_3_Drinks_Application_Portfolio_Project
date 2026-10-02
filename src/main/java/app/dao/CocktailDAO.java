package app.dao;

import app.entities.*;
import jakarta.persistence.EntityManager;

import java.util.*;

public class CocktailDAO extends AbstractDAO<Cocktail, Long> {

    /**
     * getAll() method in AbstractDAO does not use JOIN FETCH.
     * It simply retrieves Cocktail rows without their relationships.
     * Because of this a LazyInitializationException will be triggered if not handled.
     * To avoid this, JOIN FETCH needs to be added to JPQL String.
     * Also, to make the code easier to read, a static final String was made,
     * to avoid writing the same 6 lines multiple times.
     */
    private static final String COCKTAIL_WITH_RELATIONS =
            "SELECT c FROM Cocktail c " +
            "LEFT JOIN FETCH c.cocktailFamily " + // LEFT JOIN FETCH because can be null
            "LEFT JOIN FETCH c.spirit " +
            "LEFT JOIN FETCH c.liqueur " +
            "LEFT JOIN FETCH c.mixer " +
            "LEFT JOIN FETCH c.syrup " +
            "LEFT JOIN FETCH c.garnish ";

    public CocktailDAO(){
        super(Cocktail.class);
    }

    public Optional<Cocktail> getByIdWithDetails(Long id) {
        try (EntityManager em = emf.createEntityManager()) {

            String jpql = COCKTAIL_WITH_RELATIONS + "WHERE c.id = :id";

            return em.createQuery(jpql, Cocktail.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst();
        }
    }



    public List<Cocktail> getAllWithDetails() {
        try (EntityManager em = emf.createEntityManager()) {

            return em.createQuery(COCKTAIL_WITH_RELATIONS, Cocktail.class).getResultList();
        }
    }



    public List<CocktailCandidate> getCandidatesForQuiz(Long familyId, Long spiritId) {
        try (EntityManager em = emf.createEntityManager()) {

            StringBuilder jpql = new StringBuilder(COCKTAIL_WITH_RELATIONS) // StringBuilder because there is a conditional structure
                    .append("WHERE c.cocktailFamily.id = :familyId");

            if (spiritId != null) {
                jpql.append(" AND c.spirit.id = :spiritId"); // Conditional = only append if user chooses a spirit type
            }

            var query = em.createQuery(jpql.toString(), Cocktail.class)
                    .setParameter("familyId", familyId);

            if (spiritId != null) {
                query.setParameter("spiritId", spiritId);
            }

            List<Cocktail> cocktails = query.getResultList();

            List<CocktailCandidate> candidates = new ArrayList<>();
            for (Cocktail cocktail : cocktails) {
                candidates.add(new CocktailCandidate
                        (cocktail, buildFlavorProfile(cocktail)));
            }

            return candidates;
        }
    }



    private Map<Long, Integer> buildFlavorProfile(Cocktail cocktail) {
        Map <Long, Integer> sums = new HashMap<>();

        if (cocktail.getSpirit() != null) {
            for (SpiritFlavorTag tag : cocktail.getSpirit().getFlavorTags()) {
                sums.merge(tag.getFlavorTag().getId(), tag.getIntensity(), Integer::sum);
            }
        }

        if (cocktail.getLiqueur() != null) {
            for (LiqueurFlavorTag tag : cocktail.getLiqueur().getFlavorTags()) {
                sums.merge(tag.getFlavorTag().getId(), tag.getIntensity(), Integer::sum);
            }
        }

        if (cocktail.getMixer() != null) {
            for (MixerFlavorTag tag : cocktail.getMixer().getFlavorTags()) {
                sums.merge(tag.getFlavorTag().getId(), tag.getIntensity(), Integer::sum);
            }
        }

        if (cocktail.getSyrup() != null) {
            for (SyrupFlavorTag tag : cocktail.getSyrup().getFlavorTags()) {
                sums.merge(tag.getFlavorTag().getId(), tag.getIntensity(), Integer::sum);
            }
        }

        return rankFlavorProfile(sums);
    }



    private Map<Long, Integer> rankFlavorProfile(Map<Long, Integer> sums){
        if (sums.isEmpty()) {
            return sums;
        }

        List<Map.Entry<Long, Integer>> sortedEntries = new ArrayList<>(sums.entrySet());
        sortedEntries.sort((a, b) -> {
            int compareBySum = b.getValue() - a.getValue(); // Highest sum first

            if(compareBySum != 0) {
                return compareBySum;
            }
            return a.getKey().compareTo(b.getKey()); // deterministic - if two sums are equal
        });

        Map<Long, Integer> ranked = new HashMap<>();
        int score = sortedEntries.size();

        for (Map.Entry<Long, Integer> entry : sortedEntries) {
            ranked.put(entry.getKey(), score);
            score--;
        }

        return ranked;
    }


}
