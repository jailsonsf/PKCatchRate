package com.jailsonsf.pkcatchrate.engine;

import org.springframework.stereotype.Service;

@Service
public class CatchEngine {

    public CatchResult compute(CatchRequest request) {
        if (request.ball().isGuaranteed()) {
            return guaranteedResult();
        }
        int maxHp = maxHp(request.baseHp(), request.level());
        int currentHp = Math.min(request.currentHp(), maxHp);
        int ballTenths = (int) Math.round(request.ball().multiplier() * 10);
        int a = (request.catchRate() * ballTenths / 10) * (3 * maxHp - 2 * currentHp) / (3 * maxHp);
        if (a >= 255) {
            return guaranteedResult();
        }
        int b = 1048560 / integerSqrt(integerSqrt(16711680 / a));
        double probability = Math.pow(b / 65536.0, 4) * 100.0;
        return new CatchResult(probability, false, a, b, 100.0 / probability);
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
