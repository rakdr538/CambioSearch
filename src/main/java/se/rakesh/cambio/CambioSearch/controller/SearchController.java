package se.rakesh.cambio.CambioSearch.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.rakesh.cambio.CambioSearch.model.EnglishDisplaySearchModel;
import se.rakesh.cambio.CambioSearch.model.SearchModel;
import se.rakesh.cambio.CambioSearch.model.SwedishDisplaySearchModel;
import se.rakesh.cambio.CambioSearch.service.CambioSearchService;

import java.util.*;

@Slf4j
@RestController
public class SearchController {

  private static final String SWEDISH = "sv";
  private static final String ENGLISH = "en";
  private static final String ALL_MODELS = "*";
  private static final String SPACE = " ";

  @Autowired private CambioSearchService cambioSearchService;

  @GetMapping(value = "/cds-search/api/models", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<?>> searchProfiles(
      @RequestParam String search, @RequestParam(required = false) String languageCode) {
    log.info("Search triggered with {} and {}", search, languageCode);

    if (search.startsWith(ALL_MODELS)) {
      List<SearchModel> allModels = cambioSearchService.getAllModels();
      log.info("Search returning {}", allModels);
      return ResponseEntity.ok(allModels);
    } else if (languageCode.equals(SWEDISH)) {
      List<SwedishDisplaySearchModel> allModels = cambioSearchService.getSwedishKeywordModels(search);
      log.info("Search returning {}", allModels);
      return ResponseEntity.ok(allModels);
    } else if (languageCode.equals(ENGLISH)) {
      List<EnglishDisplaySearchModel> allModels = cambioSearchService.getEnglishKeywordModels(search);
      log.info("Search returning {}", allModels);
      return ResponseEntity.ok(allModels);
    }
    return (ResponseEntity<List<?>>) Collections.emptyList();
  }

  @GetMapping(value = "/cds-search-service/api/models", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Object>> searchProfilesByKeywords(@RequestParam String search) {
    log.info("Search with multi keywords triggered {}", search);
    Set<String> searchSet = new HashSet<>(Arrays.asList(search.split(SPACE)));
    List<Object> allModels = cambioSearchService.getSearchModelsByMultipleKeywords(searchSet);
    log.info("Returning mutli keyword search result {}", allModels);
    return ResponseEntity.ok(allModels);
  }
}
