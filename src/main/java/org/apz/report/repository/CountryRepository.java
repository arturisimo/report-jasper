package org.apz.report.repository;

import org.apz.report.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country,Integer>{
	
}
