package org.workie.workout.api.shared;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.workie.workout.domain.shared.DomainPageable;
import org.workie.workout.domain.shared.DomainSort;

@Component
public class PageableApiMapper {

  public DomainPageable toPageRequest(Pageable pageable) {
    return new DomainPageable(
        pageable.getPageNumber(),
        pageable.getPageSize(),
        pageable.getSort().stream()
            .map(
                order ->
                    new DomainSort(
                        order.getProperty(),
                        order.isAscending() ? DomainSort.Direction.ASC : DomainSort.Direction.DESC))
            .toList());
  }
}
