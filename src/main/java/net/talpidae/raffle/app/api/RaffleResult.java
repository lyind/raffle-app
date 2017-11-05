package net.talpidae.raffle.app.api;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


@Getter
@EqualsAndHashCode
@ToString
@Builder
public class RaffleResult
{
    private final Integer id;

    private final OffsetDateTime ts;

    private final String result;


    @JsonCreator
    public RaffleResult(@JsonProperty("id") @ColumnName("id") Integer id,
                        @JsonProperty("ts") @ColumnName("ts") OffsetDateTime ts,
                        @JsonProperty("result") @ColumnName("result") String result)
    {
        this.id = id;
        this.ts = ts;
        this.result = result;
    }
}