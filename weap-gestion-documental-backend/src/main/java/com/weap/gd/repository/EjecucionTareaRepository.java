package com.weap.gd.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weap.gd.entity.EjecucionTarea;


@Repository
public interface EjecucionTareaRepository extends JpaRepository<EjecucionTarea, Long>{

}