package net.talpidae.raffle.app.api;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Value;


@Value
public class QuizStats
{
    private static final QuizStats EMPTY = new QuizStats(null, null, null);


    /**
     * How many points were reached.
     */
    private final Integer score;


    /**
     * Maximum number of obtainable points.
     */
    private final Integer scoreMax;


    /**
     * Score time modifier.
     */
    private final Double scoreTimeModifier;


    public static QuizStats emptyStats()
    {
        return EMPTY;
    }


    @Builder
    @JsonCreator
    public QuizStats(@JsonProperty("points") Integer score,
                     @JsonProperty("scoreMax") Integer scoreMax,
                     @JsonProperty("scoreTimeModifier") Double scoreTimeModifier)
    {
        this.score = score;
        this.scoreMax = scoreMax;
        this.scoreTimeModifier = scoreTimeModifier;
    }
}