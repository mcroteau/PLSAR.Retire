package plsar.model;

import java.math.BigDecimal;

public class Workout {
    Long id;
    Long userId;

    BigDecimal duration;
    BigDecimal intervals;
    BigDecimal workoutSets;

    Integer breakDuration;
    Integer startTime;
    Integer finishTime;

    BigDecimal pushups;
    BigDecimal pullups;
    BigDecimal situps;

    BigDecimal sigmaPushups;
    BigDecimal sigmaPullups;
    BigDecimal sigmaSitups;

    BigDecimal sigmaPushupsRemaining;
    BigDecimal sigmaPullupsRemaining;
    BigDecimal sigmaSitupsRemaining;

    BigDecimal goalPushups;
    BigDecimal goalPullups;
    BigDecimal goalSitups;

    BigDecimal sigmaSetPushups;
    BigDecimal sigmaSetPullups;
    BigDecimal sigmaSetSitups;

    BigDecimal pushupsSecond;
    BigDecimal pullupsSecond;
    BigDecimal situpsSecond;

    BigDecimal setCountdownRemaining;
    BigDecimal durationRemaining;
    BigDecimal intervalsRemaining;

    boolean paused;
    boolean breakSession;

    BigDecimal percentComplete;
    BigDecimal percentCompleteSigma;

    BigDecimal setCountdown;


}
