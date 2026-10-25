package app.controllers;

import app.services.LookupService;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;



public class LookupController {

    private final LookupService lookupService;

    public LookupController(EntityManagerFactory emf) {
        this.lookupService = new LookupService(emf);
    }

    public void getFamilies(Context ctx) { ctx.json(lookupService.getFamilies()); }

    public void getSpirits(Context ctx) { ctx.json(lookupService.getSpirits()); }

    public void getLiqueurs(Context ctx) { ctx.json(lookupService.getLiqueurs()); }

    public void getMixers(Context ctx) { ctx.json(lookupService.getMixers()); }

    public void getSyrups(Context ctx) { ctx.json(lookupService.getSyrups()); }

    public void getGarnishes(Context ctx) { ctx.json(lookupService.getGarnishes()); }

    public void getFlavorTags(Context ctx) { ctx.json(lookupService.getFlavorTags()); }
}
