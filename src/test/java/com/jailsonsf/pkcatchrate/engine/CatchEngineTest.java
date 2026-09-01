package com.jailsonsf.pkcatchrate.engine;

import com.jailsonsf.pkcatchrate.ball.PokeBall;
import com.jailsonsf.pkcatchrate.generation.GameGeneration;
import com.jailsonsf.pkcatchrate.status.StatusCondition;
import org.junit.jupiter.api.Test;

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
}
