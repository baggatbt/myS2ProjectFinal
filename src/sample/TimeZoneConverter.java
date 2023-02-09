package sample;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeZoneConverter {

    public static ZonedDateTime toEasternTime(LocalDateTime localDateTime) {
        return ZonedDateTime.of(localDateTime, ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("America/New_York"));
    }

    public static ZonedDateTime toLocalTime(LocalDateTime localDateTime) {
        return ZonedDateTime.of(localDateTime, ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.systemDefault());
    }

    public static ZonedDateTime toUTC(LocalDateTime localDateTime) {
        return ZonedDateTime.of(localDateTime, ZoneId.systemDefault())
                .withZoneSameInstant(ZoneId.of("UTC"));
    }
}
