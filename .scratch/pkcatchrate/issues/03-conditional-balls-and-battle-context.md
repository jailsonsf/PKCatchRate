# 03 — Conditional balls + Battle Context

Status: ready-for-agent

## What to build

Add the optional `BattleContext` (see `CONTEXT.md`) and the conditional Poké Balls that depend on it, per ADR-0002 and the ball table agreed in planning.

`BattleContext` is an optional input with flags: turnsElapsed, isFirstTurn, isAlreadyCaught, fromFishing, onWater, inCaveOrNight. When absent, every conditional ball falls back to its neutral multiplier.

Conditional balls and their behavior:
- **Timer**: 1–4×, caps at 4× at 30 turns (Gen III–IV) vs 10 turns (Gen V+), driven by turnsElapsed
- **Quick**: 4× first turn (Gen III–IV bucket) / 5× (Gen V+), else 1×, driven by isFirstTurn
- **Repeat**: 3× (Gen III–VI) / 3.5× (Gen VII+) if isAlreadyCaught, else 1×
- **Net**: 3× (Gen III–VI) / 3.5× (Gen VII+) if target is Bug- or Water-type (dataset types), else 1×
- **Dive**: 3.5× if onWater/fromFishing per generation, else 1×
- **Nest**: level-based multiplier (see Bulbapedia: Gen III–IV `(40−level)/10` min 1×; Gen V+ `floor((41−level)/10)`), always ≥ 1×
- **Dusk**: 3.5× (Gen IV–V buckets... per ADR-0002 and planning: 3.5× Gen III–IV & V, 3× VI+) when inCaveOrNight, else 1×
- **Dream**: Gen VI+ only, 4× when asleep (Status Condition SLEEP), else 1×

Excluded entirely (never selectable): Beast, Heavy, Fast, Level, Love, Moon (ADR-0002). Ball availability per Game Generation is enforced for every ball.

## Acceptance criteria

- [ ] Every conditional ball's multiplier reacts to its Battle Context flag / Status Condition as specified above
- [ ] Absent Battle Context yields neutral multipliers for all conditional balls
- [ ] Ball availability enforced per Game Generation (e.g., Dusk unavailable before Gen IV, Dream only in VI+); excluded balls return ball-not-available
- [ ] Golden-value tests for Timer (30 vs 10 turn cap), Quick, Net (Bug/Water target), Repeat, Nest (low vs high level), Dusk (cave/night), Dream (asleep)

## Blocked by

- 02-status-and-gen-fidelity (`.scratch/pkcatchrate/issues/02-status-and-gen-fidelity.md`)
