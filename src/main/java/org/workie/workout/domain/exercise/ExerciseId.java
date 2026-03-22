package org.workie.workout.domain.exercise;

import java.util.UUID;

public record ExerciseId(UUID value) {
  public ExerciseId {
    if (value == null) {
      throw new IllegalArgumentException("ExerciseId value must not be null");
    }
  }

  public static ExerciseId generate() {
    return new ExerciseId(UUID.randomUUID());
  }

  public static ExerciseId of(UUID value) {
    return new ExerciseId(value);
  }
}
