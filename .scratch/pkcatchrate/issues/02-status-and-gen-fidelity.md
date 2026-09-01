# 02 — Status conditions + full per-generation fidelity

Status: ready-for-human

## Comments

Implemented via TDD (red → green across two confirmed seams: catch engine, web layer). 27 tests green (was 13).

- New domain types: `StatusCondition` (NONE/SLEEP/FREEZE/PARALYZE/POISON/BURN, single value by enum) and `GameGeneration` (GEN_III_IV/GEN_V/GEN_VI_PLUS). `POST /api/catch-rate` accepts optional `status` and `generation` (default NONE / GEN_III_IV).
- Formulas per generation (sources: Bulbapedia "Catch rate"; Dragonfly Cave gen5capture and gen-vi-vii-capturing; Gen III-IV cross-checked against pret/pokeemerald `Cmd_handleballthrow`):
  - III–IV: `a = (rate·ball/10)·(3M−2H)/(3M)`, status ×2 (sleep/freeze) or ×15/10 (para/poison/burn) applied to floored `a`, `b = 1048560/isqrt(isqrt(16711680/a))`, 4 shakes.
  - V: `r1 = (3M−2H)·rate·ballFixed/(3M)` (ballFixed ∈ 4096/6144/8192), `a_scaled = r1·statusFixed/4096` (statusFixed ∈ 10240/6144/4096, truncating), `b = ⌊65536·(a_scaled/1044480)^¼⌋`, 3 shakes.
  - VI+: same `a_scaled`, `b = ⌊65536·(a_scaled/1044480)^(3/16)⌋`, 4 shakes.
- Golden values (Pikachu L50 full HP, Poke Ball): III–IV NONE a=63 b=47661 p=27.9726%; III–IV SLEEP a=126 b=55187 p=50.2837%; V NONE a=63 b=46265 p=35.1818%; V SLEEP a=158 b=58175 p=69.9470% (×2.5 confirmed); VI+ NONE a=63 b=50473 p=35.1817%; VI+ SLEEP a=158 b=59934 p=69.9477%. Mewtwo L70 full HP Ultra: V NONE a=2 b=19503 p=2.6355%; VI+ NONE a=2 b=26405 p=2.6353%. Gen V and VI+ normal-capture probabilities match as documented.
- Decisions: `a` normalized to 0–255 as `a_scaled/4096` (floor); guaranteed when `a ≥ 255` (III–IV) or `a_scaled ≥ 1044480` (V/VI+), reporting a=255 b=65535 p=100; status convention is truncation (`r1·statusFixed/4096`), consistent with Bulbapedia and Emerald's ×15/10 — Dragonfly's round-half-up variant was considered but not used.
- Out of scope here: ball-not-available enforcement (deferred to issues 03/04 — no fixed ball is unavailable in any generation) and conditional-ball per-gen values (issue 03).

## What to build

Extend the engine to the full per-generation fidelity decided in ADR-0001, plus Status Conditions.

Add the single `StatusCondition` enum: NONE, SLEEP, FREEZE, PARALYZE, POISON, BURN (only one may apply, matching the games). Wire per-generation status multipliers into the Catch Value computation:
- Gen III–IV and Gen VI+: sleep/freeze ×2, para/poison/burn ×1.5
- Gen V: sleep/freeze ×2.5, para/poison/burn ×1.5

Implement the Gen V and Gen VI+ formulas alongside the existing Gen III–IV one, each with its own `a`/`b` math and shake-check count (III–IV: 4 checks, linear `≈ a/255`; V: 3 checks; VI+: 4 checks, both `≈ (a/1044480)^0.75`). The reported `a` stays normalized to 0–255 across all generations. The Game Generation input (III–IV / V / VI+) selects formula, status multipliers, and ball availability/multipliers for the fixed balls (Net Ball's ×3 vs ×3.5 split and Timer/Quick/Dusk/Dream per-gen values arrive in issue 03 — here only the fixed balls plus any availability gates among them).

Choosing a ball not available in the selected Game Generation raises the domain exception for ball-not-available (subtype exists from issue 04's hierarchy — see note; if 04 is not yet merged, throw a temporary placeholder exception and refactor to the hierarchy in 04).

## Acceptance criteria

- [ ] `StatusCondition` enum is accepted by the API and applied via the per-gen multiplier table
- [ ] Selecting Gen V or VI+ uses that generation's formula; results match published golden values for each generation
- [ ] The reported `a` is normalized 0–255 regardless of generation
- [ ] Status multipliers verified by golden values per generation (sleep/freeze at ×2.5 only in Gen V)
- [ ] Only one Status Condition is representable in the request model

## Blocked by

- 01-tracer-bullet-scaffold (`.scratch/pkcatchrate/issues/01-tracer-bullet-scaffold.md`)
