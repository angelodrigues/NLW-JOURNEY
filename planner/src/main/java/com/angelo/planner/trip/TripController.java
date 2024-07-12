package com.angelo.planner.trip;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angelo.planner.activity.ActivityData;
import com.angelo.planner.activity.ActivityRequestPayload;
import com.angelo.planner.activity.ActivityResponse;
import com.angelo.planner.activity.ActivityService;
import com.angelo.planner.link.LinkData;
import com.angelo.planner.link.LinkRequestPayload;
import com.angelo.planner.link.LinkResponse;
import com.angelo.planner.link.LinkService;
import com.angelo.planner.participant.ParticipantCreateResponse;
import com.angelo.planner.participant.ParticipantData;
import com.angelo.planner.participant.ParticipantRequest;
import com.angelo.planner.participant.ParticipantService;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private TripService tripService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private LinkService linkService;

    //Trip
    @PostMapping
    public ResponseEntity<TripResponse> createTrip(@RequestBody TripRequestPayload payload){               
        return ResponseEntity.ok(this.tripService.registerTrip(payload));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id){                
        return this.tripService
                .findTripById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload){    
        return Optional.ofNullable(this.tripService.updateTrip(id, payload))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id){
        Optional<Trip> trip = this.tripService.findTripById(id);

        if(trip.isPresent()){
            Trip rawTrip = trip.get();
            rawTrip.setIsConfirmed(true);
            this.participantService.triggerConfirmationEmailToParticipants(id);

            return ResponseEntity.ok(rawTrip);
        }
        return ResponseEntity.notFound().build();
    }

    //Participant
    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequest payload){
        Optional<Trip> trip = this.tripService.findTripById(id);

        if(trip.isPresent()){
            Trip rawTrip = trip.get();  

            ParticipantCreateResponse participantResponse = this.participantService.registerParticipantToEvent(payload.email(),rawTrip);

            if(rawTrip.getIsConfirmed()) this.participantService.triggerConfirmationEmailToParticipant(payload.email());

            return ResponseEntity.ok(participantResponse);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID id){
        List<ParticipantData> participantList = this.participantService.getAllParticipantsFromEvent(id);

        return ResponseEntity.ok(participantList);
    }

    //Activity
    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityResponse> registerActivity(@PathVariable UUID id, @RequestBody ActivityRequestPayload payload){
        Optional<Trip> trip = this.tripService.findTripById(id);

        if(trip.isPresent()){
            Trip rawTrip = trip.get();  

            ActivityResponse activityResponse = this.activityService.registerActivity(payload,rawTrip);

            return ResponseEntity.ok(activityResponse);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable UUID id){
        List<ActivityData> activityDatas = this.activityService.getAllActivitiesFromId(id);

        return ResponseEntity.ok(activityDatas);
    }

    //Link
    @PostMapping("/{id}/links")
    public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id, @RequestBody LinkRequestPayload payload){
        Optional<Trip> trip = this.tripService.findTripById(id);

        if(trip.isPresent()){
            Trip rawTrip = trip.get();  

            LinkResponse linkResponse = this.linkService.registerLink(payload,rawTrip);

            return ResponseEntity.ok(linkResponse);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinkData>> getAllLinks(@PathVariable UUID id){
        List<LinkData> linkDatas = this.linkService.getAllLinkFromId(id);

        return ResponseEntity.ok(linkDatas);
    }
}