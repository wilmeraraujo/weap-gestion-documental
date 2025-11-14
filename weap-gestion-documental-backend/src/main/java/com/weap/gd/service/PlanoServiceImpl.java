package com.weap.gd.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.weap.gd.entity.Plano;
import com.weap.gd.repository.PlanoRepository;

public class PlanoServiceImpl implements PlanoService{
	
	@Autowired
	private PlanoRepository planoRepository;

	@Override
	public Iterable<? extends Plano> saveAll(Iterable<? extends Plano> plano) {
		return planoRepository.saveAll(plano);
	}

}
