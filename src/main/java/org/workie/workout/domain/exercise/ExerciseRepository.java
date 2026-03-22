package org.workie.workout.domain.exercise;

import java.util.Optional;
import org.workie.workout.domain.shared.DomainPage;
import org.workie.workout.domain.shared.DomainPageable;

public interface ExerciseRepository {

  Optional<Exercise> findById(ExerciseId id);

  DomainPage<Exercise> findAll(ExerciseFilter filter, DomainPageable domainPageable);
}
