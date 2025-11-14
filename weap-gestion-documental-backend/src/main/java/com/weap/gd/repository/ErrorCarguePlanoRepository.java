package com.weap.gd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weap.gd.entity.ErrorCarguePlano;

@Repository
public interface ErrorCarguePlanoRepository extends JpaRepository<ErrorCarguePlano, Long> {
	
	public boolean existsByCargue_Id(Long cargueId);
	
	public List< ErrorCarguePlano> findByCargue_Id(Long cargueId);

}