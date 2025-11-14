package com.weap.gd.service;

import java.util.List;

import com.weap.gd.entity.ErrorCarguePlano;
import com.weap.gd.entity.Plano;

public interface ValidacionPlanoService {
	
	public List<ErrorCarguePlano> validarPlano(List<Plano> archivos,String idPrestador, String usuario);
	
}
