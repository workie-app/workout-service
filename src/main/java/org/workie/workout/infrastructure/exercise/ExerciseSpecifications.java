package org.workie.workout.infrastructure.exercise;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.workie.workout.domain.exercise.Category;
import org.workie.workout.domain.exercise.Difficulty;
import org.workie.workout.domain.exercise.MovementPattern;
import org.workie.workout.domain.exercise.MuscleGroup;

class ExerciseSpecifications {

  private ExerciseSpecifications() {}

  static Specification<ExerciseJpaEntity> nameLike(String name) {
    return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
  }

  static Specification<ExerciseJpaEntity> movementPatternIn(List<MovementPattern> patterns) {
    return (root, query, cb) -> root.get("movementPattern").in(patterns);
  }

  static Specification<ExerciseJpaEntity> categoryIn(List<Category> categories) {
    return (root, query, cb) -> root.get("category").in(categories);
  }

  static Specification<ExerciseJpaEntity> difficultyIn(List<Difficulty> difficulties) {
    return (root, query, cb) -> root.get("difficulty").in(difficulties);
  }

  // Uses a subquery to avoid JOIN-based duplicate rows that inflate the count query.
  static Specification<ExerciseJpaEntity> hasAnyTargetMuscleGroup(List<MuscleGroup> groups) {
    return (root, query, cb) -> {
      Subquery<Long> sub = query.subquery(Long.class);
      Root<ExerciseJpaEntity> subRoot = sub.from(ExerciseJpaEntity.class);
      Join<ExerciseJpaEntity, MuscleGroup> mg = subRoot.join("targetMuscleGroups");
      sub.select(cb.literal(1L)).where(cb.equal(subRoot.get("id"), root.get("id")), mg.in(groups));
      return cb.exists(sub);
    };
  }
}
