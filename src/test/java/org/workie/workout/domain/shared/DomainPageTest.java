package org.workie.workout.domain.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class DomainPageTest {

  @Test
  void shouldCreateValidPage() {
    DomainPage<String> page = new DomainPage<>(List.of("a", "b"), 10, 5, 0, 2);

    assertThat(page.content()).containsExactly("a", "b");
    assertThat(page.totalElements()).isEqualTo(10);
    assertThat(page.totalPages()).isEqualTo(5);
    assertThat(page.number()).isZero();
    assertThat(page.size()).isEqualTo(2);
  }

  @Test
  void shouldBeFirstWhenPageNumberIsZero() {
    DomainPage<String> page = new DomainPage<>(List.of(), 0, 0, 0, 20);
    assertThat(page.isFirst()).isTrue();
  }

  @Test
  void shouldNotBeFirstWhenPageNumberIsNotZero() {
    DomainPage<String> page = new DomainPage<>(List.of("a"), 10, 5, 1, 2);
    assertThat(page.isFirst()).isFalse();
  }

  @Test
  void shouldBeLastWhenOnLastPage() {
    DomainPage<String> page = new DomainPage<>(List.of("a"), 5, 3, 2, 2);
    assertThat(page.isLast()).isTrue();
  }

  @Test
  void shouldNotBeLastWhenNotOnLastPage() {
    DomainPage<String> page = new DomainPage<>(List.of("a", "b"), 10, 5, 0, 2);
    assertThat(page.isLast()).isFalse();
  }

  @Test
  void shouldBeEmptyWhenContentIsEmpty() {
    DomainPage<String> page = new DomainPage<>(List.of(), 0, 0, 0, 20);
    assertThat(page.isEmpty()).isTrue();
  }

  @Test
  void shouldReturnNumberOfElementsAsContentSize() {
    DomainPage<String> page = new DomainPage<>(List.of("a", "b"), 10, 5, 0, 2);
    assertThat(page.numberOfElements()).isEqualTo(2);
  }

  @Test
  void shouldThrowWhenContentIsNull() {
    assertThatThrownBy(() -> new DomainPage<>(null, 0, 0, 0, 20))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowWhenTotalElementsIsNegative() {
    assertThatThrownBy(() -> new DomainPage<>(List.of(), -1, 0, 0, 20))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowWhenTotalPagesIsNegative() {
    assertThatThrownBy(() -> new DomainPage<>(List.of(), 0, -1, 0, 20))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowWhenNumberIsNegative() {
    assertThatThrownBy(() -> new DomainPage<>(List.of(), 0, 0, -1, 20))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowWhenSizeIsZero() {
    assertThatThrownBy(() -> new DomainPage<>(List.of(), 0, 0, 0, 0))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
