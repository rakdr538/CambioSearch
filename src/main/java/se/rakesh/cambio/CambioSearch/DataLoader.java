package se.rakesh.cambio.CambioSearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import se.rakesh.cambio.CambioSearch.model.SearchModel;
import se.rakesh.cambio.CambioSearch.repository.SearchModelRepository;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;

/* Component to load initial data */
@Slf4j
@Component
public class DataLoader implements ApplicationRunner {
  private static final String DOT = ".";
  private static final String DASH = "-";

  private SearchModelRepository repository;

  @Autowired private ApplicationContext applicationContext;

  private static File[] getResourceFolderFiles (String folder) {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    URL url = loader.getResource(folder);
    String path = url.getPath();
    return new File(path).listFiles();
  }

  @Autowired
  public DataLoader(SearchModelRepository repository) {
    this.repository = repository;
  }

  public void run(ApplicationArguments args) throws IOException {
    for (File file : getResourceFolderFiles("searchModels")) {
      log.info("Parsing file {}",file.getName());
      parseAndLoadJsonData(file);
    }
  }

  private void parseAndLoadJsonData(File f) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      String content = new String(Files.readAllBytes(f.toPath()));
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
}
