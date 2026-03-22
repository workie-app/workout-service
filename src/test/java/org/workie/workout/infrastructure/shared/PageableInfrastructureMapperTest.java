package org.workie.workout.infrastructure.shared;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.workie.workout.domain.shared.DomainPage;
import org.workie.workout.domain.shared.DomainPageable;
import org.workie.workout.domain.shared.DomainSort;

class PageableInfrastructureMapperTest {

  private final PageableInfrastructureMapper mapper = new PageableInfrastructureMapper();

  @Test
  void shouldMapPageNumberAndSize() {
    Pageable result = mapper.toPageable(new DomainPageable(2, 15, List.of()));

    assertThat(result.getPageNumber()).isEqualTo(2);
    assertThat(result.getPageSize()).isEqualTo(15);
  }

  @Test
  void shouldMapAscendingSort() {
    Pageable result = mapper.toPageable(new DomainPageable(0, 20, List.of(DomainSort.of("name"))));

    assertThat(result.getSort().getOrderFor("name")).isNotNull();
    assertThat(result.getSort().getOrderFor("name").getDirection()).isEqualTo(Sort.Direction.ASC);
  }

  @Test
  void shouldMapDescendingSort() {
    Pageable result =
        mapper.toPageable(
            new DomainPageable(0, 20, List.of(new DomainSort("name", DomainSort.Direction.DESC))));

    assertThat(result.getSort().getOrderFor("name").getDirection()).isEqualTo(Sort.Direction.DESC);
  }

  @Test
  void shouldMapMultipleSortCriteria() {
    Pageable result =
        mapper.toPageable(
            new DomainPageable(
                0,
                20,
                List.of(
                    DomainSort.of("muscleGroup"),
                    new DomainSort("name", DomainSort.Direction.DESC))));

    assertThat(result.getSort().getOrderFor("muscleGroup")).isNotNull();
    assertThat(result.getSort().getOrderFor("name")).isNotNull();
  }

  @Test
  void shouldMapEmptySortToUnsorted() {
    Pageable result = mapper.toPageable(new DomainPageable(0, 20, List.of()));

    assertThat(result.getSort().isUnsorted()).isTrue();
  }

  @Test
  void shouldMapSpringPageToDomainPage() {
    List<String> content = List.of("a", "b");
    PageImpl<String> springPage = new PageImpl<>(content, PageRequest.of(1, 2), 10);

    DomainPage<String> result = mapper.toDomainPage(springPage);

    assertThat(result.content()).containsExactly("a", "b");
    assertThat(result.totalElements()).isEqualTo(10);
    assertThat(result.totalPages()).isEqualTo(5);
    assertThat(result.number()).isEqualTo(1);
    assertThat(result.size()).isEqualTo(2);
  }

  @Test
  void shouldMapEmptySpringPageToDomainPage() {
    PageImpl<String> springPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);

    DomainPage<String> result = mapper.toDomainPage(springPage);

    assertThat(result.content()).isEmpty();
    assertThat(result.totalElements()).isZero();
    assertThat(result.isEmpty()).isTrue();
  }
}
