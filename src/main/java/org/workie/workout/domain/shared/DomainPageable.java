package org.workie.workout.domain.shared;

import java.util.List;

public record DomainPageable(int page, int size, List<DomainSort> sort) {

  public DomainPageable {
    if (page < 0) {
      throw new IllegalArgumentException("page must be >= 0");
    }
    if (size < 1) {
      throw new IllegalArgumentException("size must be >= 1");
    }
    if (sort == null) {
      throw new IllegalArgumentException("sort must not be null");
    }
  }
}
