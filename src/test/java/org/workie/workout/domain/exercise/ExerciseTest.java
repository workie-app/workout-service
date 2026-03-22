package org.workie.workout.domain.exercise;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class ExerciseTest {

  private Exercise benchPress(ExerciseId id) {
    return new Exercise(
        id,
        "Bench Press",
        MovementPattern.PUSH,
        List.of(MuscleGroup.CHEST),
        List.of(MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS),
        Category.STRENGTH,
        Difficulty.INTERMEDIATE);
  }

  @Test
  void shouldCreateValidExercise() {
    Exercise exercise = benchPress(ExerciseId.generate());

    assertThat(exercise.getName()).isEqualTo("Bench Press");
    assertThat(exercise.getMovementPattern()).isEqualTo(MovementPattern.PUSH);
    assertThat(exercise.getTargetMuscleGroups()).containsExactly(MuscleGroup.CHEST);
    assertThat(exercise.getSecondaryMuscleGroups())
        .containsExactly(MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS);
    assertThat(exercise.getCategory()).isEqualTo(Category.STRENGTH);
    assertThat(exercise.getDifficulty()).isEqualTo(Difficulty.INTERMEDIATE);
  }

  @Test
  void shouldThrowWhenIdIsNull() {
    assertThatThrownBy(
            () ->
                new Exercise(
                    null,
                    "Bench Press",
                    MovementPattern.PUSH,
                    List.of(MuscleGroup.CHEST),
                    List.of(),
                    Category.STRENGTH,
                    Difficulty.INTERMEDIATE))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowWhenNameIsNull() {
    assertThatThrownBy(
            () ->
                new Exercise(
                    ExerciseId.generate(),
                    null,
                    MovementPattern.PUSH,
                    List.of(MuscleGroup.CHEST),
                    List.of(),
                    Category.STRENGTH,
                    Difficulty.INTERMEDIATE))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowWhenNameIsBlank() {
    assertThatThrownBy(
            () ->
                new Exercise(
                    ExerciseId.generate(),
                    "  ",
                    MovementPattern.PUSH,
                    List.of(MuscleGroup.CHEST),
                    List.of(),
                    Category.STRENGTH,
                    Difficulty.INTERMEDIATE))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowWhenMovementPatternIsNull() {
    assertThatThrownBy(
            () ->
                new Exercise(
                    ExerciseId.generate(),
                    "Bench Press",
                    null,
                    List.of(MuscleGroup.CHEST),
                    List.of(),
                    Category.STRENGTH,
                    Difficulty.INTERMEDIATE))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowWhenTargetMuscleGroupsIsEmpty() {
    assertThatThrownBy(
            () ->
                new Exercise(
                    ExerciseId.generate(),
                    "Bench Press",
                    MovementPattern.PUSH,
                    List.of(),
                    List.of(),
                    Category.STRENGTH,
                    Difficulty.INTERMEDIATE))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowWhenCategoryIsNull() {
    assertThatThrownBy(
            () ->
                new Exercise(
                    ExerciseId.generate(),
                    "Bench Press",
                    MovementPattern.PUSH,
                    List.of(MuscleGroup.CHEST),
                    List.of(),
                    null,
                    Difficulty.INTERMEDIATE))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowWhenDifficultyIsNull() {
    assertThatThrownBy(
            () ->
                new Exercise(
                    ExerciseId.generate(),
                    "Bench Press",
                    MovementPattern.PUSH,
                    List.of(MuscleGroup.CHEST),
                    List.of(),
                    Category.STRENGTH,
                    null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldBeEqualWhenSameId() {
    ExerciseId id = ExerciseId.generate();
    Exercise a = benchPress(id);
    Exercise b =
        new Exercise(
            id,
            "Squat",
            MovementPattern.SQUAT,
            List.of(MuscleGroup.QUADRICEPS),
            List.of(),
            Category.STRENGTH,
            Difficulty.BEGINNER);

    assertThat(a).isEqualTo(b);
    assertThat(a.hashCode()).isEqualTo(b.hashCode());
  }

  @Test
  void shouldNotBeEqualWhenDifferentId() {
    Exercise a = benchPress(ExerciseId.generate());
    Exercise b = benchPress(ExerciseId.generate());

    assertThat(a).isNotEqualTo(b);
  }

  @Test
  void shouldThrowWhenTargetMuscleGroupsIsNull() {
    assertThatThrownBy(
            () ->
                new Exercise(
                    ExerciseId.generate(),
                    "Bench Press",
                    MovementPattern.PUSH,
                    null,
                    List.of(),
                    Category.STRENGTH,
                    Difficulty.INTERMEDIATE))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowWhenSecondaryMuscleGroupsIsNull() {
    assertThatThrownBy(
            () ->
                new Exercise(
                    ExerciseId.generate(),
                    "Bench Press",
                    MovementPattern.PUSH,
                    List.of(MuscleGroup.CHEST),
                    null,
                    Category.STRENGTH,
                    Difficulty.INTERMEDIATE))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldNotBeEqualToNonExercise() {
    Exercise a = benchPress(ExerciseId.generate());

    assertThat(a).isNotEqualTo(null);
    assertThat(a).isNotEqualTo("not an exercise");
  }
}
