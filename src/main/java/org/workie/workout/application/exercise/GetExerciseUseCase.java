package org.workie.workout.application.exercise;

import org.workie.workout.domain.exercise.Exercise;
import org.workie.workout.domain.exercise.ExerciseNotFoundException;
import org.workie.workout.domain.exercise.ExerciseRepository;

public class GetExerciseUseCase {

  private final ExerciseRepository exerciseRepository;

  public GetExerciseUseCase(ExerciseRepository exerciseRepository) {
    this.exerciseRepository = exerciseRepository;
  }

  public Exercise execute(GetExerciseCommand command) {
    return exerciseRepository
        .findById(command.exerciseId())
        .orElseThrow(() -> new ExerciseNotFoundException(command.exerciseId()));
  }
}
