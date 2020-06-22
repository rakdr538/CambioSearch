package se.rakesh.cambio.CambioSearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import se.rakesh.cambio.CambioSearch.model.SearchModel;
import se.rakesh.cambio.CambioSearch.repository.SearchModelRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/* Component to load initial data */
@Slf4j
@Component
public class DataLoader implements ApplicationRunner {
  private static final String DOT = ".";
  private static final String DASH = "-";

  private SearchModelRepository repository;

  @Autowired private ApplicationContext applicationContext;

  @Autowired
  public DataLoader(SearchModelRepository repository) {
    this.repository = repository;
  }

  public void run(ApplicationArguments args) throws IOException {
    ClassLoader cl = this.getClass().getClassLoader();
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
    Stream<InputStream> stream = getInputStreamsFromClasspath("searchModels", resolver);
    stream.forEach(inputStream -> {
      try {
        parseAndLoadJsonData(inputStream.readAllBytes());
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  private void parseAndLoadJsonData(byte[] f) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      String content = new String(f);
      JsonNode actualObj = mapper.readTree(content);
      JsonNode id = actualObj.get("id");
      JsonNode details = actualObj.get("description").get("details");
      JsonNode enKeywords = details.get("en").get("keywords");
      JsonNode svKeywords = null;
      if (details.get("sv") != null) {
        svKeywords = details.get("sv").get("keywords");
      }
      log.info("sv keywords are {}", svKeywords);
      repository.save(
          SearchModel.builder()
              .id(id.toString().replace(DOT, DASH))
              .englishKeywords(new ObjectMapper().convertValue(enKeywords, ArrayList.class))
              .swedishKeywords(new ObjectMapper().convertValue(svKeywords, ArrayList.class))
              .build());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static Stream<InputStream> getInputStreamsFromClasspath(
          String path,
          PathMatchingResourcePatternResolver resolver) {
    try {
      return Arrays.stream(resolver.getResources("/" + path + "/*.json"))
              .filter(Resource::exists)
              .map(resource -> {
                try {
                  return resource.getInputStream();
                } catch (IOException e) {
                  return null;
                }
              })
              .filter(Objects::nonNull);
    } catch (IOException e) {
      log.error("Failed to get definitions from directory {}", path, e);
      return Stream.of();
    }
  }
}
