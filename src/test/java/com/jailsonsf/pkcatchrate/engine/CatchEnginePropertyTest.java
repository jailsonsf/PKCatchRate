package com.jailsonsf.pkcatchrate.engine;

import com.jailsonsf.pkcatchrate.ball.PokeBall;
import com.jailsonsf.pkcatchrate.ball.PokeBallRates;
import com.jailsonsf.pkcatchrate.battle.BattleContext;
import com.jailsonsf.pkcatchrate.generation.GameGeneration;
import com.jailsonsf.pkcatchrate.status.StatusCondition;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class CatchEnginePropertyTest {

    private final CatchEngine engine = new CatchEngine();
    private final PokeBallRates ballRates = new PokeBallRates();

    private record Species(int catchRate, int baseHp) {
    }

    private record Case(Species species, GameGeneration generation, PokeBall ball,
                        StatusCondition status, int level, int currentHp) {
        CatchResult compute(CatchEngine engine) {
            return engine.compute(new CatchRequest(
                    species.catchRate(), species.baseHp(), level, currentHp, ball, status, generation,
                    Optional.empty(), Optional.empty(), null));
        }
    }

    private static final List<Species> SPECIES = List.of(
            new Species(3, 106),    // Mewtwo-like
            new Species(45, 78),    // starter-evolution-like
            new Species(190, 35),   // Pikachu-like
            new Species(255, 1),
            new Species(255, 255),
            new Species(100, 100));

    private static final int[] LEVELS = {1, 25, 50, 75, 100};
    private static final int[] CURRENT_HPS = {1, 50, 150, 300};
    private static final StatusCondition[] STATUSES = StatusCondition.values();

    private Stream<Case> cases() {
        List<Case> cases = new ArrayList<>();
        for (Species species : SPECIES) {
            for (GameGeneration generation : GameGeneration.values()) {
                for (PokeBall ball : availableBalls(generation)) {
                    for (StatusCondition status : STATUSES) {
                        for (int level : LEVELS) {
                            for (int currentHp : CURRENT_HPS) {
                                cases.add(new Case(species, generation, ball, status, level, currentHp));
                            }
                        }
                    }
                }
            }
        }
        return cases.stream();
    }

    private Stream<Case> casesWithoutMasterBall() {
        return cases().filter(caseValue -> caseValue.ball() != PokeBall.MASTER);
    }

    private Stream<Case> casesWithoutDreamBall() {
        return cases().filter(caseValue -> caseValue.ball() != PokeBall.DREAM);
    }

    @Test
    void probabilityStaysWithinBoundsAcrossAllLegalInputs() {
        cases().forEach(caseValue -> {
            CatchResult result = caseValue.compute(engine);
            assertThat(result.probability())
                    .as("%s %s %s L%d HP%d %s", caseValue.species(), caseValue.ball(),
                            caseValue.generation(), caseValue.level(), caseValue.currentHp(), caseValue.status())
                    .isBetween(0.0, 100.0);
        });
    }

    @Test
    void guaranteedCaptureReportsHundredPercentAndCanonicalShakeThreshold() {
        cases().forEach(caseValue -> {
            CatchResult result = caseValue.compute(engine);
            if (result.guaranteed()) {
                assertThat(result.probability()).as("%s %s %s", caseValue.species(), caseValue.ball(), caseValue.generation())
                        .isEqualTo(100.0);
                assertThat(result.a()).isEqualTo(255);
                assertThat(result.b()).isEqualTo(65535);
                assertThat(result.expectedBalls()).isEqualTo(1.0);
            }
        });
    }

    @Test
    void masterBallIsAlwaysGuaranteed() {
        cases().forEach(caseValue -> {
            CatchResult result = caseValue.compute(engine);
            if (caseValue.ball() == PokeBall.MASTER) {
                assertThat(result.guaranteed()).as("%s %s", caseValue.species(), caseValue.generation()).isTrue();
            }
        });
    }

    @Test
    void nonMasterGuaranteedCaptureImpliesCatchValueReachesTwoFiftyFive() {
        casesWithoutMasterBall().forEach(caseValue -> {
            CatchResult result = caseValue.compute(engine);
            if (result.guaranteed()) {
                assertThat(result.a()).as("%s %s %s", caseValue.species(), caseValue.ball(), caseValue.generation())
                        .isEqualTo(255);
            } else {
                assertThat(result.a()).as("%s %s %s", caseValue.species(), caseValue.ball(), caseValue.generation())
                        .isLessThan(255);
            }
        });
    }

    @Test
    void expectedBallsAndProbabilityAreInverses() {
        cases().forEach(caseValue -> {
            CatchResult result = caseValue.compute(engine);
            assertThat(result.probability()).isGreaterThan(0.0);
            assertThat(result.expectedBalls()).isGreaterThan(0.0);
            assertThat(result.probability() * result.expectedBalls())
                    .as("%s %s %s", caseValue.species(), caseValue.ball(), caseValue.generation())
                    .isCloseTo(100.0, within(0.001));
        });
    }

    @Test
    void lowerCurrentHpNeverDecreasesProbability() {
        for (Species species : SPECIES) {
            for (GameGeneration generation : GameGeneration.values()) {
                for (PokeBall ball : availableBalls(generation)) {
                    for (StatusCondition status : STATUSES) {
                        for (int level : LEVELS) {
                            CatchResult full = compute(species, level, 300, ball, status, generation);
                            for (int currentHp : CURRENT_HPS) {
                                CatchResult lower = compute(species, level, currentHp, ball, status, generation);
                                assertThat(lower.probability())
                                        .as("%s L%d HP%d <= HP300 %s %s %s", species, level, currentHp, ball, status, generation)
                                        .isGreaterThanOrEqualTo(full.probability());
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void beneficialStatusNeverDecreasesProbability() {
        for (Species species : SPECIES) {
            for (GameGeneration generation : GameGeneration.values()) {
                for (PokeBall ball : availableBalls(generation)) {
                    for (int level : LEVELS) {
                        for (int currentHp : CURRENT_HPS) {
                            CatchResult baseline = compute(species, level, currentHp, ball, StatusCondition.NONE, generation);
                            for (StatusCondition status : STATUSES) {
                                if (status == StatusCondition.NONE) {
                                    continue;
                                }
                                CatchResult afflicted = compute(species, level, currentHp, ball, status, generation);
                                assertThat(afflicted.probability())
                                        .as("%s L%d HP%d %s %s %s", species, level, currentHp, ball, status, generation)
                                        .isGreaterThanOrEqualTo(baseline.probability());
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void strongerFixedBallNeverDecreasesProbability() {
        for (Species species : SPECIES) {
            for (GameGeneration generation : GameGeneration.values()) {
                for (StatusCondition status : STATUSES) {
                    for (int level : LEVELS) {
                        for (int currentHp : CURRENT_HPS) {
                            CatchResult poke = compute(species, level, currentHp, PokeBall.POKE, status, generation);
                            CatchResult great = compute(species, level, currentHp, PokeBall.GREAT, status, generation);
                            CatchResult ultra = compute(species, level, currentHp, PokeBall.ULTRA, status, generation);
                            assertThat(great.probability())
                                    .as("great >= poke %s L%d %s %s", species, level, status, generation)
                                    .isGreaterThanOrEqualTo(poke.probability());
                            assertThat(ultra.probability())
                                    .as("ultra >= great %s L%d %s %s", species, level, status, generation)
                                    .isGreaterThanOrEqualTo(great.probability());
                        }
                    }
                }
            }
        }
    }

    @Test
    void activeConditionalBallNeverDecreasesProbability() {
        List<Trigger> triggers = List.of(
                new Trigger(PokeBall.TIMER, StatusCondition.NONE, context(20, false, false, false, false, false),
                        Optional.empty(), Optional.empty()),
                new Trigger(PokeBall.QUICK, StatusCondition.NONE, context(0, true, false, false, false, false),
                        Optional.empty(), Optional.empty()),
                new Trigger(PokeBall.REPEAT, StatusCondition.NONE, context(0, false, true, false, false, false),
                        Optional.empty(), Optional.empty()),
                new Trigger(PokeBall.NET, StatusCondition.NONE, null, Optional.of("bug"), Optional.empty()),
                new Trigger(PokeBall.DIVE, StatusCondition.NONE, context(0, false, false, true, false, false),
                        Optional.empty(), Optional.empty()),
                new Trigger(PokeBall.NEST, StatusCondition.NONE, null, Optional.empty(), Optional.empty()),
                new Trigger(PokeBall.DUSK, StatusCondition.NONE, context(0, false, false, false, false, true),
                        Optional.empty(), Optional.empty()),
                new Trigger(PokeBall.DREAM, StatusCondition.SLEEP, null, Optional.empty(), Optional.empty()));

        for (Species species : SPECIES) {
            for (GameGeneration generation : GameGeneration.values()) {
                for (int level : LEVELS) {
                    for (int currentHp : CURRENT_HPS) {
                        for (Trigger trigger : triggers) {
                            if (!ballRates.isAvailable(trigger.ball(), generation)) {
                                continue;
                            }
                            double active = probability(trigger.ball(), species, level, currentHp, generation,
                                    trigger.status(), trigger.battleContext(), trigger.primaryType(), trigger.secondaryType());
                            double neutral = probability(PokeBall.POKE, species, level, currentHp, generation,
                                    trigger.status(), trigger.battleContext(), trigger.primaryType(), trigger.secondaryType());
                            assertThat(active)
                                    .as("%s active >= neutral %s %s L%d HP%d", trigger.ball(), species, generation, level, currentHp)
                                    .isGreaterThanOrEqualTo(neutral);
                        }
                    }
                }
            }
        }
    }

    @Test
    void nestBallAtLowLevelNeverDecreasesProbability() {
        for (Species species : SPECIES) {
            for (int currentHp : CURRENT_HPS) {
                for (GameGeneration generation : GameGeneration.values()) {
                    double active = probability(PokeBall.NEST, species, 5, currentHp, generation,
                            StatusCondition.NONE, null, Optional.empty(), Optional.empty());
                    double neutral = probability(PokeBall.NEST, species, 50, currentHp, generation,
                            StatusCondition.NONE, null, Optional.empty(), Optional.empty());
                    assertThat(active).as("nest L5 >= nest L50 %s %s", species, generation).isGreaterThanOrEqualTo(neutral);
                }
            }
        }
    }

    @Test
    void sleepAndFreezeAreEquivalentExceptForDreamBall() {
        casesWithoutDreamBall().forEach(caseValue -> {
            CatchResult sleep = compute(caseValue.species(), caseValue.level(), caseValue.currentHp(),
                    caseValue.ball(), StatusCondition.SLEEP, caseValue.generation());
            CatchResult freeze = compute(caseValue.species(), caseValue.level(), caseValue.currentHp(),
                    caseValue.ball(), StatusCondition.FREEZE, caseValue.generation());
            assertThat(freeze.probability()).isEqualTo(sleep.probability());
        });
    }

    @Test
    void dreamBallBoostsOnlySleepAndNotFreeze() {
        Species species = SPECIES.get(0);
        CatchResult sleep = compute(species, 50, 250, PokeBall.DREAM, StatusCondition.SLEEP, GameGeneration.GEN_VI_PLUS);
        CatchResult freeze = compute(species, 50, 250, PokeBall.DREAM, StatusCondition.FREEZE, GameGeneration.GEN_VI_PLUS);

        assertThat(sleep.probability()).isGreaterThan(freeze.probability());
    }

    @Test
    void paralyzePoisonAndBurnAreEquivalent() {
        cases().forEach(caseValue -> {
            CatchResult paralyze = compute(caseValue.species(), caseValue.level(), caseValue.currentHp(),
                    caseValue.ball(), StatusCondition.PARALYZE, caseValue.generation());
            CatchResult poison = compute(caseValue.species(), caseValue.level(), caseValue.currentHp(),
                    caseValue.ball(), StatusCondition.POISON, caseValue.generation());
            CatchResult burn = compute(caseValue.species(), caseValue.level(), caseValue.currentHp(),
                    caseValue.ball(), StatusCondition.BURN, caseValue.generation());
            assertThat(poison.probability()).isEqualTo(paralyze.probability());
            assertThat(burn.probability()).isEqualTo(paralyze.probability());
        });
    }

    private record Trigger(PokeBall ball, StatusCondition status, BattleContext battleContext,
                           Optional<String> primaryType, Optional<String> secondaryType) {
    }

    private static BattleContext context(int turns, boolean firstTurn, boolean alreadyCaught,
                                         boolean fishing, boolean water, boolean caveOrNight) {
        return new BattleContext(turns, firstTurn, alreadyCaught, fishing, water, caveOrNight);
    }

    private double probability(PokeBall ball, Species species, int level, int currentHp,
                               GameGeneration generation, StatusCondition status, BattleContext battleContext,
                               Optional<String> primaryType, Optional<String> secondaryType) {
        return engine.compute(new CatchRequest(
                species.catchRate(), species.baseHp(), level, currentHp, ball, status, generation,
                primaryType, secondaryType, battleContext)).probability();
    }

    private CatchResult compute(Species species, int level, int currentHp,
                                PokeBall ball, StatusCondition status, GameGeneration generation) {
        return engine.compute(new CatchRequest(
                species.catchRate(), species.baseHp(), level, currentHp, ball, status, generation,
                Optional.empty(), Optional.empty(), null));
    }

    private PokeBall[] availableBalls(GameGeneration generation) {
        List<PokeBall> available = new ArrayList<>();
        for (PokeBall ball : PokeBall.values()) {
            if (ballRates.isAvailable(ball, generation)) {
                available.add(ball);
            }
        }
        return available.toArray(new PokeBall[0]);
    }
}
