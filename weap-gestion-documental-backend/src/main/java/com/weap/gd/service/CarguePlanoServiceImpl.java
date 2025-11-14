package com.weap.gd.service;

import java.io.File;
import java.util.Optional;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;

import com.weap.gd.entity.CarguePlano;
import com.weap.gd.repository.CarguePlanoRepository;

@Service
public class CarguePlanoServiceImpl implements CarguePlanoService {
	
	@Autowired
	private CarguePlanoRepository cargueRepository;
	
	@Autowired
	private JobLauncher jobLoteCargueLauncher;
	
	@Lazy	
	@Qualifier("procesarLoteCargueJob")
	@Autowired
    private Job procesarLoteCargueJob;
	
	@Override
	@Transactional(readOnly = true)
	public Page<CarguePlano> findAll(Pageable pageable) {
		return cargueRepository.findAll(pageable);
	}

	@Override
	@Transactional
	public CarguePlano save(CarguePlano cargue) {
		return cargueRepository.save(cargue);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<CarguePlano> findById(Long id) {
		return cargueRepository.findById(id);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Page<CarguePlano> findByAllIgnoreDeleteAt(Pageable pageable) {
		return cargueRepository.findByAllIgnoreDeleteAt(pageable);
	}
	
	@Override
    @Async("taskExecutor")
    public void runBatchJobAsynchronously(File fileToImport, CarguePlano cargue) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("fullPathFileName", fileToImport.getAbsolutePath())
                    .addLong("identificadorCargue", cargue.getId())
                    .addString("nombreArchivo", cargue.getNombreArchivo())                    
                    .addString("usuario", cargue.getUsuario())
                    .addString("nitPrestador", cargue.getNitPrestador()) 
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();

            jobLoteCargueLauncher.run(procesarLoteCargueJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            // Manejar la excepción adecuadamente
            e.printStackTrace();
        }
    }
	
	@Override
	public Page<CarguePlano> buscarPorUsuarioYFiltro(String usuario, String term, Pageable pageable) {
	    return cargueRepository.findByUsuarioAndFiltroSinSolape(usuario, term, pageable);
	}

}
