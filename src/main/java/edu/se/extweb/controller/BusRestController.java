package edu.se.extweb.controller;

import edu.se.extweb.model.Bus;
import edu.se.extweb.request.BusCreateRequest;
import edu.se.extweb.request.BusUpdateRequest;
import edu.se.extweb.service.BusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/buses/")
@RequiredArgsConstructor
public class BusRestController {

    private final BusService busService;

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
}