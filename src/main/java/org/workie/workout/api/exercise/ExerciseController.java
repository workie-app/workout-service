package org.workie.workout.api.exercise;

import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.workie.workout.api.contract.exercise.ExerciseCategory;
import org.workie.workout.api.contract.exercise.ExerciseDifficulty;
import org.workie.workout.api.contract.exercise.ExerciseMovementPattern;
import org.workie.workout.api.contract.exercise.ExerciseMuscleGroup;
import org.workie.workout.api.contract.exercise.ExercisePage;
import org.workie.workout.api.contract.exercise.ExercisesApi;
import org.workie.workout.api.shared.PageableApiMapper;
import org.workie.workout.application.exercise.ListExercisesCommand;
import org.workie.workout.application.exercise.ListExercisesUseCase;
import org.workie.workout.domain.exercise.ExerciseFilter;
import org.workie.workout.domain.shared.DomainPageable;

@RequiredArgsConstructor
@RestController
public class ExerciseController implements ExercisesApi {

  private final PageableApiMapper pageableApiMapper;
  private final ExerciseApiMapper exerciseMapper;
  private final ListExercisesUseCase listExercisesUseCase;

  @Override
  public ResponseEntity<ExercisePage> listExercises(
      @Nullable String name,
      @Nullable List<ExerciseMovementPattern> movementPatterns,
      @Nullable List<ExerciseMuscleGroup> targetMuscleGroups,
      @Nullable List<ExerciseCategory> categories,
      @Nullable List<ExerciseDifficulty> difficulties,
      Pageable pageable) {
    DomainPageable domainPageable = pageableApiMapper.toPageRequest(pageable);
    ExerciseFilter filter =
        new ExerciseFilter(
            name,
            toMapped(movementPatterns, exerciseMapper::toDomainMovementPattern),
            toMapped(targetMuscleGroups, exerciseMapper::toDomainMuscleGroup),
            toMapped(categories, exerciseMapper::toDomainCategory),
            toMapped(difficulties, exerciseMapper::toDomainDifficulty));
    return ResponseEntity.ok(
        exerciseMapper.toPage(
            listExercisesUseCase.execute(new ListExercisesCommand(filter, domainPageable))));
  }

  private <A, B> List<B> toMapped(@Nullable List<A> input, Function<A, B> mapper) {
    return input == null ? null : input.stream().map(mapper).toList();
  }
}
