# 05 — Comprehensive reference-value test suite

Status: ready-for-agent

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
