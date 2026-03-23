package org.workie.workout.api.exercise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.workie.workout.config.TestContainersConfig;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainersConfig.class)
@ActiveProfiles("test")
class ExerciseControllerIT {

  // All tests filter by "IT Exercise" to read exclusively from V900 test data,
  // so they remain isolated from application migration data.
  private static final String IT_NAME_PREFIX = "IT Exercise";

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldReturnPagedExercises() throws Exception {
    // V900 contains exactly 15 "IT Exercise" entries; page 0 size 10 must return 10
    mockMvc
        .perform(
            get("/exercises").param("name", IT_NAME_PREFIX).param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(10))
        .andExpect(jsonPath("$.totalElements").value(15))
        .andExpect(jsonPath("$.totalPages").value(2))
        .andExpect(jsonPath("$.number").value(0))
        .andExpect(jsonPath("$.size").value(10))
        .andExpect(jsonPath("$.first").value(true))
        .andExpect(jsonPath("$.numberOfElements").value(10));
  }

  @Test
  void shouldReturnLastPageCorrectly() throws Exception {
    mockMvc
        .perform(
            get("/exercises")
                .param("name", IT_NAME_PREFIX)
                .param("page", "1000")
                .param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(0))
        .andExpect(jsonPath("$.last").value(true))
        .andExpect(jsonPath("$.empty").value(true));
  }

  @Test
  void shouldSortByNameAscending() throws Exception {
    // 15 IT exercises sorted ascending: A01 comes first
    mockMvc
        .perform(
            get("/exercises")
                .param("name", IT_NAME_PREFIX)
                .param("page", "0")
                .param("size", "5")
                .param("sort", "name,asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(5))
        .andExpect(jsonPath("$.content[0].name").value("IT Exercise A01"));
  }

  @Test
  void shouldSortByNameDescending() throws Exception {
    MvcResult ascResult =
        mockMvc
            .perform(
                get("/exercises")
                    .param("name", IT_NAME_PREFIX)
                    .param("page", "0")
                    .param("size", "1")
                    .param("sort", "name,asc"))
            .andExpect(status().isOk())
            .andReturn();

    MvcResult descResult =
        mockMvc
            .perform(
                get("/exercises")
                    .param("name", IT_NAME_PREFIX)
                    .param("page", "0")
                    .param("size", "1")
                    .param("sort", "name,desc"))
            .andExpect(status().isOk())
            .andReturn();

    assertThat(ascResult.getResponse().getContentAsString())
        .isNotEqualTo(descResult.getResponse().getContentAsString());
  }

  @Test
  void shouldReturnSecondPage() throws Exception {
    MvcResult firstPage =
        mockMvc
            .perform(
                get("/exercises")
                    .param("name", IT_NAME_PREFIX)
                    .param("page", "0")
                    .param("size", "5")
                    .param("sort", "name,asc"))
            .andExpect(status().isOk())
            .andReturn();

    MvcResult secondPage =
        mockMvc
            .perform(
                get("/exercises")
                    .param("name", IT_NAME_PREFIX)
                    .param("page", "1")
                    .param("size", "5")
                    .param("sort", "name,asc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.number").value(1))
            .andExpect(jsonPath("$.first").value(false))
            .andReturn();

    assertThat(firstPage.getResponse().getContentAsString())
        .isNotEqualTo(secondPage.getResponse().getContentAsString());
  }

  @Test
  void shouldFilterByNamePartialMatch() throws Exception {
    // "TestExercise" is a unique exercise in V900 test data
    mockMvc
        .perform(
            get("/exercises").param("name", "TestExercise").param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].name").value("TestExercise"));
  }

  @Test
  void shouldFilterByMovementPattern() throws Exception {
    // A01, A02, A03, A15 are PUSH within the IT dataset
    MvcResult pushResult =
        mockMvc
            .perform(
                get("/exercises")
                    .param("name", IT_NAME_PREFIX)
                    .param("movementPatterns", "PUSH")
                    .param("page", "0")
                    .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(4))
            .andReturn();

    assertThat(pushResult.getResponse().getContentAsString())
        .doesNotContain("\"movementPattern\":\"SQUAT\"");
  }

  @Test
  void shouldFilterByTargetMuscleGroup() throws Exception {
    // A01, A02 target CHEST within the IT dataset
    mockMvc
        .perform(
            get("/exercises")
                .param("name", IT_NAME_PREFIX)
                .param("targetMuscleGroups", "CHEST")
                .param("page", "0")
                .param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.content[0].targetMuscleGroups").isArray());
  }

  @Test
  void shouldFilterByCategoryAndDifficulty() throws Exception {
    // STRENGTH + BEGINNER/INTERMEDIATE within IT dataset: A01,A02,A04,A05,A06,A07,A14 = 7
    mockMvc
        .perform(
            get("/exercises")
                .param("name", IT_NAME_PREFIX)
                .param("categories", "STRENGTH")
                .param("difficulties", "BEGINNER")
                .param("difficulties", "INTERMEDIATE")
                .param("page", "0")
                .param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(7))
        .andExpect(jsonPath("$.content").isArray());
  }

  @Test
  void shouldReturn200WithEmptyContentWhenFilterMatchesNothing() throws Exception {
    mockMvc
        .perform(get("/exercises").param("name", "xyzzy_no_match_ever"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(0))
        .andExpect(jsonPath("$.totalElements").value(0))
        .andExpect(jsonPath("$.empty").value(true));
  }

  @Test
  void shouldReturn200WithDefaultSortWhenSortFieldIsInvalid() throws Exception {
    mockMvc
        .perform(
            get("/exercises")
                .param("name", IT_NAME_PREFIX)
                .param("sort", "difficulty,asc")
                .param("page", "0")
                .param("size", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].name").value("IT Exercise A01"));
  }

  @Test
  void shouldReturnExerciseById() throws Exception {
    mockMvc
        .perform(get("/exercises/b0000000-0000-0000-0000-000000000001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("IT Exercise A01"))
        .andExpect(jsonPath("$.movementPattern").value("PUSH"))
        .andExpect(jsonPath("$.category").value("STRENGTH"))
        .andExpect(jsonPath("$.difficulty").value("BEGINNER"))
        .andExpect(jsonPath("$.targetMuscleGroups[0]").value("CHEST"));
  }

  @Test
  void shouldReturn404WhenExerciseIdDoesNotExist() throws Exception {
    mockMvc
        .perform(get("/exercises/00000000-0000-0000-0000-000000000000"))
        .andExpect(status().isNotFound());
  }
}
