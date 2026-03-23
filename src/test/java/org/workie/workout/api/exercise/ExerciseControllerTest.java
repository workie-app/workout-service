package org.workie.workout.api.exercise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.workie.workout.api.shared.PageableApiMapper;
import org.workie.workout.application.exercise.GetExerciseCommand;
import org.workie.workout.application.exercise.GetExerciseUseCase;
import org.workie.workout.application.exercise.ListExercisesCommand;
import org.workie.workout.application.exercise.ListExercisesUseCase;
import org.workie.workout.domain.exercise.Category;
import org.workie.workout.domain.exercise.Difficulty;
import org.workie.workout.domain.exercise.Exercise;
import org.workie.workout.domain.exercise.ExerciseId;
import org.workie.workout.domain.exercise.ExerciseNotFoundException;
import org.workie.workout.domain.exercise.MovementPattern;
import org.workie.workout.domain.exercise.MuscleGroup;
import org.workie.workout.domain.shared.DomainPage;

@WebMvcTest(ExerciseController.class)
@Import({ExerciseApiMapperImpl.class, PageableApiMapper.class})
class ExerciseControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ListExercisesUseCase listExercisesUseCase;
  @MockitoBean private GetExerciseUseCase getExerciseUseCase;

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

  private DomainPage<Exercise> emptyPage() {
    return new DomainPage<>(List.of(), 0, 1, 0, 10);
  }

  private ArgumentCaptor<ListExercisesCommand> captureCommand() {
    ArgumentCaptor<ListExercisesCommand> captor =
        ArgumentCaptor.forClass(ListExercisesCommand.class);
    verify(listExercisesUseCase).execute(captor.capture());
    return captor;
  }

  @Test
  void shouldReturn200WithCorrectResponseSchema() throws Exception {
    List<Exercise> exercises = List.of(exercise("Bench Press"), exercise("Push-Up"));
    DomainPage<Exercise> page = new DomainPage<>(exercises, 2, 1, 0, 10);
    when(listExercisesUseCase.execute(any())).thenReturn(page);

    mockMvc
        .perform(get("/exercises").param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].name").value("Bench Press"))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.number").value(0))
        .andExpect(jsonPath("$.size").value(10))
        .andExpect(jsonPath("$.numberOfElements").value(2))
        .andExpect(jsonPath("$.first").value(true))
        .andExpect(jsonPath("$.last").value(true))
        .andExpect(jsonPath("$.empty").value(false));
  }

  @Test
  void shouldPassPaginationParamsToUseCase() throws Exception {
    DomainPage<Exercise> page = new DomainPage<>(List.of(), 0, 1, 2, 5);
    when(listExercisesUseCase.execute(any())).thenReturn(page);

    mockMvc
        .perform(get("/exercises").param("page", "2").param("size", "5"))
        .andExpect(status().isOk());

    ListExercisesCommand command = captureCommand().getValue();
    assertThat(command.pageable().page()).isEqualTo(2);
    assertThat(command.pageable().size()).isEqualTo(5);
  }

  @Test
  void shouldPassSortParamsToUseCase() throws Exception {
    when(listExercisesUseCase.execute(any())).thenReturn(emptyPage());

    mockMvc
        .perform(get("/exercises").param("page", "0").param("size", "10").param("sort", "name,asc"))
        .andExpect(status().isOk());

    ListExercisesCommand command = captureCommand().getValue();
    assertThat(command.pageable().sort()).hasSize(1);
    assertThat(command.pageable().sort().getFirst().field()).isEqualTo("name");
  }

  @Test
  void shouldReturnEmptyPageWhenNoResults() throws Exception {
    when(listExercisesUseCase.execute(any())).thenReturn(emptyPage());

    mockMvc
        .perform(get("/exercises").param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(0))
        .andExpect(jsonPath("$.totalElements").value(0))
        .andExpect(jsonPath("$.numberOfElements").value(0))
        .andExpect(jsonPath("$.empty").value(true));
  }

  @Test
  void shouldPassEmptyFilterWhenNoFilterParamsProvided() throws Exception {
    when(listExercisesUseCase.execute(any())).thenReturn(emptyPage());

    mockMvc
        .perform(get("/exercises").param("page", "0").param("size", "10"))
        .andExpect(status().isOk());

    ListExercisesCommand command = captureCommand().getValue();
    assertThat(command.filter().name()).isNull();
    assertThat(command.filter().movementPatterns()).isNull();
    assertThat(command.filter().targetMuscleGroups()).isNull();
    assertThat(command.filter().categories()).isNull();
    assertThat(command.filter().difficulties()).isNull();
  }

  @Test
  void shouldPassNameFilterToUseCase() throws Exception {
    when(listExercisesUseCase.execute(any())).thenReturn(emptyPage());

    mockMvc.perform(get("/exercises").param("name", "bench")).andExpect(status().isOk());

    ListExercisesCommand command = captureCommand().getValue();
    assertThat(command.filter().name()).isEqualTo("bench");
  }

  @Test
  void shouldPassMovementPatternsFilterToUseCase() throws Exception {
    when(listExercisesUseCase.execute(any())).thenReturn(emptyPage());

    mockMvc
        .perform(
            get("/exercises").param("movementPatterns", "PUSH").param("movementPatterns", "PULL"))
        .andExpect(status().isOk());

    ListExercisesCommand command = captureCommand().getValue();
    assertThat(command.filter().movementPatterns())
        .containsExactlyInAnyOrder(MovementPattern.PUSH, MovementPattern.PULL);
  }

  @Test
  void shouldPassTargetMuscleGroupsFilterToUseCase() throws Exception {
    when(listExercisesUseCase.execute(any())).thenReturn(emptyPage());

    mockMvc
        .perform(
            get("/exercises")
                .param("targetMuscleGroups", "CHEST")
                .param("targetMuscleGroups", "BACK"))
        .andExpect(status().isOk());

    ListExercisesCommand command = captureCommand().getValue();
    assertThat(command.filter().targetMuscleGroups())
        .containsExactlyInAnyOrder(MuscleGroup.CHEST, MuscleGroup.BACK);
  }

  @Test
  void shouldPassCategoriesFilterToUseCase() throws Exception {
    when(listExercisesUseCase.execute(any())).thenReturn(emptyPage());

    mockMvc
        .perform(get("/exercises").param("categories", "STRENGTH").param("categories", "CARDIO"))
        .andExpect(status().isOk());

    ListExercisesCommand command = captureCommand().getValue();
    assertThat(command.filter().categories())
        .containsExactlyInAnyOrder(Category.STRENGTH, Category.CARDIO);
  }

  @Test
  void shouldPassDifficultiesFilterToUseCase() throws Exception {
    when(listExercisesUseCase.execute(any())).thenReturn(emptyPage());

    mockMvc
        .perform(
            get("/exercises")
                .param("difficulties", "BEGINNER")
                .param("difficulties", "INTERMEDIATE"))
        .andExpect(status().isOk());

    ListExercisesCommand command = captureCommand().getValue();
    assertThat(command.filter().difficulties())
        .containsExactlyInAnyOrder(Difficulty.BEGINNER, Difficulty.INTERMEDIATE);
  }

  @Test
  void shouldReturn200WithDefaultSortWhenSortFieldIsInvalid() throws Exception {
    when(listExercisesUseCase.execute(any())).thenReturn(emptyPage());

    mockMvc.perform(get("/exercises").param("sort", "difficulty,asc")).andExpect(status().isOk());
  }

  @Test
  void shouldReturn400WithCleanMessageWhenInvalidEnumValue() throws Exception {
    mockMvc
        .perform(get("/exercises").param("movementPatterns", "ISOMETRI"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.detail", containsString("ISOMETRI")))
        .andExpect(jsonPath("$.detail", containsString("movementPatterns")))
        .andExpect(jsonPath("$.detail", containsString("ISOMETRIC")));
  }

  @Test
  void shouldReturn200WithExerciseWhenFound() throws Exception {
    ExerciseId id = ExerciseId.generate();
    Exercise ex = exercise("Bench Press");
    when(getExerciseUseCase.execute(any())).thenReturn(ex);

    mockMvc
        .perform(get("/exercises/{id}", id.value()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Bench Press"))
        .andExpect(jsonPath("$.movementPattern").value("PUSH"))
        .andExpect(jsonPath("$.category").value("STRENGTH"))
        .andExpect(jsonPath("$.difficulty").value("INTERMEDIATE"))
        .andExpect(jsonPath("$.targetMuscleGroups[0]").value("CHEST"));
  }

  @Test
  void shouldReturn404WhenExerciseNotFound() throws Exception {
    ExerciseId id = ExerciseId.generate();
    when(getExerciseUseCase.execute(any())).thenThrow(new ExerciseNotFoundException(id));

    mockMvc
        .perform(get("/exercises/{id}", id.value()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404));
  }

  @Test
  void shouldReturn400WhenIdIsInvalidUuid() throws Exception {
    mockMvc.perform(get("/exercises/not-a-uuid")).andExpect(status().isBadRequest());
  }

  @Test
  void shouldPassCorrectExerciseIdToUseCase() throws Exception {
    UUID uuid = UUID.randomUUID();
    ExerciseId expectedId = ExerciseId.of(uuid);
    when(getExerciseUseCase.execute(any())).thenReturn(exercise("Squat"));

    mockMvc.perform(get("/exercises/{id}", uuid)).andExpect(status().isOk());

    ArgumentCaptor<GetExerciseCommand> captor = ArgumentCaptor.forClass(GetExerciseCommand.class);
    verify(getExerciseUseCase).execute(captor.capture());
    assertThat(captor.getValue().exerciseId()).isEqualTo(expectedId);
  }
}
