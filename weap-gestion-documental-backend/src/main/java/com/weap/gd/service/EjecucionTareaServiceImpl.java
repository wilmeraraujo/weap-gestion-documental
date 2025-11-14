package com.weap.gd.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weap.gd.entity.EjecucionTarea;
import com.weap.gd.repository.EjecucionTareaRepository;

@Service
public class EjecucionTareaServiceImpl implements EjecucionTareaService {
	
	@Autowired
	private EjecucionTareaRepository ejecucionTareaRepository;

	@Override
	public Optional<EjecucionTarea> finById(Long id) {
		return ejecucionTareaRepository.findById(id);
	}

}
