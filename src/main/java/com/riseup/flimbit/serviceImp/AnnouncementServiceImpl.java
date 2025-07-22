package com.riseup.flimbit.serviceImp;

import com.riseup.flimbit.constant.ActionType;
import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.entity.Announcement;
import com.riseup.flimbit.entity.dto.AnnouncementTableDTO;
import com.riseup.flimbit.repository.AnnouncementRepository;
import com.riseup.flimbit.request.AnnouncementRequest;
import com.riseup.flimbit.request.DataTableAnnouncementRequest;
import com.riseup.flimbit.security.UserContextHolder;
import com.riseup.flimbit.service.AnnouncementService;
import com.riseup.flimbit.utility.DateUtility;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public AnnouncementServiceImpl(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @Autowired
	AuditLogServiceImp audit;

    @Transactional
    @Override
    public Announcement create(AnnouncementRequest request) {
        Announcement announcement = new Announcement();
        copyFromRequest(announcement, request);
        
        announcement = announcementRepository.save(announcement);
        audit.logAction(UserContextHolder.getContext().getUserId(),ActionType.CREATE.name()
				, EntityName.ANNOUNCEMENT.name(), announcement.getId(), "New announcement is added" , request);

        return announcement;
    }

    @Transactional
    @Override
    public Announcement update(int id, AnnouncementRequest request) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found "+id	));
        copyFromRequest(announcement, request);
        announcement.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        audit.logAction(UserContextHolder.getContext().getUserId(),ActionType.UPDATE.name()
				, EntityName.ANNOUNCEMENT.name(), announcement.getId(), " announcement is updated "+id , announcement);

        return announcementRepository.save(announcement);
    }

    @Override
    @Transactional
    public void delete(int id) {
    	Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found " +id));
    	
    	 audit.logAction(UserContextHolder.getContext().getUserId(),ActionType.DELETE.name()
 				, EntityName.ANNOUNCEMENT.name(), announcement.getId(), "Annoncement is deleted " + id  , announcement);

    	
        announcementRepository.deleteById(id);
    }

    @Override
    public List<Announcement> findAll() {
        return announcementRepository.findAll();
    }

    private void copyFromRequest(Announcement ann, AnnouncementRequest req) {
        ann.setTitle(req.getTitle());
        ann.setMessage(req.getMessage());
        ann.setLanguageId(req.getLanguageId());
        if(req.getStartDatetime() !=null && !req.getStartDatetime().isBlank())
        ann.setStartDatetime( DateUtility.getTimeStampFromText(req.getStartDatetime()));
        if(req.getValidUntil() !=null && !req.getValidUntil().isBlank())
        ann.setValidUntil( DateUtility.getTimeStampFromText(req.getValidUntil())	);
        ann.setImageUrl(req.getImageUrl());
        ann.setLinkUrl(req.getLinkUrl());
        ann.setPriority(req.getPriority());
        ann.setStatus(req.getStatus());
        ann.setNotifyEmail(req.isNotifyEmail());
        ann.setNotifyInApp(req.isNotifyInApp());
        ann.setNotifyPush(req.isNotifyPush());
        ann.setNotifySMS(req.isNotifySMS());
    }

	@Override
	public Page<AnnouncementTableDTO> getFilteredAnnouncements(int language,String status,String searchText, int start, int length,
			String sortColumn, String sortOrder) {
		// TODO Auto-generated method stub
		   
		
		    int page = start / length;
	        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortColumn);
	        Pageable pageable = PageRequest.of(page, length, sort);
		    
	       return  announcementRepository.fetchAnnouncementsNative(
		        language,
		        status,
		        searchText,
		        pageable
		    );

	    }

	@Override
	public Announcement getAnnonceById(int id) {
		// TODO Auto-generated method stub
		return announcementRepository.findById(id)
		.orElseThrow(() -> new RuntimeException("Id is not found"));
		 
	}
}
