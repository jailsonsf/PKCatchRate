# PKCatchRate

A Pokémon catch-rate calculator: given a species, its level, current HP, a single status condition, a Poké Ball, and the game generation being played, it returns the probability of a successful capture plus the intermediate catch math.

## Language

**Game Generation**:
One of three buckets — Gen III–IV, Gen V, or Gen VI+ — that select the catch formula, the status multipliers, and the available Poké Balls and their multipliers.
_Avoid_: Gen, generation number, version

**Catch Rate**:
The species-specific constant (1–255) from the dataset (e.g., Pikachu 190, Mewtwo 3). Fixed per species regardless of battle.
_Avoid_: catch probability, catch chance, odds

**Catch Value (a)**:
The intermediate "modified catch rate" after applying HP, ball, and status factors. Reported normalized to 0–255 so values are comparable across generations.
_Avoid_: modified catch rate (too close to Catch Rate), score

**Shake Threshold (b)**:
The per-shake-check threshold (0–65535) derived from `a`; each shake check draws a random value and succeeds if it is below `b`.
_Avoid_: shake chance

**Catch Probability**:
The final result — the percentage chance (0–100) that a single thrown ball captures the Pokémon.
_Avoid_: catch rate, odds

**Guaranteed Capture**:
A capture that always succeeds: either the Catch Value reaches 255 or the Master Ball is used. Reported as `guaranteed = true` with 100% probability.

**Expected Balls**:
The average number of throws needed for one capture, `100 / CatchProbability`.

**Max HP**:
The target's full health, derived from the species' base HP stat, its level, and fixed assumptions (31 IV, 0 EV, neutral nature — nature does not affect HP).
_Avoid_: full HP, base HP

**Current HP**:
The target's health at the moment of the throw. Must be at least 1; if it exceeds Max HP it is clamped to Max HP.

**Status Condition**:
The single status condition afflicting the target: none, sleep, freeze, paralyze, poison, or burn. Only one may apply at a time, matching the games.
_Avoid_: status effect, status ailment, ailment

**Poké Ball**:
The ball being thrown. Each has a catch multiplier and an availability per Game Generation. The Master Ball always yields a Guaranteed Capture.
_Avoid_: ball (too generic), capture device

**Conditional Ball**:
A Poké Ball whose multiplier depends on the Battle Context or the Status Condition rather than being a fixed value (e.g., Timer, Quick, Repeat, Net, Dive, Lure, Nest, Dusk, Dream).
_Avoid_: special ball, context ball

**Battle Context**:
Optional battle flags that condition the Conditional Balls: turns elapsed, is first turn, species already caught, encountered by fishing, encountered on water, and whether the encounter happens in a cave or at night. Absence means every Conditional Ball falls back to its neutral multiplier.
_Avoid_: battle state, context

**Pokémon Dataset**:
The bundled CSV of every species (base and alternate forms) with its name, Catch Rate, base HP stat, and primary/secondary types.
_Avoid_: Pokédex, species table, data file
