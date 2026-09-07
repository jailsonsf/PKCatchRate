# 05 — Comprehensive reference-value test suite

Status: ready-for-human

## Comments

Implemented as test-only (no production changes). 116 tests green (was 73). Three new test files:

- `engine/CatchEngineGoldenValuesTest` — parameterized table of 25 documented golden anchors, each labelled with its source (Emerald decompiled `Cmd_handleballthrow`, Bulbapedia worked example, Python/Dragonfly Cave reference as recorded in issues 01–03), asserting `a`, `b`, probability within 0.001, and `guaranteed`. Covers all three Game Generations, the fixed balls (Poké/Great/Ultra/Master), guaranteed captures (Master + a≥255), and the anchored conditional balls (Quick, Dusk, Net, Dream, Repeat, Nest at its neutral 1× high-level fallback). Deliberately restricted to externally-verified values — no hand-recomputed cross-products (anti-tautology).
- `engine/CatchEnginePropertyTest` — deterministic sweeps over the public `CatchEngine` seam (no new dependency): probability ∈ [0,100]; guaranteed ⇒ 100% with a=255/b=65535/expectedBalls=1 and (non-Master) guaranteed ⇔ a≥255; Master always guaranteed; `probability × expectedBalls ≈ 100`; monotonicity (lower Current HP, beneficial Status Condition, stronger fixed ball, and each active conditional ball never decrease probability); status-sibling equivalence (sleep≡freeze, paralyze≡poison≡burn). These surfaced one real domain nuance: Dream Ball boosts SLEEP but not FREEZE, so the equivalence sweep excludes DREAM and a dedicated test asserts the boost.
- `web/CatchRateControllerWebMvcTest` — `@WebMvcTest` slice (real `CatchEngine` imported, `PokemonRepository` mocked): happy path through the real engine, clamping currentHP>maxHP, and each HTTP-reachable issue-04 error mapping (404 `UNKNOWN_POKEMON`, 422 `BALL_NOT_AVAILABLE`, 400 `VALIDATION_FAILED`). INVALID_LEVEL/INVALID_HP mappings remain advice-unit-covered (issue 04) because DTO bean validation pre-empts them over HTTP.

Decisions confirmed during planning: golden table uses published/documented anchors only; property tests are looped sweeps (no jqwik dependency); the `@WebMvcTest` slice is additive alongside the existing full-context `@SpringBootTest` controller test.

## What to build

Harden the engine with a broad, table-driven test suite against published reference values and invariant properties, complementing the per-issue tests.

Golden values: a parameterized table of inputs (species, level, current HP, Status Condition, ball, Game Generation, Battle Context) → expected Catch Probability / Catch Value `a` / Shake Threshold `b`, drawn from Bulbapedia's worked examples and hand-computed cases, covering all three Game Generations, all modeled balls, and each status multiplier.

Property tests: invariants such as probability in [0, 100]; `guaranteed = true` iff `a ≥ 255` or Master Ball; monotonicity — lower Current HP, a beneficial Status Condition, or a stronger/more-active ball never decreases the Catch Probability; `expectedBalls == 100 / probability`.

REST tests: `@WebMvcTest` + MockMvc covering the happy path, every error mapping from issue 04, and clamping behavior.

## Acceptance criteria

- [ ] Golden-value table covers all three generations, every modeled ball, and all status multipliers
- [ ] Property tests for bounds, guaranteed captures, and monotonicity pass
- [ ] `@WebMvcTest` endpoint tests cover happy path + each error mapping
- [ ] Every golden value matches the published reference within expected rounding

## Blocked by

- 03-conditional-balls-and-battle-context (`.scratch/pkcatchrate/issues/03-conditional-balls-and-battle-context.md`)
- 04-domain-exceptions-and-error-mapping (`.scratch/pkcatchrate/issues/04-domain-exceptions-and-error-mapping.md`)
