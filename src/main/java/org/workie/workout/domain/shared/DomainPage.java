package org.workie.workout.domain.shared;

import java.util.List;

public record DomainPage<T>(
    List<T> content, long totalElements, int totalPages, int number, int size) {

  public DomainPage {
    if (content == null) {
      throw new IllegalArgumentException("content must not be null");
    }
    if (totalElements < 0) {
      throw new IllegalArgumentException("totalElements must be >= 0");
    }
    if (totalPages < 0) {
      throw new IllegalArgumentException("totalPages must be >= 0");
    }
    if (number < 0) {
      throw new IllegalArgumentException("number must be >= 0");
    }
    if (size < 1) {
      throw new IllegalArgumentException("size must be >= 1");
    }
  }

  public int numberOfElements() {
    return content.size();
  }

  public boolean isFirst() {
    return number == 0;
  }

  public boolean isLast() {
    return number >= totalPages - 1;
  }

  public boolean isEmpty() {
    return content.isEmpty();
  }
}
