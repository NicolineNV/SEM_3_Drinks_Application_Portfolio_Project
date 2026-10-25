package app.services;

import app.dao.*;
import app.dto.OptionDTO;
import jakarta.persistence.EntityManagerFactory;

import java.util.Comparator;
import java.util.List;

public class LookupService {

    private static final Comparator<OptionDTO> BY_NAME =
            Comparator.comparing(OptionDTO::name);

    private final CocktailFamilyDAO cocktailFamilyDAO;
    private final SpiritDAO spiritDAO;
    private final LiqueurDAO liqueurDAO;
    private final MixerDAO mixerDAO;
    private final SyrupDAO syrupDAO;
    private final GarnishDAO garnishDAO;
    private final FlavorTagDAO flavorTagDAO;

    public LookupService(EntityManagerFactory emf) {
        this.cocktailFamilyDAO = new CocktailFamilyDAO(emf);
        this.spiritDAO = new SpiritDAO(emf);
        this.liqueurDAO = new LiqueurDAO(emf);
        this.mixerDAO = new MixerDAO(emf);
        this.syrupDAO = new SyrupDAO(emf);
        this.garnishDAO = new GarnishDAO(emf);
        this.flavorTagDAO = new FlavorTagDAO(emf);
    }

    public List<OptionDTO> getFamilies() {
        return cocktailFamilyDAO.getAll().stream()
                .map(f -> new OptionDTO(f.getId(), f.getName(), f.getDescription()))
                .sorted(BY_NAME).toList();
    }

    public List<OptionDTO> getSpirits() {
        return spiritDAO.getAll().stream()
                .map(s -> new OptionDTO(s.getId(), s.getName(), s.getDescription()))
                .sorted(BY_NAME).toList();
    }

    public List<OptionDTO> getLiqueurs() {
        return liqueurDAO.getAll().stream()
                .map(l -> new OptionDTO(l.getId(), l.getName(), null))
                .sorted(BY_NAME).toList();
    }

    public List<OptionDTO> getMixers() {
        return mixerDAO.getAll().stream()
                .map(m -> new OptionDTO(m.getId(), m.getName(), null))
                .sorted(BY_NAME).toList();
    }

    public List<OptionDTO> getSyrups() {
        return syrupDAO.getAll().stream()
                .map(s -> new OptionDTO(s.getId(), s.getName(), null))
                .sorted(BY_NAME).toList();
    }

    public List<OptionDTO> getGarnishes() {
        return garnishDAO.getAll().stream()
                .map(g -> new OptionDTO(g.getId(), g.getName(), null))
                .sorted(BY_NAME).toList();
    }

    // Kept in id order: the quiz asks about the tags in a fixed order
    public List<OptionDTO> getFlavorTags() {
        return flavorTagDAO.getAll().stream()
                .map(t -> new OptionDTO(t.getId(), t.getName(), null))
                .sorted(Comparator.comparing(OptionDTO::id)).toList();
    }
}
