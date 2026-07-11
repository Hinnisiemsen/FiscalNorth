package de.fiscalnorth.household.controller;

import de.fiscalnorth.household.dto.CreateHouseholdInviteRequest;
import de.fiscalnorth.household.dto.HouseholdInviteDto;
import de.fiscalnorth.household.dto.HouseholdResponse;
import de.fiscalnorth.household.service.HouseholdService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/household")
@RequiredArgsConstructor
public class HouseholdController {

    private final HouseholdService householdService;

    @GetMapping("/me")
    public ResponseEntity<HouseholdResponse> getMyHousehold() {
        return ResponseEntity.ok(householdService.getMyHousehold());
    }

    @PostMapping("/invite")
    public ResponseEntity<HouseholdInviteDto> invitePartner(@RequestBody @Valid CreateHouseholdInviteRequest request) {
        return ResponseEntity.ok(householdService.invitePartner(request.email()));
    }

    @PostMapping("/invites/accept")
    public ResponseEntity<HouseholdResponse> acceptInvite(@RequestParam String token) {
        return ResponseEntity.ok(householdService.acceptInvite(token));
    }
}
