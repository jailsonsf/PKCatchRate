# 0003: Bundled CSV dataset loaded into memory

The Pokémon Dataset is a static CSV (`all_pokemon_data.csv`) shipped in the application resources and parsed once at startup into an in-memory repository. There is no embedded database.

An embedded DB (e.g., H2 + JPA) was considered and rejected: the data is read-only, small (~1,100 rows), and never mutated, so a database adds migration, seeding, and configuration overhead with no benefit. The CSV is the single source of truth and is trivially diffable in git.

Status: accepted
