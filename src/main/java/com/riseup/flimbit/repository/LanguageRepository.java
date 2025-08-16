package com.riseup.flimbit.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.Language;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
	
	List<Language> findAllByStatusIgnoreCaseOrderByNameAsc(String status);

}
