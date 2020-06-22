package se.rakesh.cambio.CambioSearch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.rakesh.cambio.CambioSearch.converter.StringToStringListConverter;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "SearchModel")
public class SearchModel {
  @Id
  private String id;

  @Column(name = "englishKeywords")
  @Convert(converter = StringToStringListConverter.class)
  private List<String> englishKeywords;

  @Column(name = "swedishKeywords")
  @Convert(converter = StringToStringListConverter.class)
  private List<String> swedishKeywords;
}
