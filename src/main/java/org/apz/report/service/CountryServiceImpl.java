package org.apz.report.service;

import java.util.List;

import org.apz.report.model.Country;
import org.apz.report.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountryServiceImpl implements CountryService {

	@Autowired
	CountryRepository countryRepository;
	
	
	@Override
	public List<Country> getCountries() {
		return countryRepository.findAll();
	}

}
