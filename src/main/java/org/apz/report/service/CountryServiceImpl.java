package org.apz.report.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apz.report.model.Country;
import org.apz.report.repository.CountryRepository;
import org.apz.report.util.JasperUtils;
import org.apz.report.util.JasperUtils.TypeReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CountryServiceImpl implements CountryService {

	@Autowired
	CountryRepository countryRepository;
	
	@Value("${tmp.location}")
	String tmpDir;
	
	@Override
	public List<Country> getCountries() {
		return countryRepository.findAll();
	}
	
	@Override
	public File getReport(TypeReport typeReport)  throws Exception {

		String jasper = CountryServiceImpl.class.getResource("/jasper/countries.jasper").getFile();
		
		String fileName = "countries." + typeReport.getExt();
		
		String output = tmpDir + fileName;
		
        List<Country> countries = getCountries();
        Map<String, Object> parameters = new HashMap<>();
	    parameters.put("TITULO", "PAISES");
	    parameters.put("FECHA", new java.util.Date());
	    JasperUtils.listReport(jasper, countries, typeReport, parameters, output);
        
        return new File(output);
		
	}
	
}
