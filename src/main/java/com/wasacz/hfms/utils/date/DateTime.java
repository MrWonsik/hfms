package com.wasacz.hfms.utils.date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

@Getter
@NoArgsConstructor
public class DateTime {
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate date;
    @JsonFormat(pattern="HH:mm:ss")
    private LocalTime time;


    public DateTime(Instant instant) {
        this.date = LocalDate.ofInstant(instant, ZoneId.systemDefault());
        this.time = LocalTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
