package com.example.momentix.domain.common.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.array;


public class AgeUtilTest {

    @Test
    void 만19세미만_false(){
        LocalDate birthDate = LocalDate.of(2007,2,19);
        boolean result=AgeUtil.isOverAge(birthDate,19);
        assertThat(result).isFalse();
    }

    @Test
    void 만19세이상_true(){
        LocalDate birthDate = LocalDate.of(2000,2,19);
        boolean result=AgeUtil.isOverAge(birthDate,19);
        assertThat(result).isTrue();
    }


}
