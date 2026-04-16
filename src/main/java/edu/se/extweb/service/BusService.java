package edu.se.extweb.service;

import edu.se.extweb.model.Bus;
import edu.se.extweb.repository.BusRepository;
import edu.se.extweb.request.BusCreateRequest;
import edu.se.extweb.request.BusUpdateRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusService {

    private final BusRepository busRepository;
    private final Environment environment;

    private List<Bus> initialBuses = new ArrayList<>();

    {
        initialBuses.add(new Bus("Mercedes", "101", "Central Station"));
        initialBuses.add(new Bus("Volvo", "102", "Airport"));
        initialBuses.add(new Bus("MAN", "103", "University"));
        initialBuses.add(new Bus("Scania", "104", "Shopping Mall"));
        initialBuses.add(new Bus("Mercedes", "105", "Hospital"));
        initialBuses.add(new Bus("Volvo", "106", "Train Station"));
        //initialBuses.add(new Bus("MAN", "107", "City Park"));
    }

    @PostConstruct
    void init() {
        boolean isTestProfile = Arrays.asList(environment.getActiveProfiles()).contains("test");
        System.out.println(">>> ACTIVE PROFILES: " + Arrays.toString(environment.getActiveProfiles()));
        System.out.println(">>> IS TEST: " + isTestProfile);
        if (isTestProfile) return;

        this.busRepository.deleteAll();
        for (Bus bus : initialBuses) {
            create(bus);
        }
    }

    public List<Bus> getAll() {
        return busRepository.findAll();
    }

    public Bus getById(String id) {
        return busRepository.findById(id).get();
    }

    public Bus create(Bus bus) {
        return busRepository.save(bus);
    }

    public Bus create(BusCreateRequest request) {
        return busRepository.save(new Bus(request.brand(), request.routeNumber(), request.destination()));
    }

    public Bus update(Bus bus) {
        return busRepository.save(bus);
    }

    public Bus update(BusUpdateRequest request) {
        Bus persisted = busRepository.findById(request.id()).orElse(null);
        if (persisted != null) {
            Bus toUpdate = Bus.builder()
                    .id(request.id())
                    .brand(request.brand())
                    .routeNumber(request.routeNumber())
                    .destination(request.destination())
                    .build();
            return busRepository.save(toUpdate);
        }
        return null;
    }

    public void delById(String id) {
        busRepository.deleteById(id);
    }

    public List<Bus> createAll(List<Bus> buses) {
        return busRepository.saveAll(buses);
    }

    public void deleteAll() {
        busRepository.deleteAll();
    }
}