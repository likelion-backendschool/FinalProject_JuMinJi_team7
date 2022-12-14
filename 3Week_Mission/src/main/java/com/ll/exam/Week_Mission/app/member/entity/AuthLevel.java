package com.ll.exam.Week_Mission.app.member.entity;

import com.ll.exam.Week_Mission.app.exception.NotMatchAuthLevelException;
import lombok.Getter;

import javax.persistence.AttributeConverter;
import java.util.EnumSet;

@Getter
public enum AuthLevel {
    NORMAL(3, "NORMAL"),
    ADMIN(7, "ADMIN");

    AuthLevel(int code, String value) {
        this.code = code;
        this.value = value;
    }

    private int code;
    private String value;

    /* AuthLevel code <-> Integer code */
    public static class Converter implements AttributeConverter<AuthLevel, Integer> {
        @Override
        public Integer convertToDatabaseColumn(AuthLevel attribute) {
            return attribute.getCode();
        }

        @Override
        public AuthLevel convertToEntityAttribute(Integer dbData) {
            return EnumSet.allOf(AuthLevel.class).stream() // EnumSet -> null X, EnumSet 메서드 사용 시 비트연산으로 계산속도 빠름
                    .filter(e -> e.getCode() == dbData)
                    .findAny()
                    .orElseThrow(NotMatchAuthLevelException::new);
        }
    }
}