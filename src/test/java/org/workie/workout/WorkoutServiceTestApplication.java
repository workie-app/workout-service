package org.workie.workout;

import org.springframework.boot.SpringApplication;
import org.workie.workout.config.TestContainersConfig;

public class WorkoutServiceTestApplication {

  static void main(String[] args) {
    SpringApplication.from(WorkoutServiceApplication::main)
        .with(TestContainersConfig.class)
        .withAdditionalProfiles("dev")
        .run(args);
  }
}
