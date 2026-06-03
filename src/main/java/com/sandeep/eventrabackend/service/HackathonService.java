package com.sandeep.eventrabackend.service;

import com.sandeep.eventrabackend.dto.request.HackathonCreateRequest;
import com.sandeep.eventrabackend.dto.response.HackathonResponse;
import com.sandeep.eventrabackend.exception.HackathonNotFoundException;
import com.sandeep.eventrabackend.model.Hackathon;
import com.sandeep.eventrabackend.repository.HackathonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HackathonService {

    private final HackathonRepository hackathonRepository;

    public HackathonService(HackathonRepository hackathonRepository) {
        this.hackathonRepository = hackathonRepository;
    }

    @Transactional(readOnly = true)
    public List<HackathonResponse> getAllHackathons() {
        return hackathonRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HackathonResponse getHackathonById(Long id) {
        return hackathonRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new HackathonNotFoundException("Hackathon not found with id: " + id));
    }

    @Transactional
    public HackathonResponse createHackathon(HackathonCreateRequest request) {
        Hackathon hackathon = Hackathon.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .organizer(request.getOrganizer())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .location(request.getLocation())
                .mode(request.getMode())
                .prizePool(request.getPrizePool())
                .registrationDeadline(request.getRegistrationDeadline())
                .imageUrl(request.getImageUrl())
                .build();

        Hackathon saved = hackathonRepository.save(hackathon);
        return mapToResponse(saved);
    }

    private HackathonResponse mapToResponse(Hackathon hackathon) {
        return HackathonResponse.builder()
                .id(hackathon.getId())
                .title(hackathon.getTitle())
                .description(hackathon.getDescription())
                .organizer(hackathon.getOrganizer())
                .startDate(hackathon.getStartDate())
                .endDate(hackathon.getEndDate())
                .location(hackathon.getLocation())
                .mode(hackathon.getMode())
                .prizePool(hackathon.getPrizePool())
                .registrationDeadline(hackathon.getRegistrationDeadline())
                .imageUrl(hackathon.getImageUrl())
                .build();
    }
}
