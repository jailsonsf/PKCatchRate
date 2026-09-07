package com.jailsonsf.pkcatchrate.engine;

import com.jailsonsf.pkcatchrate.ball.PokeBallRates;
import com.jailsonsf.pkcatchrate.exception.BallNotAvailableException;
import com.jailsonsf.pkcatchrate.exception.InvalidHpException;
import com.jailsonsf.pkcatchrate.exception.InvalidLevelException;
import com.jailsonsf.pkcatchrate.status.StatusCondition;
import org.springframework.stereotype.Service;

@Service
public class CatchEngine {

    private final PokeBallRates ballRates = new PokeBallRates();

    public CatchResult compute(CatchRequest request) {
        validate(request);
        if (!ballRates.isAvailable(request.ball(), request.generation())) {
            throw new BallNotAvailableException(request.ball(), request.generation());
        }
        if (request.ball().isGuaranteed()) {
            return guaranteedResult();
        }
        double multiplier = ballRates.multiplier(request.ball(), request.generation(), request.battleContext(),
                request.level(), request.status(), request.primaryType(), request.secondaryType());
        return switch (request.generation()) {
            case GEN_III_IV -> computeGen34(request, multiplier);
            case GEN_V -> computeGen56(request, multiplier, 1.0 / 4.0, 3);
            case GEN_VI_PLUS -> computeGen56(request, multiplier, 3.0 / 16.0, 4);
        };
    }

    private static void validate(CatchRequest request) {
        if (request.level() < 1 || request.level() > 100) {
            throw new InvalidLevelException(request.level());
        }
        if (request.currentHp() < 1) {
            throw new InvalidHpException(request.currentHp());
        }
    }

    private static CatchResult computeGen34(CatchRequest request, double multiplier) {
        int maxHp = maxHp(request.baseHp(), request.level());
        int currentHp = Math.min(request.currentHp(), maxHp);
        int ballTenths = (int) Math.round(multiplier * 10);
        int a = (request.catchRate() * ballTenths / 10) * (3 * maxHp - 2 * currentHp) / (3 * maxHp);
        a = applyGen34Status(a, request.status());
        if (a >= 255) {
            return guaranteedResult();
        }
        int b = 1048560 / integerSqrt(integerSqrt(16711680 / a));
        double probability = Math.pow(b / 65536.0, 4) * 100.0;
        return new CatchResult(probability, false, a, b, 100.0 / probability);
    }

    private static CatchResult computeGen56(CatchRequest request, double multiplier, double exponent, int shakeCount) {
        int maxHp = maxHp(request.baseHp(), request.level());
        int currentHp = Math.min(request.currentHp(), maxHp);
        long ballFixed = Math.round(multiplier * 4096);
        long r1 = (3L * maxHp - 2L * currentHp) * request.catchRate() * ballFixed
                / (3L * maxHp);
        long aScaled = r1 * statusFixed(request.status()) / 4096;
        if (aScaled >= 1044480L) {
            return guaranteedResult();
        }
        int a = (int) (aScaled / 4096);
        int b = (int) Math.floor(65536.0 * Math.pow(aScaled / 1044480.0, exponent));
        double probability = Math.pow(b / 65536.0, shakeCount) * 100.0;
        return new CatchResult(probability, false, a, b, 100.0 / probability);
    }

    private static long statusFixed(StatusCondition status) {
        return switch (status) {
            case SLEEP, FREEZE -> 10240;
            case PARALYZE, POISON, BURN -> 6144;
            case NONE -> 4096;
        };
    }

    private static int applyGen34Status(int a, StatusCondition status) {
        return switch (status) {
            case SLEEP, FREEZE -> a * 2;
            case PARALYZE, POISON, BURN -> a * 15 / 10;
            case NONE -> a;
        };
    }

    private static int maxHp(int baseHp, int level) {
        return (2 * baseHp + 131) * level / 100 + 10;
    }

    private static int integerSqrt(int value) {
        int guess = (int) Math.sqrt(value);
        while (guess > 0 && guess * guess > value) {
            guess--;
        }
        while ((guess + 1) * (guess + 1) <= value) {
            guess++;
        }
        return guess;
    }

    private static CatchResult guaranteedResult() {
        return new CatchResult(100.0, true, 255, 65535, 1.0);
    }
}
