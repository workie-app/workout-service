package org.workie.workout.infrastructure.exercise;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.workie.workout.domain.exercise.Exercise;
import org.workie.workout.domain.exercise.ExerciseId;

@Mapper(componentModel = SPRING)
interface ExerciseInfrastructureMapper {

  @Mapping(target = "id", source = "id.value")
  ExerciseJpaEntity toJpa(Exercise exercise);

  Exercise toDomain(ExerciseJpaEntity entity);

  default ExerciseId toExerciseId(UUID uuid) {
    return ExerciseId.of(uuid);
  }
}
