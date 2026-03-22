package org.workie.workout.infrastructure.exercise;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface ExerciseJpaRepository
    extends JpaRepository<ExerciseJpaEntity, UUID>, JpaSpecificationExecutor<ExerciseJpaEntity> {

  @Query("SELECT e FROM ExerciseJpaEntity e WHERE e.id = :id")
  @EntityGraph(attributePaths = {"targetMuscleGroups", "secondaryMuscleGroups"})
  Optional<ExerciseJpaEntity> findByIdWithMuscleGroups(@Param("id") UUID id);
}
