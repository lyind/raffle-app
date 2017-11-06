package net.talpidae.raffle.app.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Value;


@Value
public class FinishedQuiz
{
    /**
     * The unique quiz ID.
     */
    private final Integer id;


    /**
     * The timestamp of submission.
     */
    private final OffsetDateTime ts;

    /**
     * The actual quiz.
     */
    private final Quiz quiz;

    /**
     * The stats of this quiz relative to the current baseline.
     */
    private final QuizStats stats;


    @Builder
    @JsonCreator
    public FinishedQuiz(@JsonProperty("id") Integer id,
                        @JsonProperty("ts") OffsetDateTime ts,
                        @JsonProperty("quiz") Quiz quiz,
                        @JsonProperty("stats") QuizStats stats)
    {
        this.id = id;
        this.ts = ts;
        this.quiz = quiz;
        this.stats = stats;
    }
}