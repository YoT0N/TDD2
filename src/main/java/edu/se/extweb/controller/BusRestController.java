package edu.se.extweb.controller;

import edu.se.extweb.model.Bus;
import edu.se.extweb.request.BusCreateRequest;
import edu.se.extweb.request.BusPageRequest;
import edu.se.extweb.request.BusUpdateRequest;
import edu.se.extweb.response.ApiResponse;
import edu.se.extweb.response.BaseMetaData;
import edu.se.extweb.response.PaginationMetaData;
import edu.se.extweb.service.BusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/buses/")
@RequiredArgsConstructor
public class BusRestController {

    private final BusService busService;

    // ─── Прості CRUD ──────────────────────────────────────────────────────────

    @GetMapping
    public List<Bus> showAll() {
        return busService.getAll();
    }

    @GetMapping("{id}")
    public Bus showOneById(@PathVariable String id) {
        return busService.getById(id);
    }

    @PostMapping
    public Bus insert(@RequestBody Bus bus) {
        return busService.create(bus);
    }

    @PostMapping("/dto")
    public Bus insert(@RequestBody BusCreateRequest request) {
        return busService.create(request);
    }

    @PutMapping
    public Bus edit(@RequestBody Bus bus) {
        return busService.update(bus);
    }

    @PutMapping("/dto")
    public Bus edit(@RequestBody BusUpdateRequest request) {
        return busService.update(request);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        busService.delById(id);
    }

    // ─── ApiResponse ендпоінти ─────────────────────────────────────────────────

    @GetMapping("response/all")
    public ApiResponse<BaseMetaData, Bus> showAllAsResponse() {
        return busService.getAllAsApiResponse();
    }

    @GetMapping("response/{id}")
    public ApiResponse<BaseMetaData, Bus> showOneByIdAsResponse(@PathVariable String id) {
        return busService.getByIdAsApiResponse(id);
    }

    @PostMapping("response")
    public ApiResponse<BaseMetaData, Bus> insertAsResponse(@RequestBody Bus bus) {
        return busService.createAsApiResponse(bus);
    }

    @PostMapping("response/dto")
    public ApiResponse<BaseMetaData, Bus> insertAsResponse(@RequestBody BusCreateRequest request) {
        return busService.createAsApiResponse(request);
    }

    @PutMapping("response")
    public ApiResponse<BaseMetaData, Bus> editAsResponse(@RequestBody Bus bus) {
        return busService.updateAsApiResponse(bus);
    }

    @PutMapping("response/dto")
    public ApiResponse<BaseMetaData, Bus> editAsResponse(@RequestBody BusUpdateRequest request) {
        return busService.updateAsApiResponse(request);
    }

    // ─── Пагінація ─────────────────────────────────────────────────────────────

    @GetMapping("response/page")
    public ApiResponse<PaginationMetaData, Bus> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return busService.getBusesPage(new BusPageRequest(page, size));
    }
}