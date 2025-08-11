package com.riseup.flimbit.constant;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.SystemSettings;
import com.riseup.flimbit.repository.SystemSettingsRepository;

import jakarta.annotation.PostConstruct;

@Service
public class ConfigCacheService {

	@Autowired
	private SystemSettingsRepository systemSettingsRepository;
	
	 @Value("${max_invest_amount}")
	  private String maxInvestProp;
	

	private final Map<String, String> cache = new HashMap<>();

	@PostConstruct
    public void loadConstantSettings() {
        // Load only those keys which act as constants
		String maxInvestSys = getValueForKey("max_invest_amount", "SYSTEM")  ;
		
		String maxAmount = maxInvestSys != null ? maxInvestSys : maxInvestProp;
		
        cache.put("max_invest_amount", maxAmount  );
    }

	private String getValueForKey(String key, String group) {
		return systemSettingsRepository.findByKeyIgnoreCaseAndGroupNameIgnoreCase(key, group)
				.map(SystemSettings::getValue).orElse(null);
	}

	public String getConstant(String key) {
		return cache.get(key);
	}
}
