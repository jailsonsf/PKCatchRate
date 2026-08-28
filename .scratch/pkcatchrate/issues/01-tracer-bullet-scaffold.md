# 01 — Tracer bullet: scaffold + dataset + Gen III–IV fixed-ball path

Status: ready-for-human

## Comments

Implemented via TDD (red → green across three confirmed seams: dataset repository, catch engine, web layer). 13 tests green; boot verified against live `POST /api/catch-rate`.

- Golden values derived from the decompiled Emerald catch routine (`Cmd_handleballthrow` in pret/pokeemerald), not recomputed by the implementation: Pikachu L50 full HP Poké Ball → a=63, b=47661, 27.97%; Great → a=95, b=52428; Mewtwo L70 full HP Ultra → a=2, b=19784, 0.83%.
- Formula: `a = floor((rate × ballTenths / 10) × (3·maxHP − 2·hp) / (3·maxHP))`; `b = floor(1048560 / isqrt(isqrt(16711680/a)))`; `p = (b/65536)^4`; Max HP = `floor((2·base + 131) × level / 100) + 10` (31 IV / 0 EV / neutral).
- Decisions: per-shake pass is `b/65536` (CONTEXT.md "succeeds if below b"); guaranteed cases report a=255, b=65535, probability=100, expectedBalls=1; unknown species → 404; ball multiplier uses the exact integer tenths (10/15/20) from the game code.
- Maven wrapper committed (`./mvnw test`). Original `pokémon.zip` still at repo root — consider removing or ignoring it now that the CSV is extracted.

## What to build

The first end-to-end vertical slice: a Spring Boot 3 + Java 21 Maven project, single module, root package `com.jailsonsf.pkcatchrate`, that loads the Pokémon Dataset from a bundled CSV and returns a correct Catch Probability for the simplest supported case.

The engine must, for a given species name, level, and Current HP, derive Max HP (species base HP stat + level, assuming 31 IV / 0 EV / neutral nature), compute the Catch Value `a` and Shake Threshold `b` using only the Gen III–IV formula, and produce a `CatchResult` with `probability` (percentage, `double`), `guaranteed`, `a` (normalized 0–255), `b` (0–65535), and `expectedBalls`.

Ball support is deliberately restricted to the fixed-multiplier balls Poké (1×), Great (1.5×), Ultra (2×), and Master (guaranteed). No Status Condition, no Battle Context, no conditional balls, only the Gen III–IV Game Generation. Master Ball and `a ≥ 255` both yield `guaranteed = true`, `probability = 100.0`.

Expose the engine over `POST /api/catch-rate` with a separate request DTO (name, level, current HP, ball) and result DTO, translating between the two. Basic validation on the DTO: level 1–100, current HP ≥ 1.

The dataset CSV is at the repo root inside `pokémon.zip` — extract it and place the CSV under `src/main/resources/pokemon/`.

## Acceptance criteria

- [ ] Maven + Spring Boot 3 + Java 21 single-module project boots; root package `com.jailsonsf.pkcatchrate`
- [ ] Dataset CSV loaded once into an in-memory repository keyed by species name (Catch Rate, base HP, types)
- [ ] `POST /api/catch-rate` returns a correct `CatchResult` for a known Gen III–IV fixed-ball case (golden values from Bulbapedia worked examples)
- [ ] Level out of 1–100 or current HP of 0 is rejected at the web layer
- [ ] Master Ball returns `guaranteed = true`, `probability = 100.0`
- [ ] Unit tests cover the formula's intermediate `a` and `b` for at least two species

## Blocked by

None - can start immediately
