package org.workie.workout.infrastructure.exercise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.workie.workout.config.TestContainersConfig;
import org.workie.workout.domain.exercise.*;
import org.workie.workout.domain.shared.DomainPage;
import org.workie.workout.domain.shared.DomainPageable;
import org.workie.workout.infrastructure.shared.PageableInfrastructureMapper;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Import({
  TestContainersConfig.class,
  ExerciseRepositoryAdapter.class,
  ExerciseInfrastructureMapperImpl.class,
  PageableInfrastructureMapper.class
})
@ActiveProfiles("test")
class ExerciseRepositoryAdapterIT {

  @Autowired private ExerciseRepositoryAdapter adapter;
  @Autowired private ExerciseJpaRepository exerciseJpaRepository;
  @Autowired private ExerciseInfrastructureMapper mapper;

  private static final ExerciseFilter EMPTY_FILTER =
      new ExerciseFilter(null, null, null, null, null);
  private static final DomainPageable PAGE_ALL = new DomainPageable(0, 1000, List.of());

  private void saveAndFlush(Exercise... exercises) {
    for (Exercise e : exercises) {
      exerciseJpaRepository.saveAndFlush(mapper.toJpa(e));
    }
  }

  private Exercise exercise(
      String name,
      MovementPattern pattern,
      List<MuscleGroup> targetGroups,
      Category category,
      Difficulty difficulty) {
    return new Exercise(
        ExerciseId.generate(), name, pattern, targetGroups, List.of(), category, difficulty);
  }

  @Test
  void shouldReturnEmptyWhenNotFoundById() {
    assertThat(adapter.findById(ExerciseId.generate())).isEmpty();
  }

  @Test
  void shouldReturnAll() {
    int count = (int) exerciseJpaRepository.count();
    DomainPage<Exercise> page =
        adapter.findAll(EMPTY_FILTER, new DomainPageable(0, count + 1, List.of()));
    assertThat(page.content()).hasSize(count);
  }

  @Test
  void shouldFilterByNameCaseInsensitive() {
    Exercise benchPress =
        exercise(
            "Filter Name Bench Press",
            MovementPattern.PUSH,
            List.of(MuscleGroup.CHEST),
            Category.STRENGTH,
            Difficulty.INTERMEDIATE);
    Exercise squat =
        exercise(
            "Filter Name Squat",
            MovementPattern.SQUAT,
            List.of(MuscleGroup.QUADRICEPS),
            Category.STRENGTH,
            Difficulty.BEGINNER);
    saveAndFlush(benchPress, squat);

    ExerciseFilter filter = new ExerciseFilter("filter name bench", null, null, null, null);
    DomainPage<Exercise> page = adapter.findAll(filter, PAGE_ALL);

    assertThat(page.content())
        .extracting(Exercise::getName)
        .containsExactly("Filter Name Bench Press");
  }

  @Test
  void shouldFilterByMovementPattern() {
    Exercise push =
        exercise(
            "Filter Pattern Push",
            MovementPattern.PUSH,
            List.of(MuscleGroup.CHEST),
            Category.STRENGTH,
            Difficulty.BEGINNER);
    Exercise squat =
        exercise(
            "Filter Pattern Squat",
            MovementPattern.SQUAT,
            List.of(MuscleGroup.QUADRICEPS),
            Category.STRENGTH,
            Difficulty.BEGINNER);
    saveAndFlush(push, squat);

    ExerciseFilter filter =
        new ExerciseFilter(null, List.of(MovementPattern.PUSH), null, null, null);
    DomainPage<Exercise> page = adapter.findAll(filter, PAGE_ALL);

    assertThat(page.content())
        .extracting(Exercise::getMovementPattern)
        .allMatch(p -> p == MovementPattern.PUSH);
    assertThat(page.content())
        .extracting(Exercise::getName)
        .contains("Filter Pattern Push")
        .doesNotContain("Filter Pattern Squat");
  }

  @Test
  void shouldFilterByMultipleMovementPatterns() {
    Exercise push =
        exercise(
            "Filter MultiPattern Push",
            MovementPattern.PUSH,
            List.of(MuscleGroup.CHEST),
            Category.STRENGTH,
            Difficulty.BEGINNER);
    Exercise squat =
        exercise(
            "Filter MultiPattern Squat",
            MovementPattern.SQUAT,
            List.of(MuscleGroup.QUADRICEPS),
            Category.STRENGTH,
            Difficulty.BEGINNER);
    Exercise carry =
        exercise(
            "Filter MultiPattern Carry",
            MovementPattern.CARRY,
            List.of(MuscleGroup.TRAPS),
            Category.STRENGTH,
            Difficulty.BEGINNER);
    saveAndFlush(push, squat, carry);

    ExerciseFilter filter =
        new ExerciseFilter(
            null, List.of(MovementPattern.PUSH, MovementPattern.SQUAT), null, null, null);
    DomainPage<Exercise> page = adapter.findAll(filter, PAGE_ALL);

    assertThat(page.content())
        .extracting(Exercise::getName)
        .contains("Filter MultiPattern Push", "Filter MultiPattern Squat")
        .doesNotContain("Filter MultiPattern Carry");
  }

  @Test
  void shouldFilterByTargetMuscleGroup() {
    Exercise benchPress =
        exercise(
            "Filter Muscle Bench Press",
            MovementPattern.PUSH,
            List.of(MuscleGroup.CHEST),
            Category.STRENGTH,
            Difficulty.INTERMEDIATE);
    Exercise squat =
        exercise(
            "Filter Muscle Squat",
            MovementPattern.SQUAT,
            List.of(MuscleGroup.QUADRICEPS),
            Category.STRENGTH,
            Difficulty.BEGINNER);
    saveAndFlush(benchPress, squat);

    ExerciseFilter filter = new ExerciseFilter(null, null, List.of(MuscleGroup.CHEST), null, null);
    DomainPage<Exercise> page = adapter.findAll(filter, PAGE_ALL);

    assertThat(page.content())
        .extracting(Exercise::getName)
        .contains("Filter Muscle Bench Press")
        .doesNotContain("Filter Muscle Squat");
  }

  @Test
  void shouldReturnCorrectCountWhenFilteringByMuscleGroup() {
    // An exercise targeting multiple muscle groups must only count as one result
    Exercise multiTarget =
        exercise(
            "Filter Count Multi Muscle",
            MovementPattern.PUSH,
            List.of(MuscleGroup.CHEST, MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
            Category.STRENGTH,
            Difficulty.INTERMEDIATE);
    saveAndFlush(multiTarget);

    ExerciseFilter filter = new ExerciseFilter(null, null, List.of(MuscleGroup.CHEST), null, null);
    DomainPage<Exercise> page = adapter.findAll(filter, PAGE_ALL);

    long count =
        page.content().stream()
            .filter(e -> e.getName().equals("Filter Count Multi Muscle"))
            .count();
    assertThat(count).isEqualTo(1);
    assertThat(page.totalElements()).isGreaterThanOrEqualTo(1);
  }

  @Test
  void shouldFilterByCategory() {
    Exercise cardio =
        exercise(
            "Filter Category Cardio Run",
            MovementPattern.LOCOMOTION,
            List.of(MuscleGroup.QUADRICEPS),
            Category.CARDIO,
            Difficulty.BEGINNER);
    Exercise strength =
        exercise(
            "Filter Category Strength Press",
            MovementPattern.PUSH,
            List.of(MuscleGroup.CHEST),
            Category.STRENGTH,
            Difficulty.INTERMEDIATE);
    saveAndFlush(cardio, strength);

    ExerciseFilter filter =
        new ExerciseFilter(null, null, null, List.of(Category.CARDIO), null);
    DomainPage<Exercise> page = adapter.findAll(filter, PAGE_ALL);

    assertThat(page.content())
        .extracting(Exercise::getCategory)
        .allMatch(c -> c == Category.CARDIO);
    assertThat(page.content())
        .extracting(Exercise::getName)
        .contains("Filter Category Cardio Run")
        .doesNotContain("Filter Category Strength Press");
  }

  @Test
  void shouldFilterByMultipleCategories() {
    Exercise cardio =
        exercise(
            "Filter MultiCat Cardio Run",
            MovementPattern.LOCOMOTION,
            List.of(MuscleGroup.QUADRICEPS),
            Category.CARDIO,
            Difficulty.BEGINNER);
    Exercise strength =
        exercise(
            "Filter MultiCat Strength Press",
            MovementPattern.PUSH,
            List.of(MuscleGroup.CHEST),
            Category.STRENGTH,
            Difficulty.INTERMEDIATE);
    Exercise mobility =
        exercise(
            "Filter MultiCat Mobility Stretch",
            MovementPattern.ISOMETRIC,
            List.of(MuscleGroup.HAMSTRINGS),
            Category.MOBILITY,
            Difficulty.BEGINNER);
    saveAndFlush(cardio, strength, mobility);

    ExerciseFilter filter =
        new ExerciseFilter(
            null, null, null, List.of(Category.CARDIO, Category.STRENGTH), null);
    DomainPage<Exercise> page = adapter.findAll(filter, PAGE_ALL);

    assertThat(page.content())
        .extracting(Exercise::getName)
        .contains("Filter MultiCat Cardio Run", "Filter MultiCat Strength Press")
        .doesNotContain("Filter MultiCat Mobility Stretch");
  }

  @Test
  void shouldFilterByDifficulty() {
    Exercise beginner =
        exercise(
            "Filter Diff Beginner Run",
            MovementPattern.LOCOMOTION,
            List.of(MuscleGroup.QUADRICEPS),
            Category.CARDIO,
            Difficulty.BEGINNER);
    Exercise advanced =
        exercise(
            "Filter Diff Advanced Snatch",
            MovementPattern.BALLISTIC,
            List.of(MuscleGroup.GLUTES),
            Category.POWER,
            Difficulty.ADVANCED);
    saveAndFlush(beginner, advanced);

    ExerciseFilter filter =
        new ExerciseFilter(null, null, null, null, List.of(Difficulty.BEGINNER));
    DomainPage<Exercise> page = adapter.findAll(filter, PAGE_ALL);

    assertThat(page.content())
        .extracting(Exercise::getDifficulty)
        .allMatch(d -> d == Difficulty.BEGINNER);
    assertThat(page.content())
        .extracting(Exercise::getName)
        .contains("Filter Diff Beginner Run")
        .doesNotContain("Filter Diff Advanced Snatch");
  }

  @Test
  void shouldFilterByMultipleDifficulties() {
    Exercise beginner =
        exercise(
            "Filter MultiDiff Beginner Run",
            MovementPattern.LOCOMOTION,
            List.of(MuscleGroup.QUADRICEPS),
            Category.CARDIO,
            Difficulty.BEGINNER);
    Exercise intermediate =
        exercise(
            "Filter MultiDiff Intermediate Press",
            MovementPattern.PUSH,
            List.of(MuscleGroup.CHEST),
            Category.STRENGTH,
            Difficulty.INTERMEDIATE);
    Exercise advanced =
        exercise(
            "Filter MultiDiff Advanced Snatch",
            MovementPattern.BALLISTIC,
            List.of(MuscleGroup.GLUTES),
            Category.POWER,
            Difficulty.ADVANCED);
    saveAndFlush(beginner, intermediate, advanced);

    ExerciseFilter filter =
        new ExerciseFilter(
            null, null, null, null, List.of(Difficulty.BEGINNER, Difficulty.INTERMEDIATE));
    DomainPage<Exercise> page = adapter.findAll(filter, PAGE_ALL);

    assertThat(page.content())
        .extracting(Exercise::getName)
        .contains("Filter MultiDiff Beginner Run", "Filter MultiDiff Intermediate Press")
        .doesNotContain("Filter MultiDiff Advanced Snatch");
  }

  @Test
  void shouldCombineFiltersWithAndBehavior() {
    Exercise match =
        exercise(
            "Filter And Match Press",
            MovementPattern.PUSH,
            List.of(MuscleGroup.CHEST),
            Category.STRENGTH,
            Difficulty.INTERMEDIATE);
    Exercise wrongCategory =
        exercise(
            "Filter And Wrong Category Press",
            MovementPattern.PUSH,
            List.of(MuscleGroup.CHEST),
            Category.POWER,
            Difficulty.INTERMEDIATE);
    Exercise wrongPattern =
        exercise(
            "Filter And Wrong Pattern Squat",
            MovementPattern.SQUAT,
            List.of(MuscleGroup.QUADRICEPS),
            Category.STRENGTH,
            Difficulty.INTERMEDIATE);
    saveAndFlush(match, wrongCategory, wrongPattern);

    ExerciseFilter filter =
        new ExerciseFilter(
            "filter and",
            List.of(MovementPattern.PUSH),
            null,
            List.of(Category.STRENGTH),
            null);
    DomainPage<Exercise> page = adapter.findAll(filter, PAGE_ALL);

    assertThat(page.content())
        .extracting(Exercise::getName)
        .contains("Filter And Match Press")
        .doesNotContain("Filter And Wrong Category Press", "Filter And Wrong Pattern Squat");
  }

  @Test
  void shouldReturnEmptyWhenFilterMatchesNothing() {
    ExerciseFilter filter = new ExerciseFilter("xyzzy_no_match_ever", null, null, null, null);
    DomainPage<Exercise> page = adapter.findAll(filter, PAGE_ALL);

    assertThat(page.content()).isEmpty();
    assertThat(page.totalElements()).isZero();
  }
}
