package com.weap.gd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.weap.gd.entity.CarguePlano;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.repository.query.Param;

@Repository
public interface CarguePlanoRepository extends JpaRepository<CarguePlano, Long>{
	
	@Query("SELECT c FROM CarguePlano c WHERE c.deleteAt is null Order by c.id desc")
	public Page<CarguePlano> findByAllIgnoreDeleteAt(Pageable pageable);
	
	@Query(
			  value = """
			    SELECT c.*
			    FROM cargue.cargue_plano c
			    WHERE c.delete_at IS NULL
			      AND c.usuario = :usuario
			      AND (
			        LOWER(c.nombre_archivo) LIKE LOWER(CONCAT('%', :term, '%'))
			        OR LOWER(c.nit_prestador) LIKE LOWER(CONCAT('%', :term, '%'))
			        OR TO_CHAR(c.fecha_cargue, 'DD/MM/YYYY') LIKE CONCAT('%', :term, '%')
			      )
			    ORDER BY c.id DESC
			  """,
			  countQuery = """
			    SELECT COUNT(*)
			    FROM cargue.cargue_plano c
			    WHERE c.delete_at IS NULL
			      AND c.usuario = :usuario
			      AND (
			        LOWER(c.nombre_archivo) LIKE LOWER(CONCAT('%', :term, '%'))
			        OR LOWER(c.nit_prestador) LIKE LOWER(CONCAT('%', :term, '%'))
			        OR LOWER(c.codigo_prestador) LIKE LOWER(CONCAT('%', :term, '%'))
			        OR TO_CHAR(c.fecha_cargue, 'DD/MM/YYYY') LIKE CONCAT('%', :term, '%')
			      )
			  """,
			  nativeQuery = true
			)
			Page<CarguePlano> findByUsuarioAndFiltroSinSolape(
			        @Param("usuario") String usuario,
			        @Param("term") String term,
			        Pageable pageable);

}
