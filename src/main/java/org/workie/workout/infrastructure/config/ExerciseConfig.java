package org.workie.workout.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.workie.workout.application.exercise.ListExercisesUseCase;
import org.workie.workout.domain.exercise.ExerciseRepository;

@Configuration
class ExerciseConfig {

  @Bean
  ListExercisesUseCase listExercisesUseCase(ExerciseRepository exerciseRepository) {
    return new ListExercisesUseCase(exerciseRepository);
  }
}
