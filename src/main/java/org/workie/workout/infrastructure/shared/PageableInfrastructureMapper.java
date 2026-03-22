package org.workie.workout.infrastructure.shared;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.workie.workout.domain.shared.DomainPage;
import org.workie.workout.domain.shared.DomainPageable;
import org.workie.workout.domain.shared.DomainSort;

@Component
public class PageableInfrastructureMapper {

  public Pageable toPageable(DomainPageable pageable) {
    return PageRequest.of(pageable.page(), pageable.size(), toSort(pageable.sort()));
  }

  public <T> DomainPage<T> toDomainPage(Page<T> page) {
    return new DomainPage<>(
        page.getContent(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.getNumber(),
        page.getSize());
  }

  private Sort toSort(List<DomainSort> sort) {
    if (sort.isEmpty()) {
      return Sort.unsorted();
    }
    return Sort.by(
        sort.stream()
            .map(
                order ->
                    order.direction() == DomainSort.Direction.ASC
                        ? Sort.Order.asc(order.field())
                        : Sort.Order.desc(order.field()))
            .toList());
  }
}
