package com.weap.gd.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weap.gd.entity.Plano;

@Repository
public interface PlanoRepository extends JpaRepository<Plano, Long>{
	
	public Page<Plano> findByIdentificadorCargue(Pageable pageable, Long identificadorCargue);

}
