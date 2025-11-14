package com.weap.gd.service;

import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.weap.gd.entity.ErrorCarguePlano;
import com.weap.gd.entity.Plano;
import com.weap.gd.repository.PlanoRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ValidacionPlanoServiceImpl implements ValidacionPlanoService{
	
	private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private final PlanoRepository planoCitaRepository;
	private final Validator validator;

    int batchSize = 100;
    
    public ValidacionPlanoServiceImpl(
			PlanoRepository planoCitaRepository,
			Validator validator) {
		this.planoCitaRepository = planoCitaRepository;
		this.validator = validator;
	}
    
    @Override
	public List<ErrorCarguePlano> validarPlano(List<Plano> archivos,String idPrestador, String usuario) {
		// TODO Auto-generated method stub
		long totalStart = System.currentTimeMillis();
	    List<ErrorCarguePlano> errores = new ArrayList<>();
	     
		// ✅ 1. Validar prestador y sede antes de todo
	    long t1 = System.currentTimeMillis();

	 // 2️ Obtener afiliados en lotes
	    t1 = System.currentTimeMillis();
	    log.info("⏱ Tiempo obtner afiliados: {} ms", System.currentTimeMillis() - t1);
	    
	 // ✅ 5️⃣ Validaciones individuales por registro (usando cache y afiliados ya cargados)
	    for (Plano archivo : archivos) {

	        // A. Validaciones de anotaciones
	        if (validarCampos(archivo, errores)) continue;
	        // B. Validaciones de coherencia individual
	        
	    }
	    
	    errores.addAll(validarRegistroDuplicado(archivos));

	 // ✅ Fin total
	    log.info("🚀 Tiempo total de validación: {} ms", System.currentTimeMillis() - totalStart);
	    return errores;
	}
	
	private boolean validarCampos(Plano archivo, List<ErrorCarguePlano> errores) {
	    Set<ConstraintViolation<Plano>> violaciones = validator.validate(archivo);
	    if (!violaciones.isEmpty()) {
	        for (ConstraintViolation<Plano> v : violaciones) {
	        	String campo = v.getPropertyPath().toString();
	        	boolean esCampoFecha = campo.equalsIgnoreCase("fechaSolicitud")
	                    || campo.equalsIgnoreCase("fechaDeseada")
	                    || campo.equalsIgnoreCase("fechaCita");
	        	
	        	String valor = esCampoFecha ? null
	                    : (v.getInvalidValue() != null ? String.valueOf(v.getInvalidValue()) : null);
	            errores.add(ErrorCarguePlano.builder()
	                    .tipoError("Estructura")
	                    .campo(v.getPropertyPath().toString())
	                    .error(v.getMessage())
	                    .valorAsociado(valor)
	                    .numeroLinea(archivo.getLinea())
	                    .build());
	        }
	        return true; // Detener validaciones siguientes
	    }
	    return false;
	}
	
	
	
	private List<ErrorCarguePlano> validarRegistroDuplicado(List<Plano> archivos) {
	    Map<String, List<Integer>> mapaDuplicados = new HashMap<>();

	    for (Plano it : archivos) {
	        // Normalizar campos
	        String numeroIdentificacion = normalizarVacioANulo(it.getNumeroIdentificacion());

	        // Validar campos requeridos
	        if (numeroIdentificacion == null) {
	            continue; // saltar registros incompletos
	        }

	        // Crear clave compuesta única
	        String clave = numeroIdentificacion;

	        // Agrupar líneas por clave
	        mapaDuplicados.computeIfAbsent(clave, k -> new ArrayList<>()).add(it.getLinea());
	    }

	    List<ErrorCarguePlano> errores = new ArrayList<>();

	    // Buscar claves con más de un registro (duplicados)
	    for (Map.Entry<String, List<Integer>> entry : mapaDuplicados.entrySet()) {
	        List<Integer> lineas = entry.getValue();

	        if (lineas.size() > 1) {
	            for (Integer linea : lineas) {
	                errores.add(ErrorCarguePlano.builder()
	                        .tipoError("Coherencia")
	                        .campo("numeroIdentificacion|fechaCita|codigoServicio")
	                        .error("Registro duplicado con el mismo numero de identificación, fecha de cita y código de servicio.")
	                        .valorAsociado(entry.getKey())
	                        .numeroLinea(linea)
	                        .build());
	            }
	        }
	    }

	    return errores;
	}
	
	private static String normalizarVacioANulo(String campo) {
	    if (campo == null) return null;
	    campo = campo.trim();
	    return campo.isEmpty() ? null : campo;
	}
	
	private static Integer normalizarVacioANuloInt(Integer campo) {
	    if (campo == null) {
	        return null;
	    }
	    return campo;
	}


	

}
