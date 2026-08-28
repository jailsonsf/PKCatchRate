# 0002: Conditional balls limited to battle-context conditions

We model conditional Poké Balls only where their condition comes from the Battle Context flags or the Status Condition we already collect: Timer (turns), Quick (first turn), Repeat (already caught), Net (target types), Dive/Lure (fishing/water), Nest (level), Dusk (in cave or at night), and Dream (asleep, Gen VI+ only).

Every other conditional ball is deliberately out of scope and never uses its boosted multiplier:

- **Heavy, Fast, Level, Love, Moon, Beast** balls are excluded entirely (no conditional logic): their conditions would require dataset extensions (weight, base speed, player level, gender, evolution family, Ultra Beast flag).
- **Beast Ball** is not even offered, because its only multipliers (5× vs Ultra Beasts, ~0.1× otherwise) depend on a flag we do not track.

A future reader should not expect these balls to apply conditional logic.

Status: accepted
