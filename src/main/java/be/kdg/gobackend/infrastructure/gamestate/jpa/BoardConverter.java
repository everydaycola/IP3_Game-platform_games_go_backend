package be.kdg.gobackend.infrastructure.gamestate.jpa;

import be.kdg.gobackend.domain.game.Stone;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;


@Converter
public class BoardConverter implements AttributeConverter<Stone[], String> {
    @Override
    public String convertToDatabaseColumn(Stone[] board) {
        return Arrays.stream(board)
                .map(Stone::getShortName)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    @Override
    public Stone[] convertToEntityAttribute(String dbData) {
        return dbData.chars()
                .mapToObj(c -> Stone.fromShortName((char) c))
                .toArray(Stone[]::new);
    }
}