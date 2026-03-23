package org.workie.workout.api.exercise;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.workie.workout.api.contract.exercise.ExerciseCategory;
import org.workie.workout.api.contract.exercise.ExerciseDifficulty;
import org.workie.workout.api.contract.exercise.ExerciseMovementPattern;
import org.workie.workout.api.contract.exercise.ExerciseMuscleGroup;
import org.workie.workout.domain.exercise.Category;
import org.workie.workout.domain.exercise.Difficulty;
import org.workie.workout.domain.exercise.MovementPattern;
import org.workie.workout.domain.exercise.MuscleGroup;

@ExtendWith(SpringExtension.class)
@Import(ExerciseApiMapperImpl.class)
class ExerciseApiMapperTest {

  @Autowired private ExerciseApiMapper mapper;

  @Test
  void shouldReturnNullWhenMovementPatternDomainIsNull() {
    assertThat(mapper.toMovementPattern(null)).isNull();
  }

  @Test
  void shouldMapMovementPattern() {
    assertThat(mapper.toMovementPattern(MovementPattern.PUSH))
        .isEqualTo(ExerciseMovementPattern.PUSH);
  }

  @Test
  void shouldReturnNullWhenMuscleGroupDomainIsNull() {
    assertThat(mapper.toMuscleGroup(null)).isNull();
  }

  @Test
  void shouldMapMuscleGroup() {
    assertThat(mapper.toMuscleGroup(MuscleGroup.CHEST)).isEqualTo(ExerciseMuscleGroup.CHEST);
  }

  @Test
  void shouldReturnNullWhenCategoryDomainIsNull() {
    assertThat(mapper.toCategory(null)).isNull();
  }

  @Test
  void shouldMapCategory() {
    assertThat(mapper.toCategory(Category.STRENGTH)).isEqualTo(ExerciseCategory.STRENGTH);
  }

  @Test
  void shouldReturnNullWhenDifficultyDomainIsNull() {
    assertThat(mapper.toDifficulty(null)).isNull();
  }

  @Test
  void shouldMapDifficulty() {
    assertThat(mapper.toDifficulty(Difficulty.INTERMEDIATE))
        .isEqualTo(ExerciseDifficulty.INTERMEDIATE);
  }

  @Test
  void shouldReturnNullWhenDomainMovementPatternApiIsNull() {
    assertThat(mapper.toDomainMovementPattern(null)).isNull();
  }

  @Test
  void shouldMapDomainMovementPattern() {
    assertThat(mapper.toDomainMovementPattern(ExerciseMovementPattern.PULL))
        .isEqualTo(MovementPattern.PULL);
  }

  @Test
  void shouldReturnNullWhenDomainMuscleGroupApiIsNull() {
    assertThat(mapper.toDomainMuscleGroup(null)).isNull();
  }

  @Test
  void shouldMapDomainMuscleGroup() {
    assertThat(mapper.toDomainMuscleGroup(ExerciseMuscleGroup.BACK)).isEqualTo(MuscleGroup.BACK);
  }

  @Test
  void shouldReturnNullWhenDomainCategoryApiIsNull() {
    assertThat(mapper.toDomainCategory(null)).isNull();
  }

  @Test
  void shouldMapDomainCategory() {
    assertThat(mapper.toDomainCategory(ExerciseCategory.CARDIO)).isEqualTo(Category.CARDIO);
  }

  @Test
  void shouldReturnNullWhenDomainDifficultyApiIsNull() {
    assertThat(mapper.toDomainDifficulty(null)).isNull();
  }

  @Test
  void shouldMapDomainDifficulty() {
    assertThat(mapper.toDomainDifficulty(ExerciseDifficulty.BEGINNER))
        .isEqualTo(Difficulty.BEGINNER);
  }
}
