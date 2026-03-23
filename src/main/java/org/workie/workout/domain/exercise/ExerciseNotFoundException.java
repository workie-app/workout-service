package org.workie.workout.domain.exercise;

public class ExerciseNotFoundException extends RuntimeException {

  public ExerciseNotFoundException(ExerciseId id) {
    super("Exercise not found: " + id.value());
  }
}
