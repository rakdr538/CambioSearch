package se.rakesh.cambio.CambioSearch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import se.rakesh.cambio.CambioSearch.model.SearchModel;
import se.rakesh.cambio.CambioSearch.repository.SearchModelRepository;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CambioSearchApplicationTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private SearchModelRepository repository;

  @Test
  void search_with_starWildCardOnly() throws Exception {

    List<SearchModel> result = executeTestWithQueryParam("*");

    Assertions.assertNotNull(result);
    Assertions.assertEquals(4, result.size());
    Assertions.assertEquals("\"Chronic_pain_pregabalin-v1\"", result.get(0).getId());
    Assertions.assertNotNull(result.get(3).getSwedishKeywords());
  }

  @Test
  void search_with_starWildCardToBegin() throws Exception {

    List<SearchModel> result = executeTestWithQueryParam("*Preg");

    Assertions.assertNotNull(result);
    Assertions.assertEquals(4, result.size());
    Assertions.assertEquals("\"Chronic_pain_pregabalin-v1\"", result.get(0).getId());
    Assertions.assertNotNull(result.get(3).getSwedishKeywords());
  }

  private List<SearchModel> executeTestWithQueryParam(String param) throws Exception {
    MvcResult result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get("/cds-search/api/models")
                    .queryParam("search", param)
                    .characterEncoding("utf-8"))
            .andExpect(status().isOk())
            .andReturn();

    final ObjectMapper mapper = new ObjectMapper();

    return mapper.readValue(
        result.getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {});
  }

  @Test
  void search_with_starWildCardToBeginAndSvLanguageCode() throws Exception {

    List<SearchModel> result = executeTestWithQueryParams("*Preg", "sv");

    Assertions.assertNotNull(result);
    Assertions.assertEquals(4, result.size());
    Assertions.assertEquals("\"Chronic_pain_pregabalin-v1\"", result.get(0).getId());
    Assertions.assertNotNull(result.get(3).getSwedishKeywords());
  }

  @Test
  void search_with_SvLanguageCode() throws Exception {

    List<SearchModel> result = executeTestWithQueryParams("Kronisk", "sv");

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.size());
    Assertions.assertTrue("\"Chronic_pain_pregabalin-v1\"".equals(result.get(0).getId()));
    Assertions.assertNull(result.get(0).getEnglishKeywords());
  }

  @Test
  void search_with_EnLanguageCode() throws Exception {

    List<SearchModel> result = executeTestWithQueryParams("Medicines", "en");

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.size());
    Assertions.assertTrue(
        "\"Osteoporosis_secondary_prevention_match-v1\"".equals(result.get(0).getId()));
    Assertions.assertNull(result.get(0).getSwedishKeywords());
  }

  private List<SearchModel> executeTestWithQueryParams(String param1, String param2)
      throws Exception {
    MvcResult result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get("/cds-search/api/models")
                    .queryParam("search", param1)
                    .queryParam("languageCode", param2)
                    .characterEncoding("utf-8"))
            .andExpect(status().isOk())
            .andReturn();

    final ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(
        result.getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {});
  }

  @Test
  void search_with_MultipleKeywords() throws Exception {
    List<SearchModel> actualResult =
        executeTestWithMultipleKeywords("Alendronate Prega* Osteo*");

    Assertions.assertNotNull(actualResult);
    Assertions.assertEquals(2, actualResult.size());
  }

  private List<SearchModel> executeTestWithMultipleKeywords(String param) throws Exception {
    MvcResult result =
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get("/cds-search-service/api/models")
                    .queryParam("search", param)
                    .characterEncoding("utf-8"))
            .andExpect(status().isOk())
            .andReturn();

    final ObjectMapper mapper = new ObjectMapper();

    return mapper.readValue(
        result.getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {});
  }

  @Test
  void search_with_MultipleRepetitiveKeywords() throws Exception {
    List<SearchModel> actualResult =
        executeTestWithMultipleKeywords("Alendronate Prega* Osteo* Prega*");

    Assertions.assertNotNull(actualResult);
    Assertions.assertEquals(2, actualResult.size());
  }
}
