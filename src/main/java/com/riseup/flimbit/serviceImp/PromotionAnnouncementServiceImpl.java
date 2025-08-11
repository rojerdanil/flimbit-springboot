package com.riseup.flimbit.serviceImp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.PromotionAnnouncement;
import com.riseup.flimbit.repository.PromotionAnnouncementRepository;
import com.riseup.flimbit.repository.PromotionAnnouncementService;

import java.util.List;
import java.util.Optional;

@Service
public class PromotionAnnouncementServiceImpl implements PromotionAnnouncementService {

	@Autowired
    PromotionAnnouncementRepository repository;

   
    @Override
    public PromotionAnnouncement addAnnouncement(PromotionAnnouncement announcement) {
        return repository.save(announcement);
    }

    @Override
    public PromotionAnnouncement updateAnnouncement(Integer id, PromotionAnnouncement announcement) {
        Optional<PromotionAnnouncement> existing = repository.findById(id);
        if (existing.isPresent()) {
            PromotionAnnouncement updated = existing.get();
            updated.setMovieId(announcement.getMovieId());
            updated.setTitle(announcement.getTitle());
            updated.setDescription(announcement.getDescription());
            updated.setAnnouncementType(announcement.getAnnouncementType());
            updated.setValidFrom(announcement.getValidFrom());
            updated.setValidTo(announcement.getValidTo());
            updated.setStatus(announcement.getStatus());
            return repository.save(updated);
        }
        return null;
    }

    @Override
    public void deleteAnnouncement(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public List<PromotionAnnouncement> getAnnouncementsByMovieId(Integer movieId) {
        return repository.findByMovieId(movieId);
    }
}
