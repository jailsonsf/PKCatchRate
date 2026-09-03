# 03 — Conditional balls + Battle Context

Status: ready-for-human

## Comments

Implemented via TDD (red → green across three confirmed seams: `PokeBallRates`, catch engine, web layer). 63 tests green (was 27).

- New domain types: `BattleContext` (record: turnsElapsed, isFirstTurn, isAlreadyCaught, fromFishing, onWater, inCaveOrNight — nullable, absent → neutral) and `PokeBallRates` (resolver seam answering per-generation availability + effective multiplier). `PokeBall` is now a plain enum of 19 balls (fixed, conditional, excluded).
- Multipliers per ADR-0002/0001 and Bulbapedia (cross-checked): Timer III–IV `(turns+10)/10` cap 4 at turn 30 vs V+ `1+turns·1229/4096` cap at turn 10; Quick 4× (III–IV bucket, i.e. Gen IV value) / 5× (V+); Repeat/Net 3× (III–V) / 3.5× (VI+, latest Gen IX value); Dive 3.5× onWater/fromFishing; Nest `(40−L)/10` (III–IV) vs `⌊(41−L)·4096/10⌋/4096` (V) and VI+ only for L 1–29; Dusk 3.5× (III–V) / 3× (VI+) in cave or at night; Dream 4× asleep (VI+ only).
- Availability: fixed/conditional balls selectable in all three buckets (GEN_III_IV bucket models Gen IV's latest values); Dream only GEN_VI_PLUS; Lure and the six ADR-0002-excluded balls (Beast/Heavy/Fast/Level/Love/Moon) present in the enum but never available → `BallNotAvailableException` (placeholder domain exception; issue 04 will formalize the hierarchy + advice) mapped to 422 at the controller.
- Golden values (Python reference, engine-level): Quick first-turn GEN_V Mewtwo a=5 b=24523 p=5.239%; Dusk cave+asleep VI+ Mewtwo a=7 b=33831 p=7.101%; Net on Bug/Water VI+ Paras a=221 b=63836 p=90.02%; Timer caps verified turn 29→30 (III–IV) and 9→10 (V); Nest low vs high level a=221 vs 63. Seam 1 (rates) holds per-ball multiplier goldens including absent-context neutrality.
- Decisions confirmed during planning: bucket semantics (GEN_III_IV treats Dusk/Quick as available since it covers Gen IV); Net/Nest/Dream keep reacting to types/level/status even without a BattleContext (only battle-flag balls go neutral); Lure = always unavailable.

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
