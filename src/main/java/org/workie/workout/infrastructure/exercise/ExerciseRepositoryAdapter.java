package org.workie.workout.infrastructure.exercise;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.workie.workout.domain.exercise.Exercise;
import org.workie.workout.domain.exercise.ExerciseFilter;
import org.workie.workout.domain.exercise.ExerciseId;
import org.workie.workout.domain.exercise.ExerciseRepository;
import org.workie.workout.domain.shared.DomainPage;
import org.workie.workout.domain.shared.DomainPageable;
import org.workie.workout.infrastructure.shared.PageableInfrastructureMapper;

@RequiredArgsConstructor
@Repository
public class ExerciseRepositoryAdapter implements ExerciseRepository {

  private final ExerciseJpaRepository jpaRepository;
  private final ExerciseInfrastructureMapper mapper;
  private final PageableInfrastructureMapper pageableInfrastructureMapper;

  @Override
  public Optional<Exercise> findById(ExerciseId id) {
    return jpaRepository.findByIdWithMuscleGroups(id.value()).map(mapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public DomainPage<Exercise> findAll(ExerciseFilter filter, DomainPageable domainPageable) {
    Pageable pageable = pageableInfrastructureMapper.toPageable(domainPageable);
    Specification<ExerciseJpaEntity> spec = buildSpec(filter);
    return pageableInfrastructureMapper.toDomainPage(
        jpaRepository.findAll(spec, pageable).map(mapper::toDomain));
  }

  private Specification<ExerciseJpaEntity> buildSpec(ExerciseFilter filter) {
    Specification<ExerciseJpaEntity> spec = (_, _, cb) -> cb.conjunction();
    if (filter.name() != null) {
      spec = spec.and(ExerciseSpecifications.nameLike(filter.name()));
    }
    if (filter.movementPatterns() != null) {
      spec = spec.and(ExerciseSpecifications.movementPatternIn(filter.movementPatterns()));
    }
    if (filter.targetMuscleGroups() != null) {
      spec = spec.and(ExerciseSpecifications.hasAnyTargetMuscleGroup(filter.targetMuscleGroups()));
    }
    if (filter.categories() != null) {
      spec = spec.and(ExerciseSpecifications.categoryIn(filter.categories()));
    }
    if (filter.difficulties() != null) {
      spec = spec.and(ExerciseSpecifications.difficultyIn(filter.difficulties()));
    }
    return spec;
  }
}
