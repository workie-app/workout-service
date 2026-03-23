package org.workie.workout.application.exercise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.workie.workout.domain.exercise.ExerciseId;

class GetExerciseCommandTest {

  @Test
  void shouldCreateCommandWithValidExerciseId() {
    ExerciseId id = ExerciseId.generate();
    GetExerciseCommand command = new GetExerciseCommand(id);
    assertThat(command.exerciseId()).isEqualTo(id);
  }

  @Test
  void shouldThrowWhenExerciseIdIsNull() {
    assertThatThrownBy(() -> new GetExerciseCommand(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("exerciseId must not be null");
  }
}
