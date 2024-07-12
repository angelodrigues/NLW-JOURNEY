package com.angelo.planner.trip;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.angelo.planner.participant.ParticipantService;

@Service
public class TripService {

    @Autowired
    private TripRepository repository;

    @Autowired
    private ParticipantService participantService;

    public TripResponse registerTrip(TripRequestPayload payload){
        Trip newTrip = new Trip(payload);

        this.repository.save(newTrip);

        this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

        return new TripResponse(newTrip.getId());
    }

    public Optional<Trip> findTripById(UUID id){
        Optional<Trip> trip = this.repository.findById(id);
        return trip;
    }

    public Trip updateTrip(UUID id, TripRequestPayload payload){
        Optional<Trip> trip = this.repository.findById(id);
        
        if(trip.isPresent()){
            Trip rawTrip = trip.get();
            rawTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(),DateTimeFormatter.ISO_DATE_TIME));
            rawTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(),DateTimeFormatter.ISO_DATE_TIME));
            rawTrip.setDestination(payload.destination());

            this.repository.save(rawTrip);

            return rawTrip;
        }        
        return null;
    }
}