package com.riseup.flimbit.repository;

import java.util.List;

import com.riseup.flimbit.entity.PromotionAnnouncement;

public interface PromotionAnnouncementService {
	PromotionAnnouncement addAnnouncement(PromotionAnnouncement announcement);

    PromotionAnnouncement updateAnnouncement(Integer id, PromotionAnnouncement announcement);

    void deleteAnnouncement(Integer id);

    List<PromotionAnnouncement> getAnnouncementsByMovieId(Integer movieId);
}
