package net.talpidae.raffle.app.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;


@Value
public class Quiz
{
    /**
     * How long did the quiz take.
     */
    private final Long durationMillies;

    /**
     * All answered multiple choice questions, if any.
     */
    private final List<MultipleChoiceQuestion> multipleChoice;

    /**
     * Who answered the questions?
     */
    private final Participant participant;


    @Builder
    @JsonCreator
    public Quiz(@JsonProperty("durationMillies") Long durationMillies,
                @JsonProperty("multipleChoice") List<MultipleChoiceQuestion> multipleChoice,
                @JsonProperty("participant") Participant participant)
    {
        this.durationMillies = durationMillies;
        this.multipleChoice = multipleChoice;
        this.participant = participant;
    }


    @Value
    public static class Participant
    {
        private final String name;

        private final String lastName;

        private final String company;

        private final String street;

        private final String zip;

        private final String city;

        private final String email;


        @Builder
        @JsonCreator
        public Participant(@JsonProperty("name") String name,
                           @JsonProperty("lastName") String lastName,
                           @JsonProperty("company") String company,
                           @JsonProperty("street") String street,
                           @JsonProperty("zip") String zip,
                           @JsonProperty("city") String city,
                           @JsonProperty("email") String email)
        {
            this.name = name;
            this.lastName = lastName;
            this.company = company;
            this.street = street;
            this.zip = zip;
            this.city = city;
            this.email = email;
        }
    }


    @Getter
    public abstract static class Question
    {
        private final String text;

        @JsonCreator
        public Question(@JsonProperty("text") String text)
        {
            this.text = text;
        }
    }


    @Getter
    public abstract static class Answer
    {
        private final String text;

        @JsonCreator
        public Answer(@JsonProperty("text") String text)
        {
            this.text = text;
        }
    }


    @Value
    @EqualsAndHashCode(callSuper = true)
    public static class SelectableAnswer extends Answer
    {
        private final Boolean selected;


        @Builder
        @JsonCreator
        public SelectableAnswer(@JsonProperty("text") String text,
                                @JsonProperty("selected") Boolean selected)
        {
            super(text);
            this.selected = selected;
        }
    }


    @Value
    @EqualsAndHashCode(callSuper = true)
    public static class MultipleChoiceQuestion extends Question
    {
        private final List<SelectableAnswer> answers;


        @Builder
        @JsonCreator
        public MultipleChoiceQuestion(@JsonProperty("text") String text,
                                      @JsonProperty("answers") List<SelectableAnswer> answers)
        {
            super(text);
            this.answers = answers;
        }
    }
}
