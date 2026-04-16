package edu.se.extweb.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Document
public class Bus extends AuditMetadata {
    @Id
    private String id;
    private String brand;
    private String routeNumber;
    private String destination;

    public Bus(String brand, String routeNumber, String destination) {
        this.brand = brand;
        this.routeNumber = routeNumber;
        this.destination = destination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bus bus = (Bus) o;
        return getId().equals(bus.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}