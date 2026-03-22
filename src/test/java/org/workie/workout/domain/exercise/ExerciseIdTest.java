package org.workie.workout.domain.exercise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ExerciseIdTest {

  @Test
  void shouldGenerateNonNullId() {
    ExerciseId id = ExerciseId.generate();

    assertThat(id.value()).isNotNull();
  }

  @Test
  void shouldThrowWhenValueIsNull() {
    assertThatThrownBy(() -> new ExerciseId(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowWhenOfValueIsNull() {
    assertThatThrownBy(() -> ExerciseId.of(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldCreateWithOf() {
    UUID uuid = UUID.randomUUID();
    ExerciseId id = ExerciseId.of(uuid);

    assertThat(id.value()).isEqualTo(uuid);
  }
}
