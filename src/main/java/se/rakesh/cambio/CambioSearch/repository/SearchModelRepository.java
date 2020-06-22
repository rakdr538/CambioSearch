package se.rakesh.cambio.CambioSearch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;
import se.rakesh.cambio.CambioSearch.model.SearchModel;

import java.util.List;

@Repository
public interface SearchModelRepository
    extends JpaRepository<SearchModel, String>, QueryByExampleExecutor<SearchModel> {

  @Query(
      "SELECT id, swedishKeywords FROM SearchModel u WHERE u.swedishKeywords LIKE CONCAT('%',:searchString,'%')")
  List<Object[]> findBySwedishKeywords(String searchString);

  @Query(
      "SELECT id, englishKeywords FROM SearchModel u WHERE u.englishKeywords LIKE CONCAT('%',:searchString,'%')")
  List<Object[]> findByEnglishKeywords(String searchString);
}
