package se.rakesh.cambio.CambioSearch.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Converter
public class StringToStringListConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        String keywords = null;
        if(attribute != null){
            keywords = String.join(",", attribute);
        }
        return keywords;
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        List<String> keywords = new ArrayList<>();
        if(dbData != null) {
            keywords = new ArrayList<>(Arrays.asList(dbData.split(",")));
        }
        return keywords;
    }
}
