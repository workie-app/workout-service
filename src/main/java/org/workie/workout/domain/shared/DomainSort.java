package org.workie.workout.domain.shared;

public record DomainSort(String field, Direction direction) {

  public enum Direction {
    ASC,
    DESC
  }

  public static DomainSort of(String field) {
    return new DomainSort(field, Direction.ASC);
  }

  public DomainSort {
    if (field == null || field.isBlank()) {
      throw new IllegalArgumentException("field must not be blank");
    }
    if (direction == null) {
      throw new IllegalArgumentException("direction must not be null");
    }
  }
}
