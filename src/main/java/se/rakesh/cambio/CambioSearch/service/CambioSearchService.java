package se.rakesh.cambio.CambioSearch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.rakesh.cambio.CambioSearch.model.EnglishDisplaySearchModel;
import se.rakesh.cambio.CambioSearch.model.SearchModel;
import se.rakesh.cambio.CambioSearch.model.SwedishDisplaySearchModel;
import se.rakesh.cambio.CambioSearch.repository.SearchModelRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CambioSearchService {
  @Autowired private SearchModelRepository repository;

  public List<SwedishDisplaySearchModel> getSwedishKeywordModels(String search) {
    List<SwedishDisplaySearchModel> svSearchModels = new ArrayList<>();
    List<Object[]> rows = repository.findBySwedishKeywords(search);
    log.info("no of rows found {}", rows.size());
    rows.stream()
        .map(Arrays::asList)
        .forEach(
            item -> {
              SwedishDisplaySearchModel model =
                      SwedishDisplaySearchModel.builder()
                      .id((String) item.get(0))
                      .swedishKeywords(new ArrayList<>((Collection<? extends String>) item.get(1)))
                      .build();
              svSearchModels.add(model);
            });
    return svSearchModels;
  }

  public List<SearchModel> getAllModels() {
    return repository.findAll();
  }

  public List<EnglishDisplaySearchModel> getEnglishKeywordModels(String search) {
    List<EnglishDisplaySearchModel> enSearchModels = new ArrayList<>();
    List<Object[]> rows = repository.findByEnglishKeywords(search);
    log.info("no of rows found {}", rows.size());
    rows.stream()
        .map(Arrays::asList)
        .forEach(
            item -> {
              EnglishDisplaySearchModel model =
                      EnglishDisplaySearchModel.builder()
                      .id((String) item.get(0))
                      .englishKeywords(new ArrayList<>((Collection<? extends String>) item.get(1)))
                      .build();
              enSearchModels.add(model);
            });
    return enSearchModels;
  }

  public List<Object> getSearchModelsByMultipleKeywords(Set<String> searchSet) {
    Set<SearchModel> globalQueryResult = new HashSet<>();
    searchSet.stream()
        .map(search -> search.replaceAll("[^A-Za-z0-9]", ""))
        .forEach(
            search -> {
              log.info("Searching for {}", search);
              globalQueryResult.addAll(swedishKeywordModels(search));
              globalQueryResult.addAll(englishKeywordModels(search));
            });
    log.info("globalQueryResult {}", globalQueryResult);
    var allIds = getOnlyIdsFromGlobalQueryResult(globalQueryResult);

    var duplicates = getDuplicatesFromAllIds(allIds);

    duplicates.forEach(
        duplicate -> globalQueryResult.removeIf(res -> res.getId().equals(duplicate)));
    log.info("removed duplicated from globalQueryResult {}", globalQueryResult);

    duplicates.forEach(duplicate -> globalQueryResult.add(getSearchModel(duplicate)));
    log.info("globalQueryResult final {}", globalQueryResult);

    return collectObjectsForDisplay(globalQueryResult);
  }

  private List<Object> collectObjectsForDisplay(Set<SearchModel> globalQueryResult) {
    return globalQueryResult.stream().map(model -> {
      if (model.getSwedishKeywords() == null) {
        return EnglishDisplaySearchModel.builder()
                .id(model.getId())
                .englishKeywords(model.getEnglishKeywords())
                .build();
      } else if (model.getEnglishKeywords() == null){
        return SwedishDisplaySearchModel.builder()
                .id(model.getId())
                .swedishKeywords(model.getSwedishKeywords())
                .build();
      } else {
        return model;
      }
    }).collect(Collectors.toList());
  }

  private SearchModel getSearchModel(String duplicate) {
    var model = repository.findById(duplicate);
    return model.orElse(null);
  }

  private List<String> getDuplicatesFromAllIds(List<String> allIds) {
    var duplicates =
        allIds.stream()
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet()
            .stream()
            .filter(e -> e.getValue() > 1L)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    log.info("Duplicates {}", duplicates);
    return duplicates;
  }

  private List<String> getOnlyIdsFromGlobalQueryResult(Set<SearchModel> globalQueryResult) {
    log.info("getOnlyIdsFromGlobalQueryResult {}", globalQueryResult);
    var allIds = globalQueryResult.stream().map(SearchModel::getId).collect(Collectors.toList());
    log.info("All Ids {}", allIds);
    return allIds;
  }

  private List<SearchModel> swedishKeywordModels(String search) {
    List<SearchModel> svSearchModels = new ArrayList<>();
    List<Object[]> rows = repository.findBySwedishKeywords(search);
    log.info("no of rows found {}", rows.size());
    rows.stream()
            .map(Arrays::asList)
            .forEach(
                    item -> {
                      SearchModel model =
                              SearchModel.builder()
                                      .id((String) item.get(0))
                                      .swedishKeywords(new ArrayList<>((Collection<? extends String>) item.get(1)))
                                      .build();
                      svSearchModels.add(model);
                    });
    return svSearchModels;
  }

  private List<SearchModel> englishKeywordModels(String search) {
    List<SearchModel> enSearchModels = new ArrayList<>();
    List<Object[]> rows = repository.findByEnglishKeywords(search);
    log.info("no of rows found {}", rows.size());
    rows.stream()
            .map(Arrays::asList)
            .forEach(
                    item -> {
                      SearchModel model =
                              SearchModel.builder()
                                      .id((String) item.get(0))
                                      .englishKeywords(new ArrayList<>((Collection<? extends String>) item.get(1)))
                                      .build();
                      enSearchModels.add(model);
                    });
    return enSearchModels;
  }
}
