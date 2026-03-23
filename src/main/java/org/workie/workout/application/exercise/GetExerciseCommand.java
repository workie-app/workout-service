package org.workie.workout.application.exercise;

import org.workie.workout.domain.exercise.ExerciseId;

public record GetExerciseCommand(ExerciseId exerciseId) {

  public GetExerciseCommand {
    if (exerciseId == null) {
      throw new IllegalArgumentException("exerciseId must not be null");
    }
  }
}
