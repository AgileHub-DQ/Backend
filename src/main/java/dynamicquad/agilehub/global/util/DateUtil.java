package dynamicquad.agilehub.global.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static String getCurrentMonth() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return now.format(formatter);
    }
}
