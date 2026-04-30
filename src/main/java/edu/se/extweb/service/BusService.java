package edu.se.extweb.service;

import edu.se.extweb.model.Bus;
import edu.se.extweb.repository.BusRepository;
import edu.se.extweb.request.BusCreateRequest;
import edu.se.extweb.request.BusPageRequest;
import edu.se.extweb.request.BusUpdateRequest;
import edu.se.extweb.response.ApiResponse;
import edu.se.extweb.response.BaseMetaData;
import edu.se.extweb.response.PaginationMetaData;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.*;
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
        return busRepository.findById(id).orElse(null);
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

    // ─── ApiResponse методи ───────────────────────────────────────────────────

    public ApiResponse<BaseMetaData, Bus> getAllAsApiResponse() {
        List<Bus> buses = busRepository.findAll();
        BaseMetaData meta = BaseMetaData.builder()
                .code(200)
                .success(true)
                .build();
        return ApiResponse.<BaseMetaData, Bus>builder()
                .meta(meta)
                .data(buses)
                .build();
    }

    public ApiResponse<BaseMetaData, Bus> getByIdAsApiResponse(String id) {
        Bus persisted = busRepository.findById(id).orElse(null);
        if (persisted != null) {
            BaseMetaData meta = BaseMetaData.builder()
                    .code(200)
                    .success(true)
                    .build();
            return new ApiResponse<>(meta, persisted);
        }
        BaseMetaData errorMeta = BaseMetaData.builder()
                .code(404)
                .success(false)
                .errorMessage("Bus with id '" + id + "' not found")
                .build();
        ApiResponse<BaseMetaData, Bus> response = new ApiResponse<>(errorMeta);
        response.setData(new ArrayList<>());
        return response;
    }

    public ApiResponse<BaseMetaData, Bus> createAsApiResponse(Bus bus) {
        try {
            Bus saved = busRepository.save(bus);
            BaseMetaData meta = BaseMetaData.builder()
                    .code(201)
                    .success(true)
                    .build();
            return new ApiResponse<>(meta, saved);
        } catch (Exception e) {
            BaseMetaData errorMeta = BaseMetaData.builder()
                    .code(500)
                    .success(false)
                    .errorMessage("Failed to create bus: " + e.getMessage())
                    .build();
            return new ApiResponse<>(errorMeta);
        }
    }

    public ApiResponse<BaseMetaData, Bus> createAsApiResponse(BusCreateRequest request) {
        try {
            Bus saved = busRepository.save(
                    new Bus(request.brand(), request.routeNumber(), request.destination())
            );
            BaseMetaData meta = BaseMetaData.builder()
                    .code(201)
                    .success(true)
                    .build();
            return new ApiResponse<>(meta, saved);
        } catch (Exception e) {
            BaseMetaData errorMeta = BaseMetaData.builder()
                    .code(500)
                    .success(false)
                    .errorMessage("Failed to create bus: " + e.getMessage())
                    .build();
            return new ApiResponse<>(errorMeta);
        }
    }

    public ApiResponse<BaseMetaData, Bus> updateAsApiResponse(Bus bus) {
        boolean exists = busRepository.existsById(bus.getId());
        if (!exists) {
            BaseMetaData errorMeta = BaseMetaData.builder()
                    .code(404)
                    .success(false)
                    .errorMessage("Bus with id '" + bus.getId() + "' not found")
                    .build();
            ApiResponse<BaseMetaData, Bus> response = new ApiResponse<>(errorMeta);
            response.setData(new ArrayList<>());
            return response;
        }
        Bus updated = busRepository.save(bus);
        BaseMetaData meta = BaseMetaData.builder()
                .code(200)
                .success(true)
                .build();
        return new ApiResponse<>(meta, updated);
    }

    public ApiResponse<BaseMetaData, Bus> updateAsApiResponse(BusUpdateRequest request) {
        Bus persisted = busRepository.findById(request.id()).orElse(null);
        if (persisted == null) {
            BaseMetaData errorMeta = BaseMetaData.builder()
                    .code(404)
                    .success(false)
                    .errorMessage("Bus with id '" + request.id() + "' not found")
                    .build();
            ApiResponse<BaseMetaData, Bus> response = new ApiResponse<>(errorMeta);
            response.setData(new ArrayList<>());
            return response;
        }
        Bus toUpdate = Bus.builder()
                .id(request.id())
                .brand(request.brand())
                .routeNumber(request.routeNumber())
                .destination(request.destination())
                .build();
        Bus updated = busRepository.save(toUpdate);
        BaseMetaData meta = BaseMetaData.builder()
                .code(200)
                .success(true)
                .build();
        return new ApiResponse<>(meta, updated);
    }

    // ─── Пагінація ────────────────────────────────────────────────────────────

    public ApiResponse<PaginationMetaData, Bus> getBusesPage(BusPageRequest request) {
        List<Bus> all = busRepository.findAll();
        if (all.isEmpty()) {
            PaginationMetaData meta = PaginationMetaData.builder()
                    .code(200)
                    .success(true)
                    .errorMessage("No buses found in the database")
                    .number(0)
                    .size(request.size())
                    .totalElements(0)
                    .totalPages(0)
                    .isFirst(true)
                    .isLast(true)
                    .build();
            ApiResponse<PaginationMetaData, Bus> response = new ApiResponse<>(meta);
            response.setData(new ArrayList<>());
            return response;
        }

        int totalElements = (int) busRepository.count();
        int totalPages = (int) Math.ceil((double) totalElements / request.size());

        if (request.page() >= totalPages) {
            PaginationMetaData meta = PaginationMetaData.builder()
                    .code(400)
                    .success(false)
                    .errorMessage("Page index " + request.page() + " is out of range. Total pages: " + totalPages)
                    .number(request.page())
                    .size(request.size())
                    .totalElements(totalElements)
                    .totalPages(totalPages)
                    .isFirst(false)
                    .isLast(false)
                    .build();
            ApiResponse<PaginationMetaData, Bus> response = new ApiResponse<>(meta);
            response.setData(new ArrayList<>());
            return response;
        }

        Pageable pageable = PageRequest.of(request.page(), request.size(),
                Sort.by(Sort.Direction.ASC, "id"));
        Page<Bus> page = busRepository.findAll(pageable);

        PaginationMetaData meta = PaginationMetaData.builder()
                .code(200)
                .success(true)
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();

        return ApiResponse.<PaginationMetaData, Bus>builder()
                .meta(meta)
                .data(page.getContent())
                .build();
    }
}