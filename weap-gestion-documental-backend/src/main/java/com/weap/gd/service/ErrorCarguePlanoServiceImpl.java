package com.weap.gd.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weap.gd.entity.ErrorCarguePlano;
import com.weap.gd.repository.ErrorCarguePlanoRepository;

import jakarta.transaction.Transactional;


@Service
public class ErrorCarguePlanoServiceImpl implements ErrorCarguePlanoService{
	
	@Autowired
	private ErrorCarguePlanoRepository errorCarguePlanoCitaRepository;

	@Autowired
	private ExcelExportService<ErrorCarguePlano> excelExportService;

	@Override
	@Transactional
	public Iterable<ErrorCarguePlano> saveAll(Iterable<ErrorCarguePlano> errores) {
		return errorCarguePlanoCitaRepository.saveAll(errores);
	}

	@Override
	public boolean validarCargueError(Long identificadorCargue) {
		return errorCarguePlanoCitaRepository.existsByCargue_Id(identificadorCargue);
	}

	@Override
	public List<ErrorCarguePlano> obtenerErrorCargue(Long identificadorCargue) {
		return errorCarguePlanoCitaRepository.findByCargue_Id(identificadorCargue);
	}

	@Override
	public byte[] exportErrorCargueToExcel(Long identificadorCargue) throws IOException {
		List<ErrorCarguePlano> errores = errorCarguePlanoCitaRepository.findByCargue_Id(identificadorCargue);
		return excelExportService.exportToExcel(errores);
	}

}
