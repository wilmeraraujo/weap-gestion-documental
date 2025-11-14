package com.weap.gd.step;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.weap.gd.entity.Plano;

public class PlanoProcessor implements ItemProcessor<Plano, Plano>{		

	 private static final Logger LOGGER = LoggerFactory.getLogger(PlanoProcessor.class);

	    private final Long identificadorCargue;

	    public PlanoProcessor(Long identificadorCargue
	    		) {
	        this.identificadorCargue = identificadorCargue;
	    }

	    @Override
	    public Plano process(Plano item) {
	    	
	    	LOGGER.info("Procesando cargue: " + identificadorCargue);
	    	
	        item.setIdentificadorCargue(identificadorCargue);
	        return item;
	    }	
	
}
