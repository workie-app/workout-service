package org.workie.workout.domain.exercise;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ExerciseFilterTest {

  @Test
  void shouldNormalizeEmptyMovementPatternsToNull() {
    ExerciseFilter filter = new ExerciseFilter(null, List.of(), null, null, null);

    assertThat(filter.movementPatterns()).isNull();
  }

  @Test
  void shouldNormalizeEmptyTargetMuscleGroupsToNull() {
    ExerciseFilter filter = new ExerciseFilter(null, null, List.of(), null, null);

    assertThat(filter.targetMuscleGroups()).isNull();
  }

  @Test
  void shouldNormalizeEmptyCategoriesToNull() {
    ExerciseFilter filter = new ExerciseFilter(null, null, null, List.of(), null);

    assertThat(filter.categories()).isNull();
  }

  @Test
  void shouldNormalizeEmptyDifficultiesToNull() {
    ExerciseFilter filter = new ExerciseFilter(null, null, null, null, List.of());

    assertThat(filter.difficulties()).isNull();
  }

  @Test
  void shouldPreserveNullLists() {
    ExerciseFilter filter = new ExerciseFilter(null, null, null, null, null);

    assertThat(filter.movementPatterns()).isNull();
    assertThat(filter.targetMuscleGroups()).isNull();
    assertThat(filter.categories()).isNull();
    assertThat(filter.difficulties()).isNull();
  }

  @Test
  void shouldPreserveNonEmptyLists() {
    ExerciseFilter filter =
        new ExerciseFilter(
            "bench",
            List.of(MovementPattern.PUSH),
            List.of(MuscleGroup.CHEST),
            List.of(Category.STRENGTH),
            List.of(Difficulty.INTERMEDIATE));

    assertThat(filter.movementPatterns()).containsExactly(MovementPattern.PUSH);
    assertThat(filter.targetMuscleGroups()).containsExactly(MuscleGroup.CHEST);
    assertThat(filter.categories()).containsExactly(Category.STRENGTH);
    assertThat(filter.difficulties()).containsExactly(Difficulty.INTERMEDIATE);
  }
}
