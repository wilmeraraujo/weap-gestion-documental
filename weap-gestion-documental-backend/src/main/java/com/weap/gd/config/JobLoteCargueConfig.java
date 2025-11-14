package com.weap.gd.config;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.weap.gd.entity.Plano;
import com.weap.gd.service.CarguePlanoService;
import com.weap.gd.service.ErrorCarguePlanoService;
import com.weap.gd.service.PlanoService;
import com.weap.gd.service.ValidacionPlanoService;
import com.weap.gd.step.PlanoProcessor;
import com.weap.gd.step.PlanoReader;
import com.weap.gd.step.PlanoWriter;

import org.springframework.batch.core.StepExecutionListener;

import jakarta.validation.Validator;

@Configuration
@EnableBatchProcessing
public class JobLoteCargueConfig {	
	
	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private ErrorCarguePlanoService errorCargueService;

	@Autowired
	private CarguePlanoService cargueService;

	@Autowired
	private Validator validator;
	
	public JobLoteCargueConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			ErrorCarguePlanoService errorCargueService, CarguePlanoService cargueService, Validator validator) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.errorCargueService = errorCargueService;
		this.cargueService = cargueService;
		this.validator = validator;
	}

	@Bean
	public Job procesarLoteCargueJob(JobLoteCargueListener listener, Step stepOne) {
		return new JobBuilder("procesarLoteCargueJob", jobRepository)
				.listener(listener)
				.start(stepOne)
				.build();
	}

	@Bean
	public Step stepOne(
	    JobRepository jobRepository,
	    PlatformTransactionManager transactionManager,
	    FlatFileItemReader<Plano> reader,
	    PlanoProcessor processor,
	    PlanoWriter writer
	) {
	  return new StepBuilder("stepOne", jobRepository)
	      .<Plano, Plano>chunk(50, transactionManager)
	      .reader(reader)
	      .processor(processor)
	      .writer(writer)
	      .listener(writer)
	      .listener(new StepExecutionListener() {
	          @Override public void beforeStep(StepExecution se) {}
	          @Override public ExitStatus afterStep(StepExecution se) {
	              System.out.println("[STEP METRICS] readCount=" + se.getReadCount()
	                  + " filterCount=" + se.getFilterCount()
	                  + " readSkip=" + se.getReadSkipCount()
	                  + " processSkip=" + se.getProcessSkipCount()
	                  + " writeCount=" + se.getWriteCount()
	                  + " writeSkip=" + se.getWriteSkipCount());
	              return se.getExitStatus();
	          }
	      })
	      .listener(new SkipListener<Plano, Plano>() {
	          @Override public void onSkipInRead(Throwable t) {
	              if (t instanceof FlatFileParseException f) {
	                  System.out.println("[SKIP-READ] line=" + f.getLineNumber()
	                      + " input=" + f.getInput() + " msg=" + t.getMessage());
	              } else {
	                  System.out.println("[SKIP-READ] " + t.getClass().getSimpleName()
	                      + " msg=" + t.getMessage());
	              }
	          }
	          @Override public void onSkipInProcess(Plano item, Throwable t) {}
	          @Override public void onSkipInWrite(Plano item, Throwable t) {}
	      })
	      .faultTolerant()
	      .skip(Exception.class)
	      .skipLimit(Integer.MAX_VALUE)
	      .build();
	}

	@Bean
	@StepScope
	public FlatFileItemReader<Plano> reader(
			@Value("#{jobParameters['fullPathFileName']}") String csvFilePath,
			@Value("${file.delimiter}") String fileDelimiter,
			@Value("#{jobParameters['identificadorCargue']}") Long identificadorCargue) {
		return new PlanoReader(csvFilePath, fileDelimiter, errorCargueService, cargueService, identificadorCargue).read();
	}
	
	@Bean
	@StepScope
	public PlanoProcessor processor(
	    @Value("#{jobParameters['identificadorCargue']}") Long identificadorCargue
	) {

	    return new PlanoProcessor(identificadorCargue);
	}		
	
	@Bean
	@StepScope
	public PlanoWriter writer(
	    @Value("#{jobParameters['identificadorCargue']}") Long identificadorCargue,
	    @Value("#{jobParameters['nitPrestador']}") String nitPrestador,
	    @Value("#{jobParameters['usuario']}") String usuario,
	    ErrorCarguePlanoService errorCargueService,
	    PlanoService planoService,
	    CarguePlanoService carguePlanoService,
	    ValidacionPlanoService validacionService
	) {
	    //boolean writeEnabled = !errorCargueService.validarCargueError(identificadorCargue);
	    //System.out.println("[WRITER INIT] writeEnabled=" + writeEnabled + " cargue=" + identificadorCargue);
	    return new PlanoWriter(
	        //writeEnabled,
	        identificadorCargue,
	        nitPrestador,
	        usuario,
	        planoService,
	        carguePlanoService,
	        errorCargueService,
	        validacionService
	    );
	}

}
