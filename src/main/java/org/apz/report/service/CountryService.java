package org.apz.report.service;

import java.io.File;
import java.util.List;

import org.apz.report.model.Country;
import org.apz.report.util.JasperUtils.TypeReport;

public interface CountryService {
	
	List<Country> getCountries();
	
	File getReport(TypeReport typeReport)  throws Exception;
	
}
