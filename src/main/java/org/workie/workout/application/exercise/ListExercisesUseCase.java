package org.workie.workout.application.exercise;

import java.util.List;
import org.workie.workout.domain.exercise.Exercise;
import org.workie.workout.domain.exercise.ExerciseRepository;
import org.workie.workout.domain.shared.DomainPage;
import org.workie.workout.domain.shared.DomainPageable;
import org.workie.workout.domain.shared.DomainSort;

public class ListExercisesUseCase {

  private static final String SORT_FIELD_NAME = "name";

  private final ExerciseRepository exerciseRepository;

  public ListExercisesUseCase(ExerciseRepository exerciseRepository) {
    this.exerciseRepository = exerciseRepository;
  }

  public DomainPage<Exercise> execute(ListExercisesCommand command) {
    DomainSort resolvedSort =
        command.pageable().sort().stream()
            .filter(s -> SORT_FIELD_NAME.equals(s.field()))
            .findFirst()
            .orElse(DomainSort.of(SORT_FIELD_NAME));
    DomainPageable resolvedPageable =
        new DomainPageable(
            command.pageable().page(), command.pageable().size(), List.of(resolvedSort));
    return exerciseRepository.findAll(command.filter(), resolvedPageable);
  }
}
