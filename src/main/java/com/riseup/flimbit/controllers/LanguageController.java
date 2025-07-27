package com.riseup.flimbit.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.Language;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.LanguageService;
import com.riseup.flimbit.utility.JwtService;

import java.util.List;

@RestController
@RequestMapping(value = "/languages")
public class LanguageController {
	Logger logger
    = LoggerFactory.getLogger(LanguageController.class);
	
	
	@Autowired
    LanguageService languageService;

    
    @GetMapping("/alllangugage")
    public ResponseEntity<?> getAllLanguages(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken) {
    	
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_SUCCESS).result(languageService.getAllLanguages()).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Language> getLanguageById(@PathVariable Long id) {
        return languageService.getLanguageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Language> createLanguage(@RequestBody Language language) {
        return ResponseEntity.ok(languageService.createLanguage(language));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Language> updateLanguage(@PathVariable Long id, @RequestBody Language language) {
        return ResponseEntity.ok(languageService.updateLanguage(id, language));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLanguage(@PathVariable Long id) {
        languageService.deleteLanguage(id);
        return ResponseEntity.noContent().build();
    }
}
