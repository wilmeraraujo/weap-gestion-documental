package com.weap.gd.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.weap.gd.Constantes;
import com.weap.gd.entity.Plano;

public class PlanoFieldSetMapper implements FieldSetMapper<Plano>{
	
	private static final DateTimeFormatter STRICT_DDMMYYYY =
		    new DateTimeFormatterBuilder()
		        .parseStrict()
		        .appendPattern("dd/MM/uuuu")
		        .toFormatter()
		        .withResolverStyle(ResolverStyle.STRICT);
	
    private int currentLine = 0; 
    
    @Override
    public Plano mapFieldSet(FieldSet fieldSet) throws BindException {
    	
    	Plano planoCita = new Plano();
    	planoCita.setTipoIdentificacion(fieldSet.readString("tipoIdentificacion"));
    	planoCita.setNumeroIdentificacion(fieldSet.readString("numeroIdentificacion"));
    	planoCita.setPrimerNombre(fieldSet.readString("primerNombre"));
    	planoCita.setSegundoNombre(fieldSet.readString("segundoNombre"));
    	planoCita.setPrimerApellido(fieldSet.readString("primerApellido"));
    	planoCita.setSegundoApellido(fieldSet.readString("segundoApellido"));
    	planoCita.setLinea(++currentLine);
        return planoCita;
    }
    
    private LocalDate parseDate(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isEmpty()) return null;

        try {
            return LocalDate.parse(s, STRICT_DDMMYYYY);
        } catch (DateTimeParseException e) {           
            return Constantes.INVALID_DATE;
            
        }
    }
    /*
    private Long isLong(String numberString) {
        if (numberString == null || numberString.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(numberString);
        } catch (NumberFormatException e) {            
            return null; 
        }
    }*/
    
    private Integer isInteger(String numberString) {
        if (numberString == null || numberString.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(numberString);
        } catch (NumberFormatException e) {            
            return null; 
        }
    }

}
