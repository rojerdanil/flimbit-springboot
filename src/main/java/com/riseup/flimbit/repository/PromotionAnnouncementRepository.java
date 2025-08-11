package com.riseup.flimbit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.riseup.flimbit.entity.PromotionAnnouncement;

public interface PromotionAnnouncementRepository extends JpaRepository<PromotionAnnouncement, Integer> {

    List<PromotionAnnouncement> findByMovieId(Integer movieId);
}