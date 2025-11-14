package com.weap.gd.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Entity
@Table(name = "error_cargue_plano", schema = "cargue")
public class ErrorCarguePlano {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Integer numeroLinea;
    private String tipoError;
    private String campo;
    private String error;
    private String valorAsociado;
    
    @ManyToOne
    @JoinColumn(name = "id_cargue_plano")
    private CarguePlano cargue;   
    
}
