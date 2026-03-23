package org.workie.workout.application.exercise;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.workie.workout.domain.exercise.ExerciseFilter;
import org.workie.workout.domain.shared.DomainPageable;

class ListExercisesCommandTest {

  private static final ExerciseFilter EMPTY_FILTER =
      new ExerciseFilter(null, null, null, null, null);
  private static final DomainPageable PAGEABLE = new DomainPageable(0, 20, List.of());

  @Test
  void shouldThrowWhenFilterIsNull() {
    assertThatThrownBy(() -> new ListExercisesCommand(null, PAGEABLE))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowWhenPageableIsNull() {
    assertThatThrownBy(() -> new ListExercisesCommand(EMPTY_FILTER, null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldCreateWithValidArgs() {
    assertThatNoException().isThrownBy(() -> new ListExercisesCommand(EMPTY_FILTER, PAGEABLE));
  }
}
