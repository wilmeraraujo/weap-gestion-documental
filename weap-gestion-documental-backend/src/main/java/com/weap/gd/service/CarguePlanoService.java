package com.weap.gd.service;

import java.io.File;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.weap.gd.entity.CarguePlano;

public interface CarguePlanoService {
	
	public CarguePlano save(CarguePlano cargue);
	public Page<CarguePlano> findAll(Pageable pageable);	
	public Optional<CarguePlano> findById(Long id);
	public Page<CarguePlano> findByAllIgnoreDeleteAt(Pageable pageable);
	public void runBatchJobAsynchronously(File fileToImport, CarguePlano cargue);
	public Page<CarguePlano> buscarPorUsuarioYFiltro(String usuario, String term, Pageable pageable);

}
