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

package net.talpidae.raffle.app.api;

import net.talpidae.base.util.auth.AuthRequired;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


@Path("/")
public interface Raffle
{
    /**
     * Accepts a quiz result document and persists it into the DB.
     *
     * @param updateBaseline Update baseline for calculating ResultStats.
     * @return ID of the result record added to the DB.
     */
    @POST
    @Path("/quiz/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    FinishedQuiz postQuiz(Quiz quiz, @QueryParam("updateBaseline") Boolean updateBaseline);


    /**
     * Returns all recorded quiz results.
     */
    @GET
    @AuthRequired
    @Path("/quiz/")
    @Produces(MediaType.APPLICATION_JSON)
    List<FinishedQuiz> getQuiz();


    /**
     * Returns the quiz result specified by parameter ID.
     *
     * @return The quiz result having the specified ID.
     */
    @GET
    @AuthRequired
    @Path("/quiz/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    FinishedQuiz getQuizById(@PathParam("id") Integer id);
}