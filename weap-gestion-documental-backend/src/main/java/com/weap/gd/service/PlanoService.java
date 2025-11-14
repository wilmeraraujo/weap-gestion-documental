package com.weap.gd.service;

import com.weap.gd.entity.Plano;

public interface PlanoService {

	public Iterable<? extends Plano> saveAll(Iterable<? extends Plano> planoCita);
	
}
