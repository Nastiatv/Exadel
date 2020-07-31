package com.exadel.booking.entities.queue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QueueRepository extends JpaRepository<Queue, UUID> {

    public Queue findQueueById(UUID id);
}