# 02 — Status conditions + full per-generation fidelity

Status: ready-for-agent

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
