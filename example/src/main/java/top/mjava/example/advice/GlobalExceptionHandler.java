package top.mjava.example.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Map<String, Object> executeHandler(Exception e){
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code",500);
        result.put("msg",e.getMessage());
        return result;
    }
}
