package org.workie.workout.domain.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class DomainPageableTest {

  @Test
  void shouldCreateValidPageable() {
    DomainPageable pageable = new DomainPageable(0, 20, List.of());

    assertThat(pageable.page()).isZero();
    assertThat(pageable.size()).isEqualTo(20);
    assertThat(pageable.sort()).isEmpty();
  }

  @Test
  void shouldThrowWhenPageIsNegative() {
    assertThatThrownBy(() -> new DomainPageable(-1, 20, List.of()))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowWhenSizeIsZero() {
    assertThatThrownBy(() -> new DomainPageable(0, 0, List.of()))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowWhenSortIsNull() {
    assertThatThrownBy(() -> new DomainPageable(0, 20, null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
