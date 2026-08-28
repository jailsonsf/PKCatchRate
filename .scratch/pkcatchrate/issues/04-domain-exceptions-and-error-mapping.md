# 04 — Domain exception hierarchy + REST error mapping

Status: ready-for-agent

## What to build

Introduce the domain exception hierarchy and map it to clean HTTP responses, so the engine's error cases surface properly through the API.

A `CatchRateException` base class with specific subtypes: unknown Pokémon name, invalid level (outside 1–100), invalid HP (0), ball not available in the chosen Game Generation. The web layer validates the request DTO with Spring Bean Validation (`@NotBlank`, `@Min/@Max`, etc.) and a `@RestControllerAdvice` maps each domain exception to a meaningful status (404 for unknown Pokémon, 400/422 for invalid input, 422 for ball-not-available), returning a consistent error response body.

Also clamp Current HP above Max HP to Max HP (as decided), while HP 0 remains an error.

## Acceptance criteria

- [ ] `CatchRateException` hierarchy with the specified subtypes exists in the domain layer
- [ ] `@RestControllerAdvice` returns distinct, correct HTTP statuses for each subtype with a consistent JSON error body
- [ ] Bean Validation rejects bad DTOs before the engine is reached
- [ ] currentHP > maxHP is clamped to maxHP; currentHP == 0 returns the invalid-HP error
- [ ] Tests cover each exception → HTTP status mapping

## Blocked by

- 01-tracer-bullet-scaffold (`.scratch/pkcatchrate/issues/01-tracer-bullet-scaffold.md`) — can run in parallel with 02 and 03
