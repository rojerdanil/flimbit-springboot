package com.riseup.flimbit.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.StatusEnum;
import com.riseup.flimbit.entity.Language;
import com.riseup.flimbit.repository.LanguageRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LanguageService {

	@Autowired
    private final LanguageRepository languageRepository;

    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public List<Language> getAllLanguages() {
        return languageRepository.findAllByStatusIgnoreCaseOrderByNameAsc(StatusEnum.ACTIVE.name().toLowerCase());
    }

    public Optional<Language> getLanguageById(Long id) {
        return languageRepository.findById(id);
    }

    public Language createLanguage(Language language) {
        return languageRepository.save(language);
    }

    public Language updateLanguage(Long id, Language language) {
        language.setId(id);
        return languageRepository.save(language);
    }

    public void deleteLanguage(Long id) {
        languageRepository.deleteById(id);
    }
}
