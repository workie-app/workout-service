package org.workie.workout.domain.exercise;

import java.util.List;
import java.util.Objects;
import lombok.Getter;

@Getter
public class Exercise {

  private final ExerciseId id;
  private final String name;
  private final MovementPattern movementPattern;
  private final List<MuscleGroup> targetMuscleGroups;
  private final List<MuscleGroup> secondaryMuscleGroups;
  private final Category category;
  private final Difficulty difficulty;

  public Exercise(
      ExerciseId id,
      String name,
      MovementPattern movementPattern,
      List<MuscleGroup> targetMuscleGroups,
      List<MuscleGroup> secondaryMuscleGroups,
      Category category,
      Difficulty difficulty) {
    if (id == null) {
      throw new IllegalArgumentException("id must not be null");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
    if (movementPattern == null) {
      throw new IllegalArgumentException("movementPattern must not be null");
    }
    if (targetMuscleGroups == null || targetMuscleGroups.isEmpty()) {
      throw new IllegalArgumentException("targetMuscleGroups must not be empty");
    }
    if (secondaryMuscleGroups == null) {
      throw new IllegalArgumentException("secondaryMuscleGroups must not be null");
    }
    if (category == null) {
      throw new IllegalArgumentException("category must not be null");
    }
    if (difficulty == null) {
      throw new IllegalArgumentException("difficulty must not be null");
    }
    this.id = id;
    this.name = name;
    this.movementPattern = movementPattern;
    this.targetMuscleGroups = List.copyOf(targetMuscleGroups);
    this.secondaryMuscleGroups = List.copyOf(secondaryMuscleGroups);
    this.category = category;
    this.difficulty = difficulty;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Exercise exercise)) {
      return false;
    }
    return Objects.equals(id, exercise.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
