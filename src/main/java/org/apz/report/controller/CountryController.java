package org.apz.report.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apz.report.service.CountryService;
import org.apz.report.util.JasperUtils;
import org.apz.report.util.JasperUtils.TypeReport;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	@RequestMapping
    public ModelAndView country(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("countries", countryService.getCountries());
        modelAndView.addObject("reports", JasperUtils.TypeReport.values());
        modelAndView.setViewName("country");
        return modelAndView;
    }
	
	@RequestMapping("/export/{type}")
    public ResponseEntity<Resource> export(@PathVariable("type") TypeReport type) throws Exception{
		
		File report = countryService.getReport(type);
		
		
        Path path = Paths.get(report.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + report.getName());
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        
        return ResponseEntity.ok()
	            .headers(headers)
	            .contentLength(report.length())
	            .contentType(MediaType.parseMediaType(type.getMimeType()))
	            .body(resource);
    }
		
}