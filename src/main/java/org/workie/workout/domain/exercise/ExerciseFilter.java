package org.workie.workout.domain.exercise;

import java.util.List;

public record ExerciseFilter(
    String name,
    List<MovementPattern> movementPatterns,
    List<MuscleGroup> targetMuscleGroups,
    List<Category> categories,
    List<Difficulty> difficulties) {

  public ExerciseFilter {
    if (movementPatterns != null && movementPatterns.isEmpty()) {
      movementPatterns = null;
    }
    if (targetMuscleGroups != null && targetMuscleGroups.isEmpty()) {
      targetMuscleGroups = null;
    }
    if (categories != null && categories.isEmpty()) {
      categories = null;
    }
    if (difficulties != null && difficulties.isEmpty()) {
      difficulties = null;
    }
  }
}
