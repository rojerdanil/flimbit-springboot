package com.riseup.flimbit.service;

import com.riseup.flimbit.entity.Announcement;
import com.riseup.flimbit.entity.dto.AnnouncementTableDTO;
import com.riseup.flimbit.request.AnnouncementRequest;
import com.riseup.flimbit.request.DataTableAnnouncementRequest;

import java.util.List;

import org.springframework.data.domain.Page;

public interface AnnouncementService {
    Announcement create(AnnouncementRequest request);
    Announcement update(int id, AnnouncementRequest request);
    void delete(int id);
    List<Announcement> findAll();
    Page<AnnouncementTableDTO> getFilteredAnnouncements(int language,String status,String searchText, int start, int length,
			String sortColumn, String sortOrder) ;
    
    Announcement  getAnnonceById(int id);
}
