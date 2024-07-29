package com.cupid.qufit.domain.member.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MemberBirthDateUtil {

    public static LocalDate convertToLocalDate(Integer birthyear){
        return LocalDate.parse(birthyear + "0101",
                               DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static Integer convertToYear(LocalDate birthdate){
        return birthdate.getYear();
    }
}
