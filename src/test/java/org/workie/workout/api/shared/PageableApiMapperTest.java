package org.workie.workout.api.shared;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.workie.workout.domain.shared.DomainPageable;
import org.workie.workout.domain.shared.DomainSort;

class PageableApiMapperTest {

  private final PageableApiMapper mapper = new PageableApiMapper();

  @Test
  void shouldMapPageNumberAndSize() {
    DomainPageable result = mapper.toPageRequest(PageRequest.of(2, 15));

    assertThat(result.page()).isEqualTo(2);
    assertThat(result.size()).isEqualTo(15);
  }

  @Test
  void shouldMapAscendingSort() {
    DomainPageable result = mapper.toPageRequest(PageRequest.of(0, 20, Sort.by("name")));

    assertThat(result.sort()).hasSize(1);
    assertThat(result.sort().getFirst().field()).isEqualTo("name");
    assertThat(result.sort().getFirst().direction()).isEqualTo(DomainSort.Direction.ASC);
  }

  @Test
  void shouldMapDescendingSort() {
    DomainPageable result =
        mapper.toPageRequest(PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "name")));

    assertThat(result.sort().getFirst().direction()).isEqualTo(DomainSort.Direction.DESC);
  }

  @Test
  void shouldMapMultipleSortCriteria() {
    DomainPageable result =
        mapper.toPageRequest(
            PageRequest.of(
                0, 20, Sort.by("muscleGroup").and(Sort.by(Sort.Direction.DESC, "name"))));

    assertThat(result.sort()).hasSize(2);
  }

  @Test
  void shouldMapEmptySort() {
    DomainPageable result = mapper.toPageRequest(PageRequest.of(0, 20));

    assertThat(result.sort()).isEmpty();
  }
}
