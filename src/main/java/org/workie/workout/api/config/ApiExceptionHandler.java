package org.workie.workout.api.config;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.workie.workout.domain.exercise.ExerciseNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ExerciseNotFoundException.class)
  public ProblemDetail handleExerciseNotFound(ExerciseNotFoundException ex, WebRequest request) {
    return createProblemDetail(ex, HttpStatus.NOT_FOUND, ex.getMessage(), null, null, request);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ProblemDetail handleTypeMismatch(
      MethodArgumentTypeMismatchException ex, WebRequest request) {
    return createProblemDetail(ex, HttpStatus.BAD_REQUEST, buildDetail(ex), null, null, request);
  }

  private String buildDetail(MethodArgumentTypeMismatchException ex) {
    String base = "Invalid value for parameter '" + ex.getName() + "'.";
    if (ex.getCause() instanceof ConversionFailedException cfe) {
      Class<?> targetType = cfe.getTargetType().getType();
      if (targetType.isEnum()) {
        String allowed =
            Arrays.stream(targetType.getEnumConstants())
                .map(Object::toString)
                .collect(Collectors.joining(", ", "[", "]"));
        return base + " Allowed values: " + allowed + ".";
      }
    }
    return base;
  }
}
