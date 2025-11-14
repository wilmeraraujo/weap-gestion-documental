package com.weap.gd.step;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import com.weap.gd.entity.CarguePlano;
import com.weap.gd.entity.ErrorCarguePlano;
import com.weap.gd.entity.Plano;
import com.weap.gd.service.CarguePlanoService;
import com.weap.gd.service.ErrorCarguePlanoService;
import com.weap.gd.service.PlanoService;
import com.weap.gd.service.ValidacionPlanoService;

public class PlanoWriter implements ItemWriter<Plano>, StepExecutionListener{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PlanoWriter.class);

    private final PlanoService planoService;
    private final CarguePlanoService carguePlanoService;
    private final ErrorCarguePlanoService errorCargueService;
    private final ValidacionPlanoService validacionService;
    //private final boolean writeEnabled;
    private final Long identificadorCargue;
    private final String nitPrestador;
    private final String usuario;
    
    private final List<Plano> buffer = new ArrayList<>();

    private boolean hasErrors = false;

    // Caché del cargue para asociarlo a los errores
    private CarguePlano cargueCache;

    public PlanoWriter(
							//boolean writeEnabled,
                            Long identificadorCargue,
                            String nitPrestador,
                            String usuario,
                            PlanoService planoService,
                            CarguePlanoService carguePlanoService,
                            ErrorCarguePlanoService errorCargueService,
                            ValidacionPlanoService validacionService) {
        //this.writeEnabled = writeEnabled;
        this.identificadorCargue = identificadorCargue;
        this.nitPrestador = nitPrestador;
        this.usuario = usuario;
        this.planoService = planoService;
        this.carguePlanoService = carguePlanoService;
        this.errorCargueService = errorCargueService;
        this.validacionService = validacionService;
    }

    private CarguePlano resolveCargue() {
        if (cargueCache == null) {
            cargueCache = carguePlanoService
                .findById(identificadorCargue)
                .orElseThrow(() -> new IllegalStateException(
                    "No existe CarguePlanoCita id=" + identificadorCargue));
        }
        return cargueCache;
    }
    
    @Override
    public void write(Chunk<? extends Plano> chunk) {
        if (!chunk.isEmpty()) {
            buffer.addAll(chunk.getItems());
        }
    }
   
    @Override
    public void beforeStep(StepExecution stepExecution) {
    	
    }    
    
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        LOGGER.info("Persistiendo {} registros en buffer...", buffer.size());
        ExitStatus status = stepExecution.getExitStatus();

        if (buffer.isEmpty()) {
            LOGGER.info("No hay registros para validar/persistir.");
            return status;
        }

        int conteoRegistros = countRegistros(buffer);
        stepExecution.getJobExecution().getExecutionContext().putInt("countRegistros", conteoRegistros);

        List<ErrorCarguePlano> errores = validacionService.validarPlano(buffer, nitPrestador, usuario);

        if (!errores.isEmpty()) {
            hasErrors = true;
            CarguePlano cargue = resolveCargue();
            errores.forEach(e -> e.setCargue(cargue));
            errorCargueService.saveAll(errores);
            LOGGER.info("Se detectaron {} errores. No se persistirá ningún registro.", errores.size());
            buffer.clear();
            return status;
        }

        if (errorCargueService.validarCargueError(identificadorCargue) || stepExecution.getReadSkipCount() > 0) {
            buffer.clear();
            LOGGER.info("El cargue contiene errores previos. No se guardan registros.");
            return status;
        }

        LOGGER.info("Validaciones OK. Persistiendo {} registros...", buffer.size());
        planoService.saveAll(buffer);
        buffer.clear();
        return status;
    }

    private int countRegistros(List<Plano> items) {
        return (int) items.stream()
            .map(Plano::getNumeroIdentificacion)
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .count();
    }
    
}
