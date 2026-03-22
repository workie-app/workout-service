package org.workie.workout.domain.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class DomainSortTest {

  @Test
  void shouldCreateWithExplicitDirection() {
    DomainSort sort = new DomainSort("name", DomainSort.Direction.DESC);

    assertThat(sort.field()).isEqualTo("name");
    assertThat(sort.direction()).isEqualTo(DomainSort.Direction.DESC);
  }

  @Test
  void shouldDefaultToAscendingWhenUsingFactoryMethod() {
    DomainSort sort = DomainSort.of("name");

    assertThat(sort.direction()).isEqualTo(DomainSort.Direction.ASC);
  }

  @Test
  void shouldThrowWhenFieldIsNull() {
    assertThatThrownBy(() -> new DomainSort(null, DomainSort.Direction.ASC))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowWhenFieldIsBlank() {
    assertThatThrownBy(() -> new DomainSort("  ", DomainSort.Direction.ASC))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowWhenDirectionIsNull() {
    assertThatThrownBy(() -> new DomainSort("name", null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
