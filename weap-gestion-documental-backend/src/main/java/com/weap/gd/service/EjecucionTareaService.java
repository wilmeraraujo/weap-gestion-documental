package com.weap.gd.service;

import java.util.Optional;

import com.weap.gd.entity.EjecucionTarea;

public interface EjecucionTareaService {
	
	public Optional<EjecucionTarea> finById(Long id);
	
}
