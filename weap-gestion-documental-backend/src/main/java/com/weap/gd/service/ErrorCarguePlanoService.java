package com.weap.gd.service;

import java.io.IOException;
import java.util.List;

import com.weap.gd.entity.ErrorCarguePlano;

public interface ErrorCarguePlanoService {
	
	public Iterable<ErrorCarguePlano> saveAll(Iterable<ErrorCarguePlano> errores);

	public boolean validarCargueError(Long identificadorCargue);

	public List<ErrorCarguePlano> obtenerErrorCargue(Long identificadorCargue);

	public byte[] exportErrorCargueToExcel(Long identificadorCargue) throws IOException;	

}
