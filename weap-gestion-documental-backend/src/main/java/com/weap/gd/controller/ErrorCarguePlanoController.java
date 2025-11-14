package com.weap.gd.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weap.gd.service.ErrorCarguePlanoService;

@RestController
@RequestMapping("/api/v1/plano/error-cargue")
@CrossOrigin("*")
public class ErrorCarguePlanoController {
	
	@Autowired
	private ErrorCarguePlanoService errorCarguePlanoService;

	@GetMapping("/{id}")
	public ResponseEntity<byte[]> exportErrorToExcel(@PathVariable Long id) {
		try {
			byte[] bytes = errorCarguePlanoService.exportErrorCargueToExcel(id);
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=error_medicamentos.xlsx");
			return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
