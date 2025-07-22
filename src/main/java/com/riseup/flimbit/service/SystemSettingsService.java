package com.riseup.flimbit.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.SystemSettings;
import com.riseup.flimbit.entity.dto.AnnouncementTableDTO;
import com.riseup.flimbit.repository.SystemSettingsRepository;
import com.riseup.flimbit.request.SettingRequest;
import com.riseup.flimbit.response.KeyValueResponse;

import io.micrometer.core.ipc.http.HttpSender.Request;
import jakarta.transaction.Transactional;

@Service
public class SystemSettingsService {
	
	    @Autowired
	    private SystemSettingsRepository systemSettingsRepository;

	    // Fetch value based on key and group name
	    public String getValue(String key, String groupName) {
	        SystemSettings setting = systemSettingsRepository.findByKeyIgnoreCaseAndGroupNameIgnoreCase (key, groupName)
	        		.orElseThrow(() -> new RuntimeException("Key and Type is not found " + key + " "+ groupName));
	        return setting != null ? setting.getValue() : null;
	    }

	    // Save or update a setting
	    public SystemSettings save(SettingRequest request) {
	    	SystemSettings setting  = new SystemSettings();
	    	copyToSystemSetting(setting,request);
	        return systemSettingsRepository.save(setting);
	    }
	    public SystemSettings update(int id,SettingRequest request) {
	    	SystemSettings setting  = systemSettingsRepository.findById(id).orElseThrow(() -> new RuntimeException("System Setting is not found for id " + id));
	    	copyToSystemSetting(setting,request);
	    	setting.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
	        return systemSettingsRepository.save(setting);
	    }
	    public void copyToSystemSetting(SystemSettings setting,SettingRequest request)
	    {
	    	
	    	setting.setDescription(request.getDescription());
	    	setting.setGroupName(request.getGroupName());
	    	setting.setKey(request.getKey());
	    	setting.setType(request.getType());
	    	setting.setValue(request.getValue());
	    }

	    // Get all settings
	    public List<SystemSettings> getAllSettings() {
	        return systemSettingsRepository.findAll();
	    }
	    
	    public Page<SystemSettings> getFilteredSystemSetting(String type,String searchText, int start, int length,
				String sortColumn, String sortOrder)
	    {
	    	     int page = start / length;
		        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortColumn);
		        Pageable pageable = PageRequest.of(page, length, sort);	
		        
		        
	    	return systemSettingsRepository.fetchSystemSetting(type, searchText, pageable);
	    }
	    
	    public SystemSettings  getSystemSettingById(int id)
	    {
	    	
	    	
	    	return systemSettingsRepository.findById(id).orElseThrow(() -> new RuntimeException("System Setting is not found for id " + id));
	    }
	    @Transactional
	    public void delete(int id)
	    {
	    	systemSettingsRepository.deleteById(id);
	    }
      public List<KeyValueResponse>  getGroups()
      {
    	  List<String>  list= systemSettingsRepository.getGroups();
    	  List<KeyValueResponse> keyList = new ArrayList<KeyValueResponse>();
    	  
    	  if(list !=null)
    	  {
    		    for(String str : list)
    		    {
    		    	keyList.add(  KeyValueResponse.builder().key(str).value(str).build());	
    		    }
    		    	
    	  }
    	  
    	  
    	  return keyList;
      }
}
