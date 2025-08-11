package com.riseup.flimbit.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.entity.PromotionAnnouncement;
import com.riseup.flimbit.repository.PromotionAnnouncementService;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
public class PromotionAnnouncementController {

@Autowired
 PromotionAnnouncementService service;

    @PostMapping
    public ResponseEntity<PromotionAnnouncement> createAnnouncement(@RequestBody PromotionAnnouncement announcement) {
        return ResponseEntity.ok(service.addAnnouncement(announcement));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromotionAnnouncement> updateAnnouncement(
            @PathVariable Integer id,
            @RequestBody PromotionAnnouncement announcement) {
        PromotionAnnouncement updated = service.updateAnnouncement(id, announcement);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Integer id) {
        service.deleteAnnouncement(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<PromotionAnnouncement>> getAnnouncementsByMovieId(@PathVariable Integer movieId) {
        return ResponseEntity.ok(service.getAnnouncementsByMovieId(movieId));
    }
}
