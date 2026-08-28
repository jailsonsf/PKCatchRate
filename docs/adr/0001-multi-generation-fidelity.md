# 0001: Full per-generation formula fidelity

The calculator supports three game-generation buckets — Gen III–IV, Gen V, Gen VI+ — and each bucket uses its own catch formula, status multipliers, and Poké Ball availability and multipliers.

We originally intended a single shared formula with only the status multiplier varying by generation, but the catch math genuinely differs between buckets: Gen III–IV computes the catch value `a` in 8-bit space with a linear probability curve (`≈ a/255`), while Gen V and Gen VI+ compute `a` in 4096-scaled space with a `^0.75` curve and differ in the shake threshold exponent, shake-check count, and the sleep/freeze status multiplier (×2, ×2.5, ×2 respectively). Within the VI+ bucket, conditional-ball multipliers also vary (e.g., Net Ball ×3 in Gen VI vs ×3.5 in Gen VII+); we use the latest (Gen IX) values there.

Peripheral mechanics — dark grass, Pass/O-Powers/Roto Catch, raid battles, Safari Zone bait/rock logic, the Gen VIII level bonus and difficulty factor, critical captures, and the Let's Go!/Legends: Arceus systems — are deliberately excluded so the engine models only the core capture formula per generation.

Status: accepted
