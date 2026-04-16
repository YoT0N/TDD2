package edu.se.extweb.request;

public record BusUpdateRequest(String id, String brand, String routeNumber, String destination) {
}