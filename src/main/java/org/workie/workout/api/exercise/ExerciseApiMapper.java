package org.workie.workout.api.exercise;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.workie.workout.api.contract.exercise.ExerciseCategory;
import org.workie.workout.api.contract.exercise.ExerciseDifficulty;
import org.workie.workout.api.contract.exercise.ExerciseMovementPattern;
import org.workie.workout.api.contract.exercise.ExerciseMuscleGroup;
import org.workie.workout.api.contract.exercise.ExercisePage;
import org.workie.workout.api.contract.exercise.ExerciseResponse;
import org.workie.workout.domain.exercise.Category;
import org.workie.workout.domain.exercise.Difficulty;
import org.workie.workout.domain.exercise.Exercise;
import org.workie.workout.domain.exercise.MovementPattern;
import org.workie.workout.domain.exercise.MuscleGroup;
import org.workie.workout.domain.shared.DomainPage;

@Mapper(componentModel = SPRING)
interface ExerciseApiMapper {

  @Mapping(target = "id", source = "id.value")
  ExerciseResponse toResponse(Exercise exercise);

  @Mapping(target = "numberOfElements", expression = "java(domainPage.numberOfElements())")
  ExercisePage toPage(DomainPage<Exercise> domainPage);

  default ExerciseMovementPattern toMovementPattern(MovementPattern domain) {
    return domain == null ? null : ExerciseMovementPattern.valueOf(domain.name());
  }

  default ExerciseMuscleGroup toMuscleGroup(MuscleGroup domain) {
    return domain == null ? null : ExerciseMuscleGroup.valueOf(domain.name());
  }

  default ExerciseCategory toCategory(Category domain) {
    return domain == null ? null : ExerciseCategory.valueOf(domain.name());
  }

  default ExerciseDifficulty toDifficulty(Difficulty domain) {
    return domain == null ? null : ExerciseDifficulty.valueOf(domain.name());
  }

  default MovementPattern toDomainMovementPattern(ExerciseMovementPattern api) {
    return api == null ? null : MovementPattern.valueOf(api.name());
  }

  default MuscleGroup toDomainMuscleGroup(ExerciseMuscleGroup api) {
    return api == null ? null : MuscleGroup.valueOf(api.name());
  }

  default Category toDomainCategory(ExerciseCategory api) {
    return api == null ? null : Category.valueOf(api.name());
  }

  default Difficulty toDomainDifficulty(ExerciseDifficulty api) {
    return api == null ? null : Difficulty.valueOf(api.name());
  }
}
