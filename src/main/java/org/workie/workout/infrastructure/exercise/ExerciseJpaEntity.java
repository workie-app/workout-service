package org.workie.workout.infrastructure.exercise;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import org.workie.workout.domain.exercise.Difficulty;
import org.workie.workout.domain.exercise.Category;
import org.workie.workout.domain.exercise.MovementPattern;
import org.workie.workout.domain.exercise.MuscleGroup;

@Getter
@Setter
@Entity
@Table(name = "exercise", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
class ExerciseJpaEntity {

  @Id private UUID id;

  private String name;

  @Enumerated(EnumType.STRING)
  private MovementPattern movementPattern;

  @ElementCollection
  @CollectionTable(
      name = "exercise_target_muscle_groups",
      joinColumns = @JoinColumn(name = "exercise_id"))
  @Column(name = "muscle_group")
  @Enumerated(EnumType.STRING)
  @BatchSize(size = 250)
  private Set<MuscleGroup> targetMuscleGroups;

  @ElementCollection
  @CollectionTable(
      name = "exercise_secondary_muscle_groups",
      joinColumns = @JoinColumn(name = "exercise_id"))
  @Column(name = "muscle_group")
  @Enumerated(EnumType.STRING)
  @BatchSize(size = 250)
  private Set<MuscleGroup> secondaryMuscleGroups;

  @Enumerated(EnumType.STRING)
  private Category category;

  @Enumerated(EnumType.STRING)
  private Difficulty difficulty;
}
