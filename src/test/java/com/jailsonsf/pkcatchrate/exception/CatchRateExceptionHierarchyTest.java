package com.jailsonsf.pkcatchrate.exception;

import com.jailsonsf.pkcatchrate.ball.PokeBall;
import com.jailsonsf.pkcatchrate.generation.GameGeneration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CatchRateExceptionHierarchyTest {

    @Test
    void allDomainFailuresExtendCatchRateException() {
        assertThat(new UnknownPokemonException("missingno")).isInstanceOf(CatchRateException.class);
        assertThat(new InvalidLevelException(0)).isInstanceOf(CatchRateException.class);
        assertThat(new InvalidHpException(0)).isInstanceOf(CatchRateException.class);
        assertThat(new BallNotAvailableException(PokeBall.DREAM, GameGeneration.GEN_V))
                .isInstanceOf(CatchRateException.class);
    }
}
