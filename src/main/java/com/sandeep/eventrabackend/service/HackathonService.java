package com.sandeep.eventrabackend.service;

import com.sandeep.eventrabackend.dto.response.HackathonResponse;
import com.sandeep.eventrabackend.model.Hackathon;
import com.sandeep.eventrabackend.repository.HackathonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HackathonService {

    private final HackathonRepository hackathonRepository;

    public HackathonService(HackathonRepository hackathonRepository) {
        this.hackathonRepository = hackathonRepository;
    }

    public List<HackathonResponse> getAllHackathons() {
        return hackathonRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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
