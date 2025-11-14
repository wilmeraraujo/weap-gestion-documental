package com.weap.gd.controller;

import java.io.File;
import java.io.IOException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.weap.gd.entity.CarguePlano;
import com.weap.gd.service.CarguePlanoService;

@RestController
@RequestMapping("/api/v1/plano/cargue")
@CrossOrigin("*")
public class CarguePlanoController {
	
	@Autowired
    private JobLauncher jobLoteCargueLauncher;

    @Autowired
    private Job procesarLoteCargueJob;

    @Autowired
    private CarguePlanoService cargueService;

    @Value("${ruta.storage}")
    private String rutaStorage;
    
    @Value("${file.delimiter}")
    private String fileDelimiter;    

    @PutMapping("/delete-at/{id}")
    public ResponseEntity<?> updateFechaBaja(@PathVariable Long id) {
        return cargueService.findById(id)
                .map(cargue -> {
                	cargue.setDeleteAt(LocalDate.now());
                	return ResponseEntity.status(HttpStatus.CREATED).body(cargueService.save(cargue));                    
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/paginable")
    public ResponseEntity<?> listAll(Pageable pageable) {
        return ResponseEntity.ok(cargueService.findByAllIgnoreDeleteAt(pageable));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return cargueService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /*
    @GetMapping("/search/{term}")
	public ResponseEntity<?> filter(@PathVariable String term){
		return ResponseEntity.ok(cargueService.findByFiltro(term));
	}*/
    
    @GetMapping("/paginable/search")
    public ResponseEntity<Page<CarguePlano>> buscar(
            @RequestParam(name = "usuario") String usuario,
            @RequestParam(name = "term") String term,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CarguePlano> resultado = cargueService.buscarPorUsuarioYFiltro(usuario, term, pageable);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping(path = "/procesar")
    public ResponseEntity<?> startBatchProcessing(
            @Validated @ModelAttribute CarguePlano cargue,
            BindingResult result,
            @RequestParam("file") MultipartFile multipartFile          
            ) {

        if (result.hasErrors()) {
            return validateErrors(result);
        }
        if (result.hasErrors()) return validateErrors(result);
        
        if (multipartFile == null || multipartFile.isEmpty()) {
            return ResponseEntity.badRequest().body("Debe adjuntar un archivo.");
        }
       
        if (!validarExtension(multipartFile)) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Solo se permiten archivos con extensión .txt");
        }

        try {        	
           
            String newFileName = multipartFile.getOriginalFilename();       
            File fileToImport = saveMultipartFile(multipartFile,newFileName);       
            
            cargue.setNombreArchivo(newFileName);
            cargue.setFechaCargue(LocalDateTime.now());           
            cargue.setUsuario(cargue.getUsuario());            

            CarguePlano savedCargue = cargueService.save(cargue);
            cargueService.runBatchJobAsynchronously(fileToImport, savedCargue);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedCargue);

        } catch (IOException  e) {
            return ResponseEntity.badRequest().body("Error processing file: " + e.getMessage());
        } 
    }    

    @ExceptionHandler(FlatFileParseException.class)
    public ResponseEntity<?> handleFlatFileParseException(FlatFileParseException ex) {
        return ResponseEntity.badRequest().body("Error parsing file at line " + ex.getLineNumber() + ": " + ex.getInput());
    }
    
    private File saveMultipartFile(MultipartFile multipartFile, String newFileName) throws IOException {
        File fileToImport = new File(rutaStorage + newFileName);
        multipartFile.transferTo(fileToImport);
        return fileToImport;
    }       

    private ResponseEntity<?> validateErrors(BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        result.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }
    
    private boolean validarExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) return false;
        String ext = StringUtils.getFilenameExtension(filename);
        return ext != null && ext.equalsIgnoreCase("txt");
    }
    
}
