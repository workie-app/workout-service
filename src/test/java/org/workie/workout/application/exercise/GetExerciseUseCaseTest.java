package org.workie.workout.application.exercise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.workie.workout.domain.exercise.Category;
import org.workie.workout.domain.exercise.Difficulty;
import org.workie.workout.domain.exercise.Exercise;
import org.workie.workout.domain.exercise.ExerciseId;
import org.workie.workout.domain.exercise.ExerciseNotFoundException;
import org.workie.workout.domain.exercise.ExerciseRepository;
import org.workie.workout.domain.exercise.MovementPattern;
import org.workie.workout.domain.exercise.MuscleGroup;

@ExtendWith(MockitoExtension.class)
class GetExerciseUseCaseTest {

  @Mock private ExerciseRepository exerciseRepository;

  @InjectMocks private GetExerciseUseCase getExerciseUseCase;

  private Exercise exercise(ExerciseId id) {
    return new Exercise(
        id,
        "Bench Press",
        MovementPattern.PUSH,
        List.of(MuscleGroup.CHEST),
        List.of(MuscleGroup.TRICEPS),
        Category.STRENGTH,
        Difficulty.INTERMEDIATE);
  }

  @Test
  void shouldReturnExerciseWhenFound() {
    ExerciseId id = ExerciseId.generate();
    Exercise expected = exercise(id);
    when(exerciseRepository.findById(id)).thenReturn(Optional.of(expected));

    Exercise result = getExerciseUseCase.execute(new GetExerciseCommand(id));

    assertThat(result).isEqualTo(expected);
    verify(exerciseRepository).findById(id);
  }

  @Test
  void shouldThrowExerciseNotFoundExceptionWhenNotFound() {
    ExerciseId id = ExerciseId.generate();
    when(exerciseRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> getExerciseUseCase.execute(new GetExerciseCommand(id)))
        .isInstanceOf(ExerciseNotFoundException.class)
        .hasMessageContaining(id.value().toString());
  }
}
