package com.jailsonsf.pkcatchrate.engine;

import com.jailsonsf.pkcatchrate.ball.PokeBall;
import com.jailsonsf.pkcatchrate.battle.BattleContext;
import com.jailsonsf.pkcatchrate.generation.GameGeneration;
import com.jailsonsf.pkcatchrate.status.StatusCondition;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CatchEngineGoldenValuesTest {

    private final CatchEngine engine = new CatchEngine();

    @ParameterizedTest(name = "{0}")
    @MethodSource("goldenValues")
    void matchesPublishedReference(String caseName, int catchRate, int baseHp, int level, int currentHp,
                                   PokeBall ball, StatusCondition status, GameGeneration generation,
                                   BattleContext battleContext, Optional<String> primaryType, Optional<String> secondaryType,
                                   int expectedA, int expectedB, double expectedProbability, boolean expectedGuaranteed) {
        CatchResult result = engine.compute(new CatchRequest(
                catchRate, baseHp, level, currentHp, ball, status, generation, primaryType, secondaryType, battleContext));

        assertThat(result.guaranteed())
                .as("%s guaranteed", caseName)
                .isEqualTo(expectedGuaranteed);
        assertThat(result.a()).as("%s catch value a", caseName).isEqualTo(expectedA);
        assertThat(result.b()).as("%s shake threshold b", caseName).isEqualTo(expectedB);
        assertThat(result.probability())
                .as("%s probability", caseName)
                .isCloseTo(expectedProbability, within(0.001));
    }

    private static Stream<Arguments> goldenValues() {
        return Stream.of(
                // Fixed balls, Gen III-IV — Emerald decomp / Bulbapedia worked example
                // Pikachu L50 full HP, Poké Ball (rate 190, base HP 35)
                Arguments.of("Gen III-IV Poké Ball Pikachu full HP (Emerald decomp)",
                        190, 35, 50, 110, PokeBall.POKE, StatusCondition.NONE, GameGeneration.GEN_III_IV,
                        null, Optional.empty(), Optional.empty(), 63, 47661, 27.972603, false),
                // Great Ball — Emerald decomp
                Arguments.of("Gen III-IV Great Ball Pikachu full HP (Emerald decomp)",
                        190, 35, 50, 110, PokeBall.GREAT, StatusCondition.NONE, GameGeneration.GEN_III_IV,
                        null, Optional.empty(), Optional.empty(), 95, 52428, 40.957500, false),
                // Mewtwo L70 full HP, Ultra Ball (rate 3, base HP 106)
                Arguments.of("Gen III-IV Ultra Ball Mewtwo full HP (Emerald decomp)",
                        3, 106, 70, 250, PokeBall.ULTRA, StatusCondition.NONE, GameGeneration.GEN_III_IV,
                        null, Optional.empty(), Optional.empty(), 2, 19784, 0.830494, false),
                // Sleep x2 status — Emerald decomp
                Arguments.of("Gen III-IV Poké Ball Pikachu asleep x2 (Emerald decomp)",
                        190, 35, 50, 110, PokeBall.POKE, StatusCondition.SLEEP, GameGeneration.GEN_III_IV,
                        null, Optional.empty(), Optional.empty(), 126, 55187, 50.283723, false),
                // Paralyze x1.5 status
                Arguments.of("Gen III-IV Poké Ball Pikachu paralyzed x1.5",
                        190, 35, 50, 110, PokeBall.POKE, StatusCondition.PARALYZE, GameGeneration.GEN_III_IV,
                        null, Optional.empty(), Optional.empty(), 94, 52428, 40.957500, false),

                // Gen V — Dragonfly Cave / Python reference (issue 02)
                Arguments.of("Gen V Poké Ball Pikachu full HP",
                        190, 35, 50, 110, PokeBall.POKE, StatusCondition.NONE, GameGeneration.GEN_V,
                        null, Optional.empty(), Optional.empty(), 63, 46265, 35.181788, false),
                // Sleep x2.5 only in Gen V
                Arguments.of("Gen V Poké Ball Pikachu asleep x2.5",
                        190, 35, 50, 110, PokeBall.POKE, StatusCondition.SLEEP, GameGeneration.GEN_V,
                        null, Optional.empty(), Optional.empty(), 158, 58175, 69.947047, false),
                Arguments.of("Gen V Great Ball Pikachu full HP",
                        190, 35, 50, 110, PokeBall.GREAT, StatusCondition.NONE, GameGeneration.GEN_V,
                        null, Optional.empty(), Optional.empty(), 95, 51200, 47.683716, false),
                // Paralyze x1.5 — Mewtwo L70 Ultra Ball
                Arguments.of("Gen V Ultra Ball Mewtwo paralyzed x1.5",
                        3, 106, 70, 250, PokeBall.ULTRA, StatusCondition.PARALYZE, GameGeneration.GEN_V,
                        null, Optional.empty(), Optional.empty(), 3, 21583, 3.571870, false),

                // Gen VI+ — Gen VII/VIII catch formula
                Arguments.of("Gen VI+ Poké Ball Pikachu full HP",
                        190, 35, 50, 110, PokeBall.POKE, StatusCondition.NONE, GameGeneration.GEN_VI_PLUS,
                        null, Optional.empty(), Optional.empty(), 63, 50473, 35.181695, false),
                Arguments.of("Gen VI+ Poké Ball Pikachu asleep",
                        190, 35, 50, 110, PokeBall.POKE, StatusCondition.SLEEP, GameGeneration.GEN_VI_PLUS,
                        null, Optional.empty(), Optional.empty(), 158, 59934, 69.947683, false),
                Arguments.of("Gen VI+ Great Ball Pikachu full HP",
                        190, 35, 50, 110, PokeBall.GREAT, StatusCondition.NONE, GameGeneration.GEN_VI_PLUS,
                        null, Optional.empty(), Optional.empty(), 95, 54459, 47.682490, false),
                Arguments.of("Gen VI+ Ultra Ball Mewtwo full HP",
                        3, 106, 70, 250, PokeBall.ULTRA, StatusCondition.NONE, GameGeneration.GEN_VI_PLUS,
                        null, Optional.empty(), Optional.empty(), 2, 26405, 2.635269, false),

                // Guaranteed captures
                Arguments.of("Master Ball is guaranteed in Gen III-IV",
                        3, 106, 70, 250, PokeBall.MASTER, StatusCondition.NONE, GameGeneration.GEN_III_IV,
                        null, Optional.empty(), Optional.empty(), 255, 65535, 100.0, true),
                Arguments.of("Master Ball is guaranteed in Gen V",
                        3, 106, 70, 250, PokeBall.MASTER, StatusCondition.NONE, GameGeneration.GEN_V,
                        null, Optional.empty(), Optional.empty(), 255, 65535, 100.0, true),
                Arguments.of("Master Ball is guaranteed in Gen VI+",
                        3, 106, 70, 250, PokeBall.MASTER, StatusCondition.NONE, GameGeneration.GEN_VI_PLUS,
                        null, Optional.empty(), Optional.empty(), 255, 65535, 100.0, true),
                // Catch value reaching 255 without Master Ball
                Arguments.of("Gen III-IV catch value 255 is guaranteed (Ultra + asleep L50 Pikachu low HP)",
                        190, 35, 50, 109, PokeBall.ULTRA, StatusCondition.SLEEP, GameGeneration.GEN_III_IV,
                        null, Optional.empty(), Optional.empty(), 255, 65535, 100.0, true),
                Arguments.of("Gen V catch value 255 is guaranteed (rate 255 L50 Ultra near-zero HP)",
                        255, 30, 50, 1, PokeBall.ULTRA, StatusCondition.NONE, GameGeneration.GEN_V,
                        null, Optional.empty(), Optional.empty(), 255, 65535, 100.0, true),

                // Conditional balls — Python reference / Bulbapedia ball table (issue 03)
                // Quick Ball first turn: 5x in Gen V+ Mewtwo L70
                Arguments.of("Gen V Quick Ball first turn Mewtwo",
                        3, 106, 70, 250, PokeBall.QUICK, StatusCondition.NONE, GameGeneration.GEN_V,
                        context(0, true, false, false, false, false), Optional.empty(), Optional.empty(),
                        5, 24523, 5.239393, false),
                Arguments.of("Gen VI+ Quick Ball first turn Mewtwo",
                        3, 106, 70, 250, PokeBall.QUICK, StatusCondition.NONE, GameGeneration.GEN_VI_PLUS,
                        context(0, true, false, false, false, false), Optional.empty(), Optional.empty(),
                        5, 31355, 5.239713, false),
                // Dusk Ball 3x in cave at night, Gen VI+ Mewtwo asleep
                Arguments.of("Gen VI+ Dusk Ball in cave asleep Mewtwo",
                        3, 106, 70, 250, PokeBall.DUSK, StatusCondition.SLEEP, GameGeneration.GEN_VI_PLUS,
                        context(0, false, false, false, false, true), Optional.empty(), Optional.empty(),
                        7, 33831, 7.101329, false),
                // Net Ball 3.5x against Bug/Water in Gen VI+
                Arguments.of("Gen VI+ Net Ball on Bug target",
                        190, 35, 50, 110, PokeBall.NET, StatusCondition.NONE, GameGeneration.GEN_VI_PLUS,
                        null, Optional.of("bug"), Optional.of("grass"),
                        221, 63836, 90.020815, false),
                // Dream Ball 4x asleep in Gen VI+ Mewtwo
                Arguments.of("Gen VI+ Dream Ball asleep Mewtwo",
                        3, 106, 70, 250, PokeBall.DREAM, StatusCondition.SLEEP, GameGeneration.GEN_VI_PLUS,
                        null, Optional.empty(), Optional.empty(),
                        10, 35707, 8.812390, false),
                // Repeat Ball 3.5x already caught, Gen VI+ Mewtwo
                Arguments.of("Gen VI+ Repeat Ball already caught Mewtwo",
                        3, 106, 70, 250, PokeBall.REPEAT, StatusCondition.NONE, GameGeneration.GEN_VI_PLUS,
                        context(0, false, true, false, false, false), Optional.empty(), Optional.empty(),
                        3, 29326, 4.009513, false),
                // Nest Ball falls to neutral 1x at level 100 in Gen III-IV (matches Poké Ball anchor)
                Arguments.of("Gen III-IV Nest Ball high level equals neutral Poké Ball",
                        190, 35, 100, 211, PokeBall.NEST, StatusCondition.NONE, GameGeneration.GEN_III_IV,
                        null, Optional.empty(), Optional.empty(),
                        63, 47661, 27.972603, false));
    }

    private static BattleContext context(int turns, boolean firstTurn, boolean alreadyCaught,
                                         boolean fishing, boolean water, boolean caveOrNight) {
        return new BattleContext(turns, firstTurn, alreadyCaught, fishing, water, caveOrNight);
    }
}
