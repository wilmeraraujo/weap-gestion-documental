package com.weap.gd.step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import com.weap.gd.Constantes;
import com.weap.gd.entity.ErrorCarguePlano;
import com.weap.gd.entity.Plano;
import com.weap.gd.mapper.PlanoFieldSetMapper;
import com.weap.gd.service.CarguePlanoService;
import com.weap.gd.service.ErrorCarguePlanoService;

@StepScope
@Component
public class PlanoReader {

	private final String fileInput;
	private final String fileDelimiter;
	private final ErrorCarguePlanoService errorCargueService;
	private final CarguePlanoService cargueService;
	private final Long identificadorCargue;	

	public PlanoReader(String fileInput, String fileDelimiter, ErrorCarguePlanoService errorCargueService, CarguePlanoService cargueService, Long identificadorCargue) {
		this.fileInput = fileInput;
		this.fileDelimiter = fileDelimiter;
		this.errorCargueService = errorCargueService;
		this.cargueService = cargueService;
		this.identificadorCargue = identificadorCargue;

	}
	
	public FlatFileItemReader<Plano> read() {
		
		System.out.println("=== 📂 Leyendo archivo: " + fileInput);
	    System.out.println("=== 🧩 Delimitador configurado: [" + fileDelimiter + "]");
	    
	    DefaultLineMapper<Plano> lineMapper = new DefaultLineMapper<>() {
	        @Override
	        public Plano mapLine(String line, int lineNumber) throws Exception {
	        	System.out.println("\n📄 Línea #" + lineNumber + ": " + line);
	            // valida estructura antes de tokenizar/mapear
	            String[] columns = line.split(Pattern.quote(fileDelimiter), -1);
	            int cols = columns.length;
	            System.out.println("➡️  Columnas detectadas: " + cols + " → " + Arrays.toString(columns));
	            boolean estructuraOk = (cols == Constantes.NUM_COLUMN_MIN);

	            if (!estructuraOk) {
	            	System.out.println("⚠️  ERROR: Estructura inválida. Se esperaban " +
	                        Constantes.NUM_COLUMN_MIN + " columnas, pero se recibieron " + cols);
	                // persiste el error de estructura
	                List<ErrorCarguePlano> errores = new ArrayList<>();
	                errores.add(ErrorCarguePlano.builder()
	                    .tipoError("Estructura")
	                    .campo("txt")
	                    .error("Número de columnas o delimitador incorrecto. Se esperaban "
	                        + Constantes.NUM_COLUMN_MIN //+ " o " + Constantes.NUM_COLUMN_MAX
	                        + " columnas; recibidas " + cols + ". Delimitador: " + fileDelimiter)
	                    .valorAsociado(line)
	                    .numeroLinea(lineNumber)
	                    .cargue(cargueService.findById(identificadorCargue).orElse(null))
	                    .build());
	                errorCargueService.saveAll(errores);

	                // hace que Spring Batch *salte* esta línea en lectura
	                throw new org.springframework.batch.item.file.FlatFileParseException(
	                    "Estructura inválida", line, lineNumber);
	            }

	            // si la estructura es válida, delega al comportamiento normal
	            //return super.mapLine(line, lineNumber);
	            Plano item = super.mapLine(line, lineNumber);
	            System.out.println("✅ Línea válida mapeada a objeto PlanoCita: " + item);
	            return item;
	        }
	    };
	    
	    DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
	    tokenizer.setDelimiter(fileDelimiter);
	    tokenizer.setNames(
	    	"codigoEps","tipoIdentificacion","numeroIdentificacion",
	    	"primerNombre","segundoNombre","primerApellido","segundoApellido",
	        "fechaSolicitud","fechaDeseada","fechaCita","codigoServicio","horasEspecialista"
	    );
	    //tokenizer.setStrict(false);

	    lineMapper.setLineTokenizer(tokenizer);
	    lineMapper.setFieldSetMapper(new PlanoFieldSetMapper());

	    return new FlatFileItemReaderBuilder<Plano>()
	        .name("Plano")
	        .resource(new FileSystemResource(fileInput))
	        .lineMapper(lineMapper)	        
	        .build();
	}    

}
