package com.ll.exam.Week_Mission.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.exam.Week_Mission.app.base.dto.RsData;
import com.ll.exam.Week_Mission.app.config.AppConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Ut {
    private static ObjectMapper getObjectMapper() {
        // AppConfig의 context 변수를 static으로 선언해놨기 때문에 @Autowired 어노테이션 사용불가
        // 대신 @Autowired 기능 직접 구현 -> .getBean("objectMapper")
        // 이 외의 방법) new ObjectMapper로 새로 객체 생성할 수도 있지만 싱글톤 객체로 유지하기 위해 이 방법으로 구현
        // private final ObjectMapper objectMapper로 생성자주입하지 않는 이유? json 클래스는 static이므로 static 변수 필요
        return (ObjectMapper) AppConfig.getContext().getBean("objectMapper");
    }

    public static class date {
        /* month 자릿수 두 자릿수로 맞추기 */
        public static int getEndDayOf(int year, int month) {
            String yearMonth = year + "-" + "%02d".formatted(month);

            return getEndDayOf(yearMonth);
        }

        /* 월초 데이터로 월말 리턴 */
        public static int getEndDayOf(String yearMonth) {
            LocalDate convertedDate = LocalDate.parse(yearMonth + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            convertedDate = convertedDate.withDayOfMonth(
                    convertedDate.getMonth().length(convertedDate.isLeapYear()));

            return convertedDate.getDayOfMonth();
        }

        /* String -> LocalDateTime (pattern parameter O) */
        public static LocalDateTime parse(String pattern, String dateText) {
            return LocalDateTime.parse(dateText, DateTimeFormatter.ofPattern(pattern));
        }

        /* String -> LocalDateTime (pattern parameter X) */
        public static LocalDateTime parse(String dateText) {
            return parse("yyyy-MM-dd HH:mm:ss.SSSSSS", dateText);
        }
    }

    public static class url {

        public static String addQueryParam(String url, String paramName, String paramValue) {
            if (url.contains("?") == false) {
                url += "?";
            }

            url += paramName + "=" + encode(paramValue);

            return url;
        }

        public static String modifyQueryParam(String url, String paramName, String paramValue) {
            url = deleteQueryParam(url, paramName);
            url = addQueryParam(url, paramName, paramValue);

            return url;
        }

        private static String deleteQueryParam(String url, String paramName) {
            int startPoint = url.indexOf(paramName + "=");
            if (startPoint == -1) return url;

            int endPoint = url.substring(startPoint).indexOf("&");

            if (endPoint == -1) {
                return url.substring(0, startPoint - 1);
            }

            String urlAfter = url.substring(startPoint + endPoint + 1);

            return url.substring(0, startPoint) + urlAfter;
        }

        public static String encode(String str) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return str;
            }
        }
    }

    public static class json {

        /* Map -> JSON String으로 변환 */
        // ex. {"name":"mkyong","age":"37"} 형태로 변환
        public static Object toStr(Map<String, Object> map) {
            try {
                // jackson.databind.ObjectMapper 이용하여 String으로 Serialization
                return getObjectMapper().writeValueAsString(map);
            } catch (JsonProcessingException e) {
                return null;
            }
        }
    }
}
