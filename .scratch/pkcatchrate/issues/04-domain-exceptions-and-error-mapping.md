# 04 — Domain exception hierarchy + REST error mapping

Status: ready-for-human

## Comments

Implemented via TDD (red → green across four confirmed seams: engine validation, exception hierarchy, `@RestControllerAdvice`, web layer). 73 tests green (was 63).

- New domain layer: `com.jailsonsf.pkcatchrate.exception` with abstract base `CatchRateException` and the four subtypes — `UnknownPokemonException`, `InvalidLevelException`, `InvalidHpException`, and `BallNotAvailableException` (relocated here from `engine`). The controller no longer throws Spring's `ResponseStatusException` inline or keeps its own `@ExceptionHandler`; `CatchRateController` throws `UnknownPokemonException` on a species miss and the repository keeps its `Optional` return.
- The engine now validates its own inputs at the top of `CatchEngine.compute` (`validate`): level outside 1–100 → `InvalidLevelException`, current HP < 1 → `InvalidHpException`. The web DTO (`CatchRateRequest`) already carries `@NotBlank/@Min/@Max`, so bean validation rejects bad DTOs before the engine on the HTTP path; the engine-level guards exist so the domain exceptions are reachable when the engine is invoked directly. `currentHP > maxHP` stays clamped to max HP (as before); `currentHP == 0` is the invalid-HP error.
- New `@RestControllerAdvice` (`CatchRateExceptionHandler`) maps each subtype via its own `@ExceptionHandler` to a status + consistent JSON body (`ApiError` record: status, error code, message): `UnknownPokemonException` → 404 `UNKNOWN_POKEMON`, `InvalidLevelException` → 400 `INVALID_LEVEL`, `InvalidHpException` → 400 `INVALID_HP`, `BallNotAvailableException` → 422 `BALL_NOT_AVAILABLE`. `MethodArgumentNotValidException` (bean validation) → 400 `VALIDATION_FAILED` with the joined field errors, so the error body is uniform across the API.
- Mapping coverage: `UNKNOWN_POKEMON`→404 and `BALL_NOT_AVAILABLE`→422 are exercised end-to-end via MockMvc; because bean validation rejects out-of-range level and HP 0 before the engine, `INVALID_LEVEL`/`INVALID_HP`→400 are covered by direct unit tests of the advice handlers plus engine tests asserting the exceptions are thrown.
- Decisions confirmed during planning: invalid-input subtypes map to 400 (matching the pre-existing bean-validation 400s); a new `exception` package hosts the hierarchy; the controller raises `UnknownPokemonException` while the repository stays `Optional`; engine-level validation is defensive (the API path is pre-blocked by bean validation).

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
