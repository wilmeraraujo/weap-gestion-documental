package com.weap.gd.config;

import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.weap.gd.entity.CarguePlano;
import com.weap.gd.entity.ErrorCarguePlano;
import com.weap.gd.service.CarguePlanoService;
import com.weap.gd.service.EjecucionTareaService;
import com.weap.gd.service.ErrorCarguePlanoService;

@Component
public class JobLoteCargueListener implements JobExecutionListener{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JobLoteCargueListener.class);
	
	@Autowired
    private ErrorCarguePlanoService errorCargueService;

    @Autowired
    private CarguePlanoService cargueService;
    
    @Autowired
    private EjecucionTareaService ejecucionTareaService;  
   
    
    @Override
    public void beforeJob(JobExecution jobExecution) {
        
    }
    
    @Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			LOGGER.info("JobLotePaquete Termino!!! ID: {}", jobExecution.getId());
			JobParameters parameters = jobExecution.getJobParameters();
			
			try {
				Files.deleteIfExists(Paths.get(parameters.getString("fullPathFileName")));
			} catch (IOException e) {
				LOGGER.error("Error deleting file: ", e);
			}
			
			Long identificadorCargue = parameters.getLong("identificadorCargue");
			
			ExecutionContext ctx = jobExecution.getExecutionContext();
			
            int countRegistros = ctx.containsKey("countRegistros") ? ctx.getInt("countRegistros") : 0;
			

			List<ErrorCarguePlano> errorCargues = errorCargueService.obtenerErrorCargue(identificadorCargue);

			Optional<CarguePlano> cargueActualizar = cargueService.findById(identificadorCargue);
			if (cargueActualizar.isPresent()) {
				CarguePlano cargue = cargueActualizar.get();
				cargue.setEjecucionTarea(ejecucionTareaService.finById(jobExecution.getId()).get());

				if (!errorCargues.isEmpty()) {
					cargue.setErroresEnCargue(true);
				} else {
					cargue.setErroresEnCargue(false);
				}
				
				cargue.setNumeroRegistro(countRegistros);

				cargueService.save(cargue);
			}
		} else {
			LOGGER.info("El trabajo no se completó correctamente.");
		}
	}

}
