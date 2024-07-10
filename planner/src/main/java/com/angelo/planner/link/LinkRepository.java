package com.angelo.planner.link;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LinkRepository extends JpaRepository<Link,UUID>{   
    List<LinkData> findByTripId(UUID tripId);
}