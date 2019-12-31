package org.apz.report.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apz.report.model.Country;
import org.apz.report.service.CountryService;
import org.apz.report.util.JasperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CountryController {
	
	@Autowired
    private CountryService countryService;
	
	@Value("${tmp.location}")
	String tmpDir;
	
	@RequestMapping
    public ModelAndView country(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("countries", countryService.getCountries());
        modelAndView.setViewName("country");
        return modelAndView;
    }
	
	@RequestMapping("/export/{type}")
    public ResponseEntity<Resource> export(@PathVariable("type") String type) throws Exception{
		
		String jasper = CountryController.class.getResource("/jasper/countries.jasper").getFile();
		
		String salida = tmpDir + "countries." + type;
		
        List<Country> country = countryService.getCountries();
        Map<String, Object> parameters = new HashMap<>();
	    parameters.put("TITULO", "PAISES");
	    parameters.put("FECHA", new java.util.Date());
        JasperUtils.listReport(jasper, country, type, parameters, salida);
        
        File file = new File(salida);
        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=countries." + type);
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        
        return ResponseEntity.ok()
	            .headers(headers)
	            .contentLength(file.length())
	            .contentType(MediaType.parseMediaType("application/octet-stream"))
	            .body(resource);
    }
	
	
	
	
}
