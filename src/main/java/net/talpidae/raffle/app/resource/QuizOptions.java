package net.talpidae.raffle.app.resource;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.talpidae.base.util.BaseArguments;

import lombok.Getter;
import lombok.val;


@Singleton
public class QuizOptions
{
    /**
     * How much time influences scores (0.0 - 1.0).
     * <p>
     * Default: 30%
     */
    @Getter
    private final double scoreTimeFactor;


    /**
     * Points per correct answer.
     * <p>
     * Default: 5
     */
    @Getter
    private final int pointsPerAnswer;


    @Inject
    public QuizOptions(BaseArguments baseArguments)
    {
        val parser = baseArguments.getOptionParser();
        val scoreTimeFactorOption = parser.accepts("quiz.scoreTimeFactor").withRequiredArg().ofType(Double.class).defaultsTo(0.30);
        val pointsPerAnswerOption = parser.accepts("quiz.pointsPerAnswer").withRequiredArg().ofType(Integer.class).defaultsTo(5);
        val options = baseArguments.parse();

        this.scoreTimeFactor = Math.max(0.0, Math.min(1.0, options.valueOf(scoreTimeFactorOption)));
        this.pointsPerAnswer = options.valueOf(pointsPerAnswerOption);
    }
}