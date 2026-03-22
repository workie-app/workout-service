package org.workie.workout.application.exercise;

import org.workie.workout.domain.exercise.ExerciseFilter;
import org.workie.workout.domain.shared.DomainPageable;

public record ListExercisesCommand(ExerciseFilter filter, DomainPageable pageable) {

  public ListExercisesCommand {
    if (filter == null) {
      throw new IllegalArgumentException("filter must not be null");
    }
    if (pageable == null) {
      throw new IllegalArgumentException("pageable must not be null");
    }
  }
}
