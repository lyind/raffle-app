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

import net.talpidae.raffle.app.api.Quiz;
import net.talpidae.raffle.app.api.Raffle;
import net.talpidae.raffle.app.api.RaffleResult;
import net.talpidae.raffle.app.database.RaffleRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Singleton
@Resource
@Path("/")
public class RaffleImpl implements Raffle
{
    private final ObjectReader quizReader;

    private final ObjectWriter quizWriter;

    private final RaffleRepository raffleRepository;


    @Inject
    public RaffleImpl(RaffleRepository raffleRepository, ObjectMapper objectMapper)
    {
        this.raffleRepository = raffleRepository;
        this.quizReader = objectMapper.readerFor(Quiz.class);
        this.quizWriter = objectMapper.writerFor(Quiz.class);
    }


    @Override
    public Integer postQuiz(Quiz quiz)
    {
        try
        {
            return raffleRepository.insertRaffleResult(RaffleResult.builder().result(quizWriter.writeValueAsString(quiz)).build());
        }
        catch (JsonProcessingException e)
        {
            throw new BadRequestException(e.getMessage(), e);
        }
    }


    @Override
    public List<Quiz> getQuiz()
    {
        return raffleRepository.findAll()
                .stream()
                .map(raffleResult -> {
                    try
                    {
                        return (Quiz) quizReader.readValue(raffleResult.getResult());
                    }
                    catch (IOException e)
                    {
                        throw new InternalServerErrorException(e.getMessage(), e);
                    }
                })
                .collect(Collectors.toList());
    }


    @Override
    public Quiz getQuizById(Integer id)
    {
        return Optional.ofNullable(raffleRepository.findById(id))
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
}