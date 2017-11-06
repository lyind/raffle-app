/*
 * Copyright (C) 2017  Jonas Zeiger <jonas.zeiger@talpidae.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.talpidae.raffle.app.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import net.talpidae.raffle.app.api.FinishedQuiz;
import net.talpidae.raffle.app.api.Quiz;
import net.talpidae.raffle.app.api.QuizStats;
import net.talpidae.raffle.app.api.Raffle;
import net.talpidae.raffle.app.api.RaffleResult;
import net.talpidae.raffle.app.database.RaffleRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;


@Slf4j
@Singleton
@Resource
@Path("/")
public class RaffleImpl implements Raffle
{
    private final ObjectReader quizReader;

    private final ObjectWriter quizWriter;

    private final RaffleRepository raffleRepository;

    private final QuizOptions quizOptions;


    @Inject
    public RaffleImpl(RaffleRepository raffleRepository, ObjectMapper objectMapper, QuizOptions quizOptions)
    {
        this.raffleRepository = raffleRepository;
        this.quizReader = objectMapper.readerFor(Quiz.class);
        this.quizWriter = objectMapper.writerFor(Quiz.class);
        this.quizOptions = quizOptions;
    }


    @Override
    public Integer postQuiz(Quiz quiz, Boolean updateBaseline)
    {
        try
        {
            if (updateBaseline != null && updateBaseline)
            {
                return raffleRepository.insertRaffleBaseline(RaffleResult.builder().result(quizWriter.writeValueAsString(quiz)).build());
            }
            else
            {
                return raffleRepository.insertRaffleResult(RaffleResult.builder().result(quizWriter.writeValueAsString(quiz)).build());
            }
        }
        catch (JsonProcessingException e)
        {
            throw new BadRequestException(e.getMessage(), e);
        }
    }


    @Override
    public List<FinishedQuiz> getQuiz()
    {
        val baseline = getCurrentBaseline();

        return raffleRepository.findAll()
                .stream()
                .map(result -> mapResultToFinishedQuiz(result, baseline))
                .collect(Collectors.toList());
    }


    @Override
    public FinishedQuiz getQuizById(Integer id)
    {
        val baseline = getCurrentBaseline();

        return Optional.ofNullable(raffleRepository.findById(id))
                .map(result -> mapResultToFinishedQuiz(result, baseline))
                .orElse(null);
    }


    private Quiz getCurrentBaseline()
    {
        return Optional.ofNullable(raffleRepository.findCurrentRaffleBaseline())
                .map(this::mapResultToQuiz)
                .orElse(null);
    }


    private QuizStats generateQuizStats(Quiz quiz, Quiz baseline)
    {
        if (baseline == null || quiz == null)
        {
            return QuizStats.emptyStats();
        }

        val baselineDuration = baseline.getDurationMillies();
        val baselineAnswers = getMultipleChoiceAnswers(baseline);
        val quizDuration = quiz.getDurationMillies();
        val quizAnswers = getMultipleChoiceAnswers(quiz);

        val baselineCorrectAnswers = baselineAnswers.values()
                .stream()
                .filter(Boolean::booleanValue)
                .count();

        val correctMultipleChoiceCount = (long) quizAnswers.entrySet()
                .stream()
                .filter((entry) -> entry.getValue() != null && entry.getValue())  // is selected
                .filter((entry) ->
                {
                    val baselineAnswer = baselineAnswers.get(entry.getKey());
                    if (baselineAnswer == null)
                    {
                        throw new IllegalStateException("corrupt quiz or outdated baseline: baseline doesn't contain key: " + entry.getKey());
                    }

                    // is also selected in baseline
                    return baselineAnswer;
                })
                .count();

        val rangeMillies = baselineDuration / 2;
        // quiz took shorter than baseline: positive difference, negative difference otherwise (both limited by range)
        val baseLineTimeDifferenceMillies = Math.max(-rangeMillies, Math.min(rangeMillies, baselineDuration - quizDuration));

        val scoreTimeModifier = ((double)baseLineTimeDifferenceMillies / (double)rangeMillies) * quizOptions.getScoreTimeFactor();

        val rawScoreMax = (int) (baselineCorrectAnswers * quizOptions.getPointsPerAnswer());
        val rawScore = (int) (correctMultipleChoiceCount * quizOptions.getPointsPerAnswer());

        val scoreMax = rawScoreMax + (int) Math.round((rawScoreMax * quizOptions.getScoreTimeFactor()));
        val score = rawScore + (int) Math.round((rawScore * scoreTimeModifier));

        return QuizStats.builder()
                .score(score)
                .scoreMax(scoreMax)
                .scoreTimeModifier(scoreTimeModifier)
                .build();
    }


    @Value
    @Builder
    @ToString
    @EqualsAndHashCode
    private static class AnswerKey
    {
        private final String questionText;

        private final String answerText;
    }


    private Map<AnswerKey, Boolean> getMultipleChoiceAnswers(Quiz quiz)
    {
        val answers = new HashMap<AnswerKey, Boolean>();

        for (val question : quiz.getMultipleChoice())
        {
            for (val answer : question.getAnswers())
            {
                val key = AnswerKey.builder().questionText(question.getText()).answerText(answer.getText()).build();
                val previous = answers.put(key, answer.getSelected());
                if (previous != null)
                    throw new IllegalStateException("corrupt quiz: multiple answers with same key: " + key);
            }
        }

        return answers;
    }


    private Quiz mapResultToQuiz(RaffleResult raffleResult) throws InternalServerErrorException
    {
        return Optional.ofNullable(raffleResult)
                .map(RaffleResult::getResult)
                .map(result ->
                {
                    try
                    {
                        return (Quiz) quizReader.readValue(result);
                    }
                    catch (IOException e)
                    {
                        throw new InternalServerErrorException(e.getMessage(), e);
                    }
                })
                .orElse(null);
    }


    private FinishedQuiz mapResultToFinishedQuiz(RaffleResult raffleResult, Quiz baseline)
    {
        val quiz = mapResultToQuiz(raffleResult);

        return FinishedQuiz.builder()
                .id(raffleResult.getId())
                .ts(raffleResult.getTs())
                .quiz(quiz)
                .stats(generateQuizStats(quiz, baseline))
                .build();
    }
}