package com.jailsonsf.pkcatchrate.ball;

import com.jailsonsf.pkcatchrate.battle.BattleContext;
import com.jailsonsf.pkcatchrate.generation.GameGeneration;
import com.jailsonsf.pkcatchrate.status.StatusCondition;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class PokeBallRatesTest {

    private final PokeBallRates rates = new PokeBallRates();

    private static final List<PokeBall> AVAILABLE_EVERYWHERE = List.of(
            PokeBall.POKE, PokeBall.GREAT, PokeBall.ULTRA, PokeBall.MASTER,
            PokeBall.TIMER, PokeBall.QUICK, PokeBall.REPEAT, PokeBall.NET,
            PokeBall.DIVE, PokeBall.NEST, PokeBall.DUSK);

    private static final List<PokeBall> EXCLUDED = List.of(
            PokeBall.LURE, PokeBall.BEAST, PokeBall.HEAVY, PokeBall.FAST,
            PokeBall.LEVEL, PokeBall.LOVE, PokeBall.MOON);

    private static BattleContext context(int turns, boolean firstTurn, boolean alreadyCaught,
                                         boolean fishing, boolean water, boolean caveOrNight) {
        return new BattleContext(turns, firstTurn, alreadyCaught, fishing, water, caveOrNight);
    }

    @Test
    void fixedAndContextualBallsAreAvailableInEveryGeneration() {
        for (PokeBall ball : AVAILABLE_EVERYWHERE) {
            for (GameGeneration generation : GameGeneration.values()) {
                assertThat(rates.isAvailable(ball, generation))
                        .as("%s in %s", ball, generation).isTrue();
            }
        }
    }

    @Test
    void dreamBallIsOnlyAvailableInGenSixPlus() {
        assertThat(rates.isAvailable(PokeBall.DREAM, GameGeneration.GEN_III_IV)).isFalse();
        assertThat(rates.isAvailable(PokeBall.DREAM, GameGeneration.GEN_V)).isFalse();
        assertThat(rates.isAvailable(PokeBall.DREAM, GameGeneration.GEN_VI_PLUS)).isTrue();
    }

    @Test
    void excludedBallsAreNeverAvailable() {
        for (PokeBall ball : EXCLUDED) {
            for (GameGeneration generation : GameGeneration.values()) {
                assertThat(rates.isAvailable(ball, generation))
                        .as("%s in %s", ball, generation).isFalse();
            }
        }
    }

    @Test
    void fixedBallsKeepTheirNeutralMultipliers() {
        assertThat(fixed(PokeBall.POKE, GameGeneration.GEN_III_IV)).isCloseTo(1.0, within(1e-9));
        assertThat(fixed(PokeBall.GREAT, GameGeneration.GEN_V)).isCloseTo(1.5, within(1e-9));
        assertThat(fixed(PokeBall.ULTRA, GameGeneration.GEN_VI_PLUS)).isCloseTo(2.0, within(1e-9));
    }

    @Test
    void timerGen34RisesByTenthPerTurnAndCapsAtFourTimesFromTurnThirty() {
        assertThat(timer(GameGeneration.GEN_III_IV, 0)).isCloseTo(1.0, within(1e-9));
        assertThat(timer(GameGeneration.GEN_III_IV, 10)).isCloseTo(2.0, within(1e-9));
        assertThat(timer(GameGeneration.GEN_III_IV, 29)).isCloseTo(3.9, within(1e-9));
        assertThat(timer(GameGeneration.GEN_III_IV, 30)).isCloseTo(4.0, within(1e-9));
        assertThat(timer(GameGeneration.GEN_III_IV, 100)).isCloseTo(4.0, within(1e-9));
    }

    @Test
    void timerGenFiveUsesTwelveTwentyNinethsScalingAndCapsAtTenTurns() {
        assertThat(timer(GameGeneration.GEN_V, 0)).isCloseTo(1.0, within(1e-9));
        assertThat(timer(GameGeneration.GEN_V, 1)).isCloseTo(5325.0 / 4096.0, within(1e-9));
        assertThat(timer(GameGeneration.GEN_V, 9)).isCloseTo(15157.0 / 4096.0, within(1e-9));
        assertThat(timer(GameGeneration.GEN_V, 10)).isCloseTo(4.0, within(1e-9));
        assertThat(timer(GameGeneration.GEN_V, 100)).isCloseTo(4.0, within(1e-9));
        assertThat(timer(GameGeneration.GEN_VI_PLUS, 9)).isCloseTo(15157.0 / 4096.0, within(1e-9));
    }

    @Test
    void timerWithNoBattleContextIsNeutral() {
        assertThat(rates.multiplier(PokeBall.TIMER, GameGeneration.GEN_V, null, 50,
                StatusCondition.NONE, Optional.empty(), Optional.empty())).isCloseTo(1.0, within(1e-9));
    }

    @Test
    void quickBallBoostsOnlyOnFirstTurnWithGenSpecificMultiplier() {
        assertThat(quick(GameGeneration.GEN_III_IV, true)).isCloseTo(4.0, within(1e-9));
        assertThat(quick(GameGeneration.GEN_V, true)).isCloseTo(5.0, within(1e-9));
        assertThat(quick(GameGeneration.GEN_VI_PLUS, true)).isCloseTo(5.0, within(1e-9));
        assertThat(quick(GameGeneration.GEN_V, false)).isCloseTo(1.0, within(1e-9));
    }

    @Test
    void quickBallWithNoBattleContextIsNeutral() {
        assertThat(rates.multiplier(PokeBall.QUICK, GameGeneration.GEN_V, null, 50,
                StatusCondition.NONE, Optional.empty(), Optional.empty())).isCloseTo(1.0, within(1e-9));
    }

    @Test
    void repeatBallBoostsOnlyWhenAlreadyCaughtWithGenSplit() {
        assertThat(repeat(GameGeneration.GEN_III_IV, true)).isCloseTo(3.0, within(1e-9));
        assertThat(repeat(GameGeneration.GEN_V, true)).isCloseTo(3.0, within(1e-9));
        assertThat(repeat(GameGeneration.GEN_VI_PLUS, true)).isCloseTo(3.5, within(1e-9));
        assertThat(repeat(GameGeneration.GEN_VI_PLUS, false)).isCloseTo(1.0, within(1e-9));
        assertThat(rates.multiplier(PokeBall.REPEAT, GameGeneration.GEN_VI_PLUS, null, 50,
                StatusCondition.NONE, Optional.empty(), Optional.empty())).isCloseTo(1.0, within(1e-9));
    }

    @Test
    void netBallBoostsAgainstBugOrWaterWithGenSplit() {
        assertThat(net(GameGeneration.GEN_III_IV, "bug", "grass")).isCloseTo(3.0, within(1e-9));
        assertThat(net(GameGeneration.GEN_V, "normal", "water")).isCloseTo(3.0, within(1e-9));
        assertThat(net(GameGeneration.GEN_VI_PLUS, "water", null)).isCloseTo(3.5, within(1e-9));
        assertThat(net(GameGeneration.GEN_VI_PLUS, "normal", "flying")).isCloseTo(1.0, within(1e-9));
        assertThat(net(GameGeneration.GEN_VI_PLUS, null, null)).isCloseTo(1.0, within(1e-9));
    }

    @Test
    void diveBallBoostsOnWaterOrFromFishingAcrossGenerations() {
        assertThat(dive(true, false)).isCloseTo(3.5, within(1e-9));
        assertThat(dive(false, true)).isCloseTo(3.5, within(1e-9));
        assertThat(dive(false, false)).isCloseTo(1.0, within(1e-9));
        assertThat(rates.multiplier(PokeBall.DIVE, GameGeneration.GEN_VI_PLUS, null, 50,
                StatusCondition.NONE, Optional.empty(), Optional.empty())).isCloseTo(1.0, within(1e-9));
    }

    @Test
    void nestBallGen34FallsWithLevelAndNeverDropsBelowOne() {
        assertThat(nest(GameGeneration.GEN_III_IV, 1)).isCloseTo(3.9, within(1e-9));
        assertThat(nest(GameGeneration.GEN_III_IV, 5)).isCloseTo(3.5, within(1e-9));
        assertThat(nest(GameGeneration.GEN_III_IV, 30)).isCloseTo(1.0, within(1e-9));
        assertThat(nest(GameGeneration.GEN_III_IV, 100)).isCloseTo(1.0, within(1e-9));
    }

    @Test
    void nestBallGenFiveUsesFortyOneScalingAndStaysAboveOneThroughLevelThirty() {
        assertThat(nest(GameGeneration.GEN_V, 1)).isCloseTo(16384.0 / 4096.0, within(1e-9));
        assertThat(nest(GameGeneration.GEN_V, 5)).isCloseTo(14745.0 / 4096.0, within(1e-9));
        assertThat(nest(GameGeneration.GEN_V, 10)).isCloseTo(12697.0 / 4096.0, within(1e-9));
        assertThat(nest(GameGeneration.GEN_V, 30)).isCloseTo(4505.0 / 4096.0, within(1e-9));
        assertThat(nest(GameGeneration.GEN_V, 31)).isCloseTo(1.0, within(1e-9));
    }

    @Test
    void nestBallGenSixPlusDropsToOneAtLevelThirty() {
        assertThat(nest(GameGeneration.GEN_VI_PLUS, 5)).isCloseTo(14745.0 / 4096.0, within(1e-9));
        assertThat(nest(GameGeneration.GEN_VI_PLUS, 29)).isCloseTo(4915.0 / 4096.0, within(1e-9));
        assertThat(nest(GameGeneration.GEN_VI_PLUS, 30)).isCloseTo(1.0, within(1e-9));
        assertThat(nest(GameGeneration.GEN_VI_PLUS, 100)).isCloseTo(1.0, within(1e-9));
    }

    @Test
    void duskBallBoostsInCaveOrNightWithGenSplit() {
        assertThat(dusk(GameGeneration.GEN_III_IV, true)).isCloseTo(3.5, within(1e-9));
        assertThat(dusk(GameGeneration.GEN_V, true)).isCloseTo(3.5, within(1e-9));
        assertThat(dusk(GameGeneration.GEN_VI_PLUS, true)).isCloseTo(3.0, within(1e-9));
        assertThat(dusk(GameGeneration.GEN_VI_PLUS, false)).isCloseTo(1.0, within(1e-9));
        assertThat(rates.multiplier(PokeBall.DUSK, GameGeneration.GEN_VI_PLUS, null, 50,
                StatusCondition.NONE, Optional.empty(), Optional.empty())).isCloseTo(1.0, within(1e-9));
    }

    @Test
    void dreamBallMultipliesOnlyOnSleepingTargetsInGenSixPlus() {
        assertThat(dream(StatusCondition.SLEEP)).isCloseTo(4.0, within(1e-9));
        assertThat(dream(StatusCondition.PARALYZE)).isCloseTo(1.0, within(1e-9));
        assertThat(dream(StatusCondition.NONE)).isCloseTo(1.0, within(1e-9));
    }

    private double timer(GameGeneration generation, int turns) {
        return multiplier(PokeBall.TIMER, generation, context(turns, false, false, false, false, false));
    }

    private double quick(GameGeneration generation, boolean firstTurn) {
        return multiplier(PokeBall.QUICK, generation, context(0, firstTurn, false, false, false, false));
    }

    private double repeat(GameGeneration generation, boolean alreadyCaught) {
        return multiplier(PokeBall.REPEAT, generation, context(0, false, alreadyCaught, false, false, false));
    }

    private double dive(boolean water, boolean fishing) {
        return multiplier(PokeBall.DIVE, GameGeneration.GEN_VI_PLUS, context(0, false, false, fishing, water, false));
    }

    private double dusk(GameGeneration generation, boolean caveOrNight) {
        return multiplier(PokeBall.DUSK, generation, context(0, false, false, false, false, caveOrNight));
    }

    private double nest(GameGeneration generation, int level) {
        return multiplier(PokeBall.NEST, generation, null, level);
    }

    private double dream(StatusCondition status) {
        return multiplier(PokeBall.DREAM, GameGeneration.GEN_VI_PLUS, null, 50, status);
    }

    private double net(GameGeneration generation, String primary, String secondary) {
        return multiplier(PokeBall.NET, generation, null, 50, primary, secondary);
    }

    private double fixed(PokeBall ball, GameGeneration generation) {
        return multiplier(ball, generation, null);
    }

    private double multiplier(PokeBall ball, GameGeneration generation, BattleContext battleContext) {
        return multiplier(ball, generation, battleContext, 50, StatusCondition.NONE);
    }

    private double multiplier(PokeBall ball, GameGeneration generation, BattleContext battleContext,
                              int level, StatusCondition status) {
        return multiplier(ball, generation, battleContext, level, status, null, null);
    }

    private double multiplier(PokeBall ball, GameGeneration generation, BattleContext battleContext, int level) {
        return multiplier(ball, generation, battleContext, level, StatusCondition.NONE);
    }

    private double multiplier(PokeBall ball, GameGeneration generation, BattleContext battleContext,
                              int level, String primaryType, String secondaryType) {
        return multiplier(ball, generation, battleContext, level, StatusCondition.NONE, primaryType, secondaryType);
    }

    private double multiplier(PokeBall ball, GameGeneration generation, BattleContext battleContext,
                              int level, StatusCondition status, String primaryType, String secondaryType) {
        return rates.multiplier(ball, generation, battleContext, level, status,
                Optional.ofNullable(primaryType), Optional.ofNullable(secondaryType));
    }
}
