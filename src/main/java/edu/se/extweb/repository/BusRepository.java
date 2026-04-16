package edu.se.extweb.repository;

import edu.se.extweb.model.Bus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusRepository extends MongoRepository<Bus, String> {
    boolean existsByRouteNumber(String routeNumber);
}