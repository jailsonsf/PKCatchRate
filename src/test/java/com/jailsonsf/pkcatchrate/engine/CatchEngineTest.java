package com.jailsonsf.pkcatchrate.engine;

import com.jailsonsf.pkcatchrate.ball.PokeBall;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class CatchEngineTest {

    private final CatchEngine engine = new CatchEngine();

    @Test
    void pikachuLevel50FullHpPokeballMatchesBulbapediaWorkedExample() {
        CatchResult result = engine.compute(new CatchRequest(190, 35, 50, 110, PokeBall.POKE));

        assertThat(result.guaranteed()).isFalse();
        assertThat(result.a()).isEqualTo(63);
        assertThat(result.b()).isEqualTo(47661);
        assertThat(result.probability()).isCloseTo(27.972603, within(0.001));
        assertThat(result.expectedBalls()).isCloseTo(3.5749, within(0.01));
    }

    @Test
    void pikachuLevel50FullHpGreatBallScalesUpCatchValue() {
        CatchResult result = engine.compute(new CatchRequest(190, 35, 50, 110, PokeBall.GREAT));

        assertThat(result.a()).isEqualTo(95);
        assertThat(result.b()).isEqualTo(52428);
        assertThat(result.probability()).isCloseTo(40.957500, within(0.001));
    }

    @Test
    void mewtwoLevel70FullHpUltraBallHasTinyChance() {
        CatchResult result = engine.compute(new CatchRequest(3, 106, 70, 250, PokeBall.ULTRA));

        assertThat(result.a()).isEqualTo(2);
        assertThat(result.b()).isEqualTo(19784);
        assertThat(result.probability()).isCloseTo(0.830494, within(0.001));
    }

    @Test
    void masterBallIsAlwaysGuaranteed() {
        CatchResult result = engine.compute(new CatchRequest(3, 106, 70, 250, PokeBall.MASTER));

        assertThat(result.guaranteed()).isTrue();
        assertThat(result.probability()).isEqualTo(100.0);
        assertThat(result.a()).isEqualTo(255);
        assertThat(result.b()).isEqualTo(65535);
    }
}
