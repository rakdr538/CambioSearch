package se.rakesh.cambio.CambioSearch.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class EnglishDisplaySearchModel {
    private String id;
    private List<String> englishKeywords;
}
