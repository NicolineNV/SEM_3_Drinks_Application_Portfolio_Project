**OBS! - This is a school project - not a fully functioning application and still a work-in-progress.
In this project I am using TheCocktailDB.com free API, as this is a school project and not a production-ready application.
All rights reserved to TheCocktailDB**

## Drinks Application

### About the project

This project is a drink recommendation application built for a 3rd-semester 
school project. Rather than presenting users with a static library of 
recipes to scroll through, the application guides them through a short, 
engaging quiz designed to pinpoint exactly the kind of drink they're in 
the mood for.

At the core of the experience is the **taste compass** — an interactive, 
free-placement tool where users position themselves along different flavor 
axes to express their preferences visually rather than through plain text 
answers. Based on the user's answers, the application filters through all 
available recipes to surface the best matches.

The goal of the project is not just to build a functioning application, 
but to demonstrate solid domain modeling and clean architecture using 
different kinds of patterns.

### Tech stack

- **Frontend:**
- **Backend:**
- **Database:**
- **External API:** [TheCocktailDB](https://www.thecocktaildb.com/) (free tier)
- **Fonts:**

### Architecture

The application is built around a normalized domain model that separates 
**Spirit**, **Liqueur**, and **Mixer** as distinct entities, each with 
independent links to flavor tags. This allows for flexible, precise 
filtering rather than treating ingredients as a single flat category.

Three design patterns form the backbone of the recommendation logic:

- **Specification pattern** — used to filter recipes based on the 
  combination of criteria gathered from the user's quiz answers.
- **Strategy pattern** — used to score and rank the filtered recipes, 
  allowing different scoring strategies to be swapped in if needed.
- **Decorator pattern** — used to handle garnish logic, layering optional 
  details onto a recipe without modifying its core structure.

Together, these patterns keep the filtering and scoring logic decoupled 
and easy to extend as new criteria or recipe types are added.
