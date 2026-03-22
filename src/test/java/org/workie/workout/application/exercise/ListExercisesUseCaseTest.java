package org.workie.workout.application.exercise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.workie.workout.domain.exercise.*;
import org.workie.workout.domain.shared.DomainPage;
import org.workie.workout.domain.shared.DomainPageable;
import org.workie.workout.domain.shared.DomainSort;

@ExtendWith(MockitoExtension.class)
class ListExercisesUseCaseTest {

  @Mock private ExerciseRepository exerciseRepository;

  @InjectMocks private ListExercisesUseCase listExercisesUseCase;

  private static final ExerciseFilter EMPTY_FILTER =
      new ExerciseFilter(null, null, null, null, null);
  private static final DomainPageable NAME_ASC_20 =
      new DomainPageable(0, 20, List.of(DomainSort.of("name")));

  private Exercise exercise(String name) {
    return new Exercise(
        ExerciseId.generate(),
        name,
        MovementPattern.PUSH,
        List.of(MuscleGroup.CHEST),
        List.of(MuscleGroup.TRICEPS),
        Category.STRENGTH,
        Difficulty.INTERMEDIATE);
  }

  @Test
  void shouldReturnPageOfExercises() {
    List<Exercise> exercises = List.of(exercise("Bench Press"), exercise("Running"));
    DomainPage<Exercise> page = new DomainPage<>(exercises, 2, 1, 0, 20);
    when(exerciseRepository.findAll(EMPTY_FILTER, NAME_ASC_20)).thenReturn(page);

    DomainPage<Exercise> result =
        listExercisesUseCase.execute(
            new ListExercisesCommand(EMPTY_FILTER, new DomainPageable(0, 20, List.of())));

    assertThat(result.content()).hasSize(2);
    assertThat(result.totalElements()).isEqualTo(2);
    assertThat(result.totalPages()).isEqualTo(1);
    assertThat(result.number()).isZero();
    verify(exerciseRepository).findAll(EMPTY_FILTER, NAME_ASC_20);
  }

  @Test
  void shouldReturnEmptyPageWhenNoExercises() {
    DomainPage<Exercise> emptyPage = new DomainPage<>(List.of(), 0, 0, 0, 20);
    when(exerciseRepository.findAll(EMPTY_FILTER, NAME_ASC_20)).thenReturn(emptyPage);

    DomainPage<Exercise> result =
        listExercisesUseCase.execute(
            new ListExercisesCommand(EMPTY_FILTER, new DomainPageable(0, 20, List.of())));

    assertThat(result.content()).isEmpty();
    assertThat(result.totalElements()).isZero();
    assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void shouldDefaultToNameAscWhenNoSortProvided() {
    DomainPage<Exercise> page = new DomainPage<>(List.of(), 0, 0, 0, 20);
    DomainPageable expectedPageable = new DomainPageable(0, 20, List.of(DomainSort.of("name")));
    when(exerciseRepository.findAll(EMPTY_FILTER, expectedPageable)).thenReturn(page);

    listExercisesUseCase.execute(
        new ListExercisesCommand(EMPTY_FILTER, new DomainPageable(0, 20, List.of())));

    verify(exerciseRepository).findAll(EMPTY_FILTER, expectedPageable);
  }

  @Test
  void shouldDefaultToNameAscWhenSortFieldIsInvalid() {
    DomainPageable inputPageable =
        new DomainPageable(0, 20, List.of(new DomainSort("difficulty", DomainSort.Direction.ASC)));
    DomainPageable expectedPageable = new DomainPageable(0, 20, List.of(DomainSort.of("name")));
    DomainPage<Exercise> page = new DomainPage<>(List.of(), 0, 0, 0, 20);
    when(exerciseRepository.findAll(EMPTY_FILTER, expectedPageable)).thenReturn(page);

    listExercisesUseCase.execute(new ListExercisesCommand(EMPTY_FILTER, inputPageable));

    verify(exerciseRepository).findAll(EMPTY_FILTER, expectedPageable);
  }

  @Test
  void shouldPreserveNameDescSort() {
    DomainPageable inputPageable =
        new DomainPageable(0, 20, List.of(new DomainSort("name", DomainSort.Direction.DESC)));
    DomainPageable expectedPageable =
        new DomainPageable(0, 20, List.of(new DomainSort("name", DomainSort.Direction.DESC)));
    DomainPage<Exercise> page = new DomainPage<>(List.of(), 0, 0, 0, 20);
    when(exerciseRepository.findAll(EMPTY_FILTER, expectedPageable)).thenReturn(page);

    listExercisesUseCase.execute(new ListExercisesCommand(EMPTY_FILTER, inputPageable));

    verify(exerciseRepository).findAll(EMPTY_FILTER, expectedPageable);
  }

  @Test
  void shouldPassFilterToRepository() {
    ExerciseFilter filter =
        new ExerciseFilter(
            "bench",
            List.of(MovementPattern.PUSH),
            List.of(MuscleGroup.CHEST),
            List.of(Category.STRENGTH),
            List.of(Difficulty.INTERMEDIATE));
    DomainPage<Exercise> page = new DomainPage<>(List.of(exercise("Bench Press")), 1, 1, 0, 20);
    when(exerciseRepository.findAll(filter, NAME_ASC_20)).thenReturn(page);

    DomainPage<Exercise> result =
        listExercisesUseCase.execute(
            new ListExercisesCommand(filter, new DomainPageable(0, 20, List.of())));

    assertThat(result.content()).hasSize(1);
    verify(exerciseRepository).findAll(filter, NAME_ASC_20);
  }
}
