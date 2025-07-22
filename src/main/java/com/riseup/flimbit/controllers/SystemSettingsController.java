package com.riseup.flimbit.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.entity.SystemSettings;
import com.riseup.flimbit.entity.dto.AnnouncementTableDTO;
import com.riseup.flimbit.request.SettingRequest;
import com.riseup.flimbit.service.SystemSettingsService;
import com.riseup.flimbit.utility.HttpResponseUtility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/settings/")
public class SystemSettingsController {

    @Autowired
    private SystemSettingsService systemSettingsService;

    // Fetch value of a setting based on key and group name
    @GetMapping("/{key}/{groupName}")
    public String getSetting(@PathVariable String key, @PathVariable String groupName) {
        return systemSettingsService.getValue(key, groupName);
    }
    
    @GetMapping("/read/{id}")
    public ResponseEntity<?> getSetting( @PathVariable int id) {
        return HttpResponseUtility.getHttpSuccess(systemSettingsService.getSystemSettingById(id));
    }

    // Fetch all settings
    @GetMapping
    public List<SystemSettings> getAllSettings() {
        return systemSettingsService.getAllSettings();
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateSetting( @PathVariable int id,@RequestBody SettingRequest setting) {
        return  HttpResponseUtility.getHttpSuccess(systemSettingsService.update(id,setting));
    }
    // Save a new or update an existing setting
    @PostMapping("/add")
    public ResponseEntity<?> saveSetting(@RequestBody SettingRequest setting) {
        return  HttpResponseUtility.getHttpSuccess(systemSettingsService.save(setting));
    }
    
    
    @GetMapping("/dataTable")
    public ResponseEntity<?> getAllAnnouncements(
    		@RequestParam(value = "group", required = false) String group,
    		@RequestParam(value = "searchText", required = false) String searchText,
    		@RequestParam(value = "start", defaultValue = "0") int start,
    		@RequestParam(value = "length", defaultValue = "10") int length,
    		@RequestParam(value = "sortColumn", defaultValue = "created_at") String sortColumn,
    		@RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder,
    		@RequestParam(value = "draw", defaultValue = "1") int draw
    ) {
    	
    	

    	group = group == null  || group.isEmpty() ? null : group;
    	searchText = searchText == null || searchText.isEmpty() ? null : searchText;
    	
    	System.out.println( " "+ group + " "+searchText + draw  + " " + start + " "+length + " "+ sortOrder);
       
        Page<SystemSettings> page = systemSettingsService.getFilteredSystemSetting(
        		group, searchText, start, length, sortColumn, sortOrder
        );

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", page.getTotalElements());
        response.put("recordsFiltered", page.getTotalElements());
        	response.put("data", page.getContent());
        return HttpResponseUtility.getHttpSuccess(response);

    }
    
    @GetMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
    	systemSettingsService.delete(id);
        return HttpResponseUtility.getHttpSuccess("deleted successfully");

    }
    
    @GetMapping("/readGroup")
    public ResponseEntity<?> getGroup() {
        return HttpResponseUtility.getHttpSuccess(systemSettingsService.getGroups());
    }
}
