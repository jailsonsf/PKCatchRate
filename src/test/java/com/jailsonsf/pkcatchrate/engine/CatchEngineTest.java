package com.jailsonsf.pkcatchrate.engine;

import com.jailsonsf.pkcatchrate.ball.PokeBall;
import com.jailsonsf.pkcatchrate.battle.BattleContext;
import com.jailsonsf.pkcatchrate.generation.GameGeneration;
import com.jailsonsf.pkcatchrate.status.StatusCondition;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class CatchEngineTest {

    private final CatchEngine engine = new CatchEngine();

    @Test
    void pikachuLevel50FullHpPokeballMatchesBulbapediaWorkedExample() {
        CatchResult result = engine.compute(new CatchRequest(190, 35, 50, 110, PokeBall.POKE, StatusCondition.NONE, GameGeneration.GEN_III_IV));

        assertThat(result.guaranteed()).isFalse();
        assertThat(result.a()).isEqualTo(63);
        assertThat(result.b()).isEqualTo(47661);
        assertThat(result.probability()).isCloseTo(27.972603, within(0.001));
        assertThat(result.expectedBalls()).isCloseTo(3.5749, within(0.01));
    }

    @Test
    void pikachuLevel50FullHpGreatBallScalesUpCatchValue() {
        CatchResult result = engine.compute(new CatchRequest(190, 35, 50, 110, PokeBall.GREAT, StatusCondition.NONE, GameGeneration.GEN_III_IV));

        assertThat(result.a()).isEqualTo(95);
        assertThat(result.b()).isEqualTo(52428);
        assertThat(result.probability()).isCloseTo(40.957500, within(0.001));
    }

    @Test
    void mewtwoLevel70FullHpUltraBallHasTinyChance() {
        CatchResult result = engine.compute(new CatchRequest(3, 106, 70, 250, PokeBall.ULTRA, StatusCondition.NONE, GameGeneration.GEN_III_IV));

        assertThat(result.a()).isEqualTo(2);
        assertThat(result.b()).isEqualTo(19784);
        assertThat(result.probability()).isCloseTo(0.830494, within(0.001));
    }

    @Test
    void masterBallIsAlwaysGuaranteed() {
        CatchResult result = engine.compute(new CatchRequest(3, 106, 70, 250, PokeBall.MASTER, StatusCondition.NONE, GameGeneration.GEN_III_IV));

        assertThat(result.guaranteed()).isTrue();
        assertThat(result.probability()).isEqualTo(100.0);
        assertThat(result.a()).isEqualTo(255);
        assertThat(result.b()).isEqualTo(65535);
    }

    @Test
    void pikachuAsleepInGen34DoublesCatchValue() {
        CatchResult result = engine.compute(new CatchRequest(190, 35, 50, 110, PokeBall.POKE, StatusCondition.SLEEP, GameGeneration.GEN_III_IV));

        assertThat(result.guaranteed()).isFalse();
        assertThat(result.a()).isEqualTo(126);
        assertThat(result.b()).isEqualTo(55187);
        assertThat(result.probability()).isCloseTo(50.283723, within(0.001));
        assertThat(result.expectedBalls()).isCloseTo(1.9887, within(0.01));
    }

    @Test
    void pikachuParalyzedInGen34BoostsCatchValueByHalf() {
        CatchResult result = engine.compute(new CatchRequest(190, 35, 50, 110, PokeBall.POKE, StatusCondition.PARALYZE, GameGeneration.GEN_III_IV));

        assertThat(result.a()).isEqualTo(94);
        assertThat(result.b()).isEqualTo(52428);
        assertThat(result.probability()).isCloseTo(40.957500, within(0.001));
    }

    @Test
    void gen5PikachuPokeballFullHpMatchesGen5Formula() {
        CatchResult result = engine.compute(new CatchRequest(190, 35, 50, 110, PokeBall.POKE, StatusCondition.NONE, GameGeneration.GEN_V));

        assertThat(result.guaranteed()).isFalse();
        assertThat(result.a()).isEqualTo(63);
        assertThat(result.b()).isEqualTo(46265);
        assertThat(result.probability()).isCloseTo(35.181788, within(0.001));
    }

    @Test
    void gen5SleepMultiplierIsTwoAndHalf() {
        CatchResult result = engine.compute(new CatchRequest(190, 35, 50, 110, PokeBall.POKE, StatusCondition.SLEEP, GameGeneration.GEN_V));

        assertThat(result.guaranteed()).isFalse();
        assertThat(result.a()).isEqualTo(158);
        assertThat(result.b()).isEqualTo(58175);
        assertThat(result.probability()).isCloseTo(69.947047, within(0.001));
    }

    @Test
    void gen5MewtwoParalyzedUltraBall() {
        CatchResult result = engine.compute(new CatchRequest(3, 106, 70, 250, PokeBall.ULTRA, StatusCondition.PARALYZE, GameGeneration.GEN_V));

        assertThat(result.a()).isEqualTo(3);
        assertThat(result.b()).isEqualTo(21583);
        assertThat(result.probability()).isCloseTo(3.571870, within(0.001));
    }

    @Test
    void gen6PlusPikachuPokeballFullHpMatchesGen6PlusFormula() {
        CatchResult result = engine.compute(new CatchRequest(190, 35, 50, 110, PokeBall.POKE, StatusCondition.NONE, GameGeneration.GEN_VI_PLUS));

        assertThat(result.guaranteed()).isFalse();
        assertThat(result.a()).isEqualTo(63);
        assertThat(result.b()).isEqualTo(50473);
        assertThat(result.probability()).isCloseTo(35.181695, within(0.001));
    }

    @Test
    void gen6PlusPikachuAsleepMatchesGen6PlusFormula() {
        CatchResult result = engine.compute(new CatchRequest(190, 35, 50, 110, PokeBall.POKE, StatusCondition.SLEEP, GameGeneration.GEN_VI_PLUS));

        assertThat(result.guaranteed()).isFalse();
        assertThat(result.a()).isEqualTo(158);
        assertThat(result.b()).isEqualTo(59934);
        assertThat(result.probability()).isCloseTo(69.947683, within(0.001));
    }

    @Test
    void gen6PlusMewtwoUltraBallHasTinyChance() {
        CatchResult result = engine.compute(new CatchRequest(3, 106, 70, 250, PokeBall.ULTRA, StatusCondition.NONE, GameGeneration.GEN_VI_PLUS));

        assertThat(result.a()).isEqualTo(2);
        assertThat(result.b()).isEqualTo(26405);
        assertThat(result.probability()).isCloseTo(2.635269, within(0.001));
    }

    @Test
    void gen5GreatBallMatchesGreatBallMultiplier() {
        CatchResult result = engine.compute(new CatchRequest(190, 35, 50, 110, PokeBall.GREAT, StatusCondition.NONE, GameGeneration.GEN_V));

        assertThat(result.a()).isEqualTo(95);
        assertThat(result.b()).isEqualTo(51200);
        assertThat(result.probability()).isCloseTo(47.683716, within(0.001));
    }

    @Test
    void gen6PlusGreatBallMatchesGreatBallMultiplier() {
        CatchResult result = engine.compute(new CatchRequest(190, 35, 50, 110, PokeBall.GREAT, StatusCondition.NONE, GameGeneration.GEN_VI_PLUS));

        assertThat(result.a()).isEqualTo(95);
        assertThat(result.b()).isEqualTo(54459);
        assertThat(result.probability()).isCloseTo(47.682490, within(0.001));
    }

    @Test
    void catchValueReaching255IsGuaranteedAcrossGenerations() {
        CatchResult gen34 = engine.compute(new CatchRequest(190, 35, 50, 109, PokeBall.ULTRA, StatusCondition.SLEEP, GameGeneration.GEN_III_IV));
        CatchResult gen5 = engine.compute(new CatchRequest(255, 30, 50, 1, PokeBall.ULTRA, StatusCondition.NONE, GameGeneration.GEN_V));

        assertThat(gen34.guaranteed()).isTrue();
        assertThat(gen34.probability()).isEqualTo(100.0);
        assertThat(gen34.a()).isEqualTo(255);
        assertThat(gen34.b()).isEqualTo(65535);

        assertThat(gen5.guaranteed()).isTrue();
        assertThat(gen5.probability()).isEqualTo(100.0);
        assertThat(gen5.a()).isEqualTo(255);
        assertThat(gen5.b()).isEqualTo(65535);
    }

    @Test
    void quickBallOnFirstTurnInGenVBoostsToFiveTimes() {
        CatchResult result = engine.compute(new CatchRequest(
                3, 106, 70, 250, PokeBall.QUICK, StatusCondition.NONE, GameGeneration.GEN_V,
                Optional.empty(), Optional.empty(),
                new BattleContext(0, true, false, false, false, false)));

        assertThat(result.guaranteed()).isFalse();
        assertThat(result.a()).isEqualTo(5);
        assertThat(result.b()).isEqualTo(24523);
        assertThat(result.probability()).isCloseTo(5.239393, within(0.001));
    }

    @Test
    void quickBallFirstTurnMultiplierDiffersByGenerationBucket() {
        CatchResult gen34 = engine.compute(new CatchRequest(
                3, 106, 70, 250, PokeBall.QUICK, StatusCondition.NONE, GameGeneration.GEN_III_IV,
                Optional.empty(), Optional.empty(),
                new BattleContext(0, true, false, false, false, false)));
        CatchResult gen6 = engine.compute(new CatchRequest(
                3, 106, 70, 250, PokeBall.QUICK, StatusCondition.NONE, GameGeneration.GEN_VI_PLUS,
                Optional.empty(), Optional.empty(),
                new BattleContext(0, true, false, false, false, false)));

        assertThat(gen34.a()).isEqualTo(4);
        assertThat(gen34.b()).isEqualTo(23301);
        assertThat(gen6.a()).isEqualTo(5);
        assertThat(gen6.b()).isEqualTo(31355);
        assertThat(gen6.probability()).isCloseTo(5.239713, within(0.001));
    }

    @Test
    void quickBallAfterFirstTurnStaysNeutral() {
        CatchResult result = engine.compute(new CatchRequest(
                3, 106, 70, 250, PokeBall.QUICK, StatusCondition.NONE, GameGeneration.GEN_V,
                Optional.empty(), Optional.empty(),
                new BattleContext(5, false, false, false, false, false)));

        assertThat(result.a()).isEqualTo(1);
        assertThat(result.b()).isEqualTo(16400);
        assertThat(result.probability()).isCloseTo(1.567082, within(0.001));
    }

    @Test
    void duskBallInCaveWithSleepingTargetInGen6Plus() {
        CatchResult result = engine.compute(new CatchRequest(
                3, 106, 70, 250, PokeBall.DUSK, StatusCondition.SLEEP, GameGeneration.GEN_VI_PLUS,
                Optional.empty(), Optional.empty(),
                new BattleContext(0, false, false, false, false, true)));

        assertThat(result.a()).isEqualTo(7);
        assertThat(result.b()).isEqualTo(33831);
        assertThat(result.probability()).isCloseTo(7.101329, within(0.001));
    }

    @Test
    void netBallOnBugTargetInGen6PlusBoostsCatchValue() {
        CatchResult result = engine.compute(new CatchRequest(
                190, 35, 50, 110, PokeBall.NET, StatusCondition.NONE, GameGeneration.GEN_VI_PLUS,
                Optional.of("bug"), Optional.of("grass"), null));

        assertThat(result.a()).isEqualTo(221);
        assertThat(result.b()).isEqualTo(63836);
        assertThat(result.probability()).isCloseTo(90.020815, within(0.001));
    }

    @Test
    void timerBallCapsAtFourTimesOnTurnThirtyInGen34() {
        CatchResult turn29 = engine.compute(new CatchRequest(
                3, 106, 70, 250, PokeBall.TIMER, StatusCondition.NONE, GameGeneration.GEN_III_IV,
                Optional.empty(), Optional.empty(),
                new BattleContext(29, false, false, false, false, false)));
        CatchResult turn30 = engine.compute(new CatchRequest(
                3, 106, 70, 250, PokeBall.TIMER, StatusCondition.NONE, GameGeneration.GEN_III_IV,
                Optional.empty(), Optional.empty(),
                new BattleContext(30, false, false, false, false, false)));

        assertThat(turn29.a()).isEqualTo(3);
        assertThat(turn29.b()).isEqualTo(21845);
        assertThat(turn30.a()).isEqualTo(4);
        assertThat(turn30.b()).isEqualTo(23301);
    }

    @Test
    void timerBallCapsAtFourTimesOnTurnTenInGenFive() {
        CatchResult turn9 = engine.compute(new CatchRequest(
                3, 106, 70, 250, PokeBall.TIMER, StatusCondition.NONE, GameGeneration.GEN_V,
                Optional.empty(), Optional.empty(),
                new BattleContext(9, false, false, false, false, false)));
        CatchResult turn10 = engine.compute(new CatchRequest(
                3, 106, 70, 250, PokeBall.TIMER, StatusCondition.NONE, GameGeneration.GEN_V,
                Optional.empty(), Optional.empty(),
                new BattleContext(10, false, false, false, false, false)));
        CatchResult turn100 = engine.compute(new CatchRequest(
                3, 106, 70, 250, PokeBall.TIMER, StatusCondition.NONE, GameGeneration.GEN_V,
                Optional.empty(), Optional.empty(),
                new BattleContext(100, false, false, false, false, false)));

        assertThat(turn9.a()).isEqualTo(3);
        assertThat(turn9.b()).isEqualTo(22746);
        assertThat(turn10.a()).isEqualTo(4);
        assertThat(turn10.b()).isEqualTo(23193);
        assertThat(turn100.a()).isEqualTo(turn10.a());
        assertThat(turn100.b()).isEqualTo(turn10.b());
    }

    @Test
    void repeatBallOnAlreadyCaughtTargetInGen6Plus() {
        CatchResult result = engine.compute(new CatchRequest(
                3, 106, 70, 250, PokeBall.REPEAT, StatusCondition.NONE, GameGeneration.GEN_VI_PLUS,
                Optional.empty(), Optional.empty(),
                new BattleContext(0, false, true, false, false, false)));

        assertThat(result.a()).isEqualTo(3);
        assertThat(result.b()).isEqualTo(29326);
        assertThat(result.probability()).isCloseTo(4.009513, within(0.001));
    }

    @Test
    void dreamBallOnAsleepTargetInGen6PlusIsNotGuaranteedButStrong() {
        CatchResult result = engine.compute(new CatchRequest(
                3, 106, 70, 250, PokeBall.DREAM, StatusCondition.SLEEP, GameGeneration.GEN_VI_PLUS,
                Optional.empty(), Optional.empty(), null));

        assertThat(result.a()).isEqualTo(10);
        assertThat(result.b()).isEqualTo(35707);
        assertThat(result.probability()).isCloseTo(8.812390, within(0.001));
    }

    @Test
    void conditionalBallWithoutBattleContextFallsBackToNeutral() {
        CatchResult dusk = engine.compute(new CatchRequest(
                3, 106, 70, 250, PokeBall.DUSK, StatusCondition.NONE, GameGeneration.GEN_V,
                Optional.empty(), Optional.empty(), null));
        CatchResult quick = engine.compute(new CatchRequest(
                3, 106, 70, 250, PokeBall.QUICK, StatusCondition.NONE, GameGeneration.GEN_V,
                Optional.empty(), Optional.empty(), null));

        assertThat(dusk.a()).isEqualTo(1);
        assertThat(quick.a()).isEqualTo(1);
        assertThat(dusk.probability()).isEqualTo(quick.probability());
    }

    @Test
    void dreamBallIsNotAvailableBeforeGen6Plus() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> engine.compute(new CatchRequest(
                3, 106, 70, 250, PokeBall.DREAM, StatusCondition.SLEEP, GameGeneration.GEN_V,
                Optional.empty(), Optional.empty(), null)))
                .isInstanceOf(BallNotAvailableException.class);
    }

    @Test
    void lureBallIsNeverAvailable() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> engine.compute(new CatchRequest(
                3, 106, 70, 250, PokeBall.LURE, StatusCondition.NONE, GameGeneration.GEN_VI_PLUS,
                Optional.empty(), Optional.empty(), null)))
                .isInstanceOf(BallNotAvailableException.class);
    }

    @Test
    void masterBallRemainsGuaranteedInEveryGeneration() {
        for (GameGeneration generation : GameGeneration.values()) {
            CatchResult result = engine.compute(new CatchRequest(
                    3, 106, 70, 250, PokeBall.MASTER, StatusCondition.NONE, generation,
                    Optional.empty(), Optional.empty(), null));
            assertThat(result.guaranteed()).isTrue();
            assertThat(result.probability()).isEqualTo(100.0);
        }
    }

    @Test
    void nestBallBoostsLowLevelTargetsMoreThanHighLevelInGen34() {
        CatchResult low = engine.compute(new CatchRequest(
                190, 35, 5, 20, PokeBall.NEST, StatusCondition.NONE, GameGeneration.GEN_III_IV,
                Optional.empty(), Optional.empty(), null));
        CatchResult high = engine.compute(new CatchRequest(
                190, 35, 100, 211, PokeBall.NEST, StatusCondition.NONE, GameGeneration.GEN_III_IV,
                Optional.empty(), Optional.empty(), null));

        assertThat(low.a()).isEqualTo(221);
        assertThat(low.b()).isEqualTo(65535);
        assertThat(high.a()).isEqualTo(63);
        assertThat(high.b()).isEqualTo(47661);
        assertThat(low.probability()).isGreaterThan(high.probability());
    }
}
