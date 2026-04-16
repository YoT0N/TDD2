package edu.se.extweb;

import edu.se.extweb.model.Bus;
import edu.se.extweb.request.BusCreateRequest;
import edu.se.extweb.request.BusUpdateRequest;
import edu.se.extweb.response.ApiResponse;
import edu.se.extweb.response.BaseMetaData;
import edu.se.extweb.service.BusService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BusServiceTest {

    @Autowired
    private BusService underTest;

    // Справжні ID з бази які знаю до запуску
    private String gmcBusId;
    private String dodgeBusId;

    @BeforeAll
    void setUp() {
        List<Bus> all = underTest.getAll();

        gmcBusId = all.stream()
                .filter(b -> "072414187".equals(b.getRouteNumber()))
                .findFirst()
                .map(Bus::getId)
                .orElseThrow(() -> new IllegalStateException(
                        "GMC bus (routeNumber=072414187) not found in DB"));

        dodgeBusId = all.stream()
                .filter(b -> "221272361".equals(b.getRouteNumber()))
                .findFirst()
                .map(Bus::getId)
                .orElseThrow(() -> new IllegalStateException(
                        "Dodge bus (routeNumber=221272361) not found in DB"));
    }

    // ─── getAllAsApiResponse ──────────────────────────────────────────────────────

    @Test
    @Order(1)
    void whenGetAllAsApiResponseThenSuccessIsTrue() {
        ApiResponse<BaseMetaData, Bus> response = underTest.getAllAsApiResponse();
        assertTrue(response.getMeta().isSuccess());
    }

    @Test
    @Order(2)
    void whenGetAllAsApiResponseThenCodeIs200() {
        ApiResponse<BaseMetaData, Bus> response = underTest.getAllAsApiResponse();
        assertEquals(200, response.getMeta().getCode());
    }

    @Test
    @Order(3)
    void whenGetAllAsApiResponseThenDataIsNotEmpty() {
        ApiResponse<BaseMetaData, Bus> response = underTest.getAllAsApiResponse();
        assertNotNull(response.getData());
        assertFalse(response.getData().isEmpty());
    }

    @Test
    @Order(4)
    void whenGetAllAsApiResponseThenDataContainsGMCBus() {
        ApiResponse<BaseMetaData, Bus> response = underTest.getAllAsApiResponse();
        boolean found = response.getData().stream()
                .anyMatch(b -> "072414187".equals(b.getRouteNumber())
                        && "GMC".equals(b.getBrand()));
        assertTrue(found);
    }

    @Test
    @Order(5)
    void whenGetAllAsApiResponseThenDataContainsMercedesBenz() {
        ApiResponse<BaseMetaData, Bus> response = underTest.getAllAsApiResponse();
        boolean found = response.getData().stream()
                .anyMatch(b -> "107002406".equals(b.getRouteNumber())
                        && "Mercedes-Benz".equals(b.getBrand()));
        assertTrue(found);
    }

    @Test
    @Order(6)
    void whenGetAllAsApiResponseThenNoErrorMessage() {
        ApiResponse<BaseMetaData, Bus> response = underTest.getAllAsApiResponse();
        assertNull(response.getMeta().getErrorMessage());
    }

    // ─── getByIdAsApiResponse — перевіряю успіх ───────────────────────────────────────

    @Test
    @Order(7)
    void whenGetByIdWithGMCIdThenSuccessIsTrue() {
        ApiResponse<BaseMetaData, Bus> response = underTest.getByIdAsApiResponse(gmcBusId);
        assertTrue(response.getMeta().isSuccess());
    }

    @Test
    @Order(8)
    void whenGetByIdWithGMCIdThenCodeIs200() {
        ApiResponse<BaseMetaData, Bus> response = underTest.getByIdAsApiResponse(gmcBusId);
        assertEquals(200, response.getMeta().getCode());
    }

    @Test
    @Order(9)
    void whenGetByIdWithGMCIdThenDataHasExactlyOneBus() {
        ApiResponse<BaseMetaData, Bus> response = underTest.getByIdAsApiResponse(gmcBusId);
        assertEquals(1, response.getData().size());
    }

    @Test
    @Order(10)
    void whenGetByIdWithGMCIdThenBrandIsGMC() {
        ApiResponse<BaseMetaData, Bus> response = underTest.getByIdAsApiResponse(gmcBusId);
        assertEquals("GMC", response.getData().get(0).getBrand());
    }

    @Test
    @Order(11)
    void whenGetByIdWithGMCIdThenDestinationMatches() {
        ApiResponse<BaseMetaData, Bus> response = underTest.getByIdAsApiResponse(gmcBusId);
        assertEquals("217 Monterey Hill", response.getData().get(0).getDestination());
    }

    @Test
    @Order(12)
    void whenGetByIdWithDodgeIdThenBrandIsDodge() {
        ApiResponse<BaseMetaData, Bus> response = underTest.getByIdAsApiResponse(dodgeBusId);
        assertEquals("Dodge", response.getData().get(0).getBrand());
    }

    @Test
    @Order(13)
    void whenGetByIdWithDodgeIdThenRouteNumberMatches() {
        ApiResponse<BaseMetaData, Bus> response = underTest.getByIdAsApiResponse(dodgeBusId);
        assertEquals("221272361", response.getData().get(0).getRouteNumber());
    }

    @Test
    @Order(14)
    void whenGetByIdWithDodgeIdThenDestinationMatches() {
        ApiResponse<BaseMetaData, Bus> response = underTest.getByIdAsApiResponse(dodgeBusId);
        assertEquals("75960 Independence Center", response.getData().get(0).getDestination());
    }

    // ─── getByIdAsApiResponse — перевіряю невдачі ─────────────────────────────────────────

    @Test
    @Order(15)
    void whenGetByIdWithFakeIdThenSuccessIsFalse() {
        ApiResponse<BaseMetaData, Bus> response = underTest.getByIdAsApiResponse("000000000000000000000000");
        assertFalse(response.getMeta().isSuccess());
    }

    @Test
    @Order(16)
    void whenGetByIdWithFakeIdThenCodeIs404() {
        ApiResponse<BaseMetaData, Bus> response = underTest.getByIdAsApiResponse("000000000000000000000000");
        assertEquals(404, response.getMeta().getCode());
    }

    @Test
    @Order(17)
    void whenGetByIdWithFakeIdThenErrorMessageContainsId() {
        String fakeId = "000000000000000000000000";
        ApiResponse<BaseMetaData, Bus> response = underTest.getByIdAsApiResponse(fakeId);
        assertTrue(response.getMeta().getErrorMessage().contains(fakeId));
    }

    @Test
    @Order(18)
    void whenGetByIdWithFakeIdThenDataIsNullOrEmpty() {
        ApiResponse<BaseMetaData, Bus> response = underTest.getByIdAsApiResponse("000000000000000000000000");
        assertTrue(response.getData() == null || response.getData().isEmpty());
    }

    // ─── createAsApiResponse(Bus) ────────────────────────────────────────────────

    @Test
    @Order(19)
    void whenCreateBusAsApiResponseThenSuccessIsTrue() {
        Bus bus = new Bus("Land Rover", "011307077", "126 Towne Park");
        ApiResponse<BaseMetaData, Bus> response = underTest.createAsApiResponse(bus);
        assertTrue(response.getMeta().isSuccess());
        underTest.delById(response.getData().get(0).getId());
    }

    @Test
    @Order(20)
    void whenCreateBusAsApiResponseThenCodeIs201() {
        Bus bus = new Bus("Land Rover", "011307077", "126 Towne Park");
        ApiResponse<BaseMetaData, Bus> response = underTest.createAsApiResponse(bus);
        assertEquals(201, response.getMeta().getCode());
        underTest.delById(response.getData().get(0).getId());
    }

    @Test
    @Order(21)
    void whenCreateBusAsApiResponseThenReturnedBusHasId() {
        Bus bus = new Bus("Land Rover", "011307077", "126 Towne Park");
        ApiResponse<BaseMetaData, Bus> response = underTest.createAsApiResponse(bus);
        assertNotNull(response.getData().get(0).getId());
        underTest.delById(response.getData().get(0).getId());
    }

    @Test
    @Order(22)
    void whenCreateBusAsApiResponseThenBrandIsPersisted() {
        Bus bus = new Bus("Land Rover", "011307077", "126 Towne Park");
        ApiResponse<BaseMetaData, Bus> response = underTest.createAsApiResponse(bus);
        assertEquals("Land Rover", response.getData().get(0).getBrand());
        underTest.delById(response.getData().get(0).getId());
    }

    @Test
    @Order(23)
    void whenCreateBusAsApiResponseThenDestinationIsPersisted() {
        Bus bus = new Bus("Land Rover", "011307077", "126 Towne Park");
        ApiResponse<BaseMetaData, Bus> response = underTest.createAsApiResponse(bus);
        assertEquals("126 Towne Park", response.getData().get(0).getDestination());
        underTest.delById(response.getData().get(0).getId());
    }

    // ─── createAsApiResponse(BusCreateRequest) ───────────────────────────────────

    @Test
    @Order(24)
    void whenCreateFromRequestAsApiResponseThenSuccessIsTrue() {
        BusCreateRequest request = new BusCreateRequest("Mercedes-Benz", "107002406", "44967 Arizona Crossing");
        ApiResponse<BaseMetaData, Bus> response = underTest.createAsApiResponse(request);
        assertTrue(response.getMeta().isSuccess());
        underTest.delById(response.getData().get(0).getId());
    }

    @Test
    @Order(25)
    void whenCreateFromRequestAsApiResponseThenCodeIs201() {
        BusCreateRequest request = new BusCreateRequest("Mercedes-Benz", "107002406", "44967 Arizona Crossing");
        ApiResponse<BaseMetaData, Bus> response = underTest.createAsApiResponse(request);
        assertEquals(201, response.getMeta().getCode());
        underTest.delById(response.getData().get(0).getId());
    }

    @Test
    @Order(26)
    void whenCreateFromRequestAsApiResponseThenRouteNumberMatches() {
        BusCreateRequest request = new BusCreateRequest("Mercedes-Benz", "107002406", "44967 Arizona Crossing");
        ApiResponse<BaseMetaData, Bus> response = underTest.createAsApiResponse(request);
        assertEquals("107002406", response.getData().get(0).getRouteNumber());
        underTest.delById(response.getData().get(0).getId());
    }

    @Test
    @Order(27)
    void whenCreateFromRequestAsApiResponseThenDestinationMatches() {
        BusCreateRequest request = new BusCreateRequest("Mercedes-Benz", "107002406", "44967 Arizona Crossing");
        ApiResponse<BaseMetaData, Bus> response = underTest.createAsApiResponse(request);
        assertEquals("44967 Arizona Crossing", response.getData().get(0).getDestination());
        underTest.delById(response.getData().get(0).getId());
    }

    // ─── updateAsApiResponse(Bus) — перевіряю успіх ───────────────────────────────────

    @Test
    @Order(28)
    void whenUpdateBusAsApiResponseWithGMCBusThenSuccessIsTrue() {
        Bus toUpdate = underTest.getById(gmcBusId);
        toUpdate.setDestination("217 Monterey Hill Updated");
        ApiResponse<BaseMetaData, Bus> response = underTest.updateAsApiResponse(toUpdate);
        assertTrue(response.getMeta().isSuccess());
        // Restore
        toUpdate.setDestination("217 Monterey Hill");
        underTest.update(toUpdate);
    }

    @Test
    @Order(29)
    void whenUpdateBusAsApiResponseWithGMCBusThenCodeIs200() {
        Bus toUpdate = underTest.getById(gmcBusId);
        ApiResponse<BaseMetaData, Bus> response = underTest.updateAsApiResponse(toUpdate);
        assertEquals(200, response.getMeta().getCode());
    }

    @Test
    @Order(30)
    void whenUpdateBusAsApiResponseWithGMCBusThenUpdatedDestinationIsPersisted() {
        Bus toUpdate = underTest.getById(gmcBusId);
        toUpdate.setDestination("999 Updated Street");
        underTest.updateAsApiResponse(toUpdate);
        Bus fetched = underTest.getById(gmcBusId);
        assertEquals("999 Updated Street", fetched.getDestination());
        // Restore
        fetched.setDestination("217 Monterey Hill");
        underTest.update(fetched);
    }

    // ─── updateAsApiResponse(Bus) — перевіряю невдачі ─────────────────────────────────────

    @Test
    @Order(31)
    void whenUpdateBusAsApiResponseWithFakeIdThenSuccessIsFalse() {
        Bus ghost = Bus.builder()
                .id("000000000000000000000000")
                .brand("Ghost").routeNumber("999").destination("Nowhere")
                .build();
        ApiResponse<BaseMetaData, Bus> response = underTest.updateAsApiResponse(ghost);
        assertFalse(response.getMeta().isSuccess());
    }

    @Test
    @Order(32)
    void whenUpdateBusAsApiResponseWithFakeIdThenCodeIs404() {
        Bus ghost = Bus.builder()
                .id("000000000000000000000000")
                .brand("Ghost").routeNumber("999").destination("Nowhere")
                .build();
        ApiResponse<BaseMetaData, Bus> response = underTest.updateAsApiResponse(ghost);
        assertEquals(404, response.getMeta().getCode());
    }

    @Test
    @Order(33)
    void whenUpdateBusAsApiResponseWithFakeIdThenErrorMessageContainsId() {
        String fakeId = "000000000000000000000000";
        Bus ghost = Bus.builder()
                .id(fakeId)
                .brand("Ghost").routeNumber("999").destination("Nowhere")
                .build();
        ApiResponse<BaseMetaData, Bus> response = underTest.updateAsApiResponse(ghost);
        assertTrue(response.getMeta().getErrorMessage().contains(fakeId));
    }

    // ─── updateAsApiResponse(BusUpdateRequest) — успіх ──────────────────────

    @Test
    @Order(34)
    void whenUpdateFromRequestWithDodgeIdThenSuccessIsTrue() {
        BusUpdateRequest request = new BusUpdateRequest(
                dodgeBusId, "Dodge", "221272361", "75960 Independence Center Updated");
        ApiResponse<BaseMetaData, Bus> response = underTest.updateAsApiResponse(request);
        assertTrue(response.getMeta().isSuccess());
        // Restore
        underTest.updateAsApiResponse(new BusUpdateRequest(
                dodgeBusId, "Dodge", "221272361", "75960 Independence Center"));
    }

    @Test
    @Order(35)
    void whenUpdateFromRequestWithDodgeIdThenCodeIs200() {
        BusUpdateRequest request = new BusUpdateRequest(
                dodgeBusId, "Dodge", "221272361", "75960 Independence Center");
        ApiResponse<BaseMetaData, Bus> response = underTest.updateAsApiResponse(request);
        assertEquals(200, response.getMeta().getCode());
    }

    @Test
    @Order(36)
    void whenUpdateFromRequestWithDodgeIdThenUpdatedBrandIsPersisted() {
        BusUpdateRequest request = new BusUpdateRequest(
                dodgeBusId, "Dodge-EV", "221272361", "75960 Independence Center");
        underTest.updateAsApiResponse(request);
        Bus fetched = underTest.getById(dodgeBusId);
        assertEquals("Dodge-EV", fetched.getBrand());
        // Restore
        underTest.updateAsApiResponse(new BusUpdateRequest(
                dodgeBusId, "Dodge", "221272361", "75960 Independence Center"));
    }

    // ─── updateAsApiResponse(BusUpdateRequest) — невдачі ────────────────────────

    @Test
    @Order(37)
    void whenUpdateFromRequestWithFakeIdThenSuccessIsFalse() {
        BusUpdateRequest request = new BusUpdateRequest("000000000000000000000000", "X", "0", "Void");
        ApiResponse<BaseMetaData, Bus> response = underTest.updateAsApiResponse(request);
        assertFalse(response.getMeta().isSuccess());
    }

    @Test
    @Order(38)
    void whenUpdateFromRequestWithFakeIdThenCodeIs404() {
        BusUpdateRequest request = new BusUpdateRequest("000000000000000000000000", "X", "0", "Void");
        ApiResponse<BaseMetaData, Bus> response = underTest.updateAsApiResponse(request);
        assertEquals(404, response.getMeta().getCode());
    }

    @Test
    @Order(39)
    void whenUpdateFromRequestWithFakeIdThenErrorMessageContainsId() {
        String fakeId = "000000000000000000000000";
        BusUpdateRequest request = new BusUpdateRequest(fakeId, "X", "0", "Void");
        ApiResponse<BaseMetaData, Bus> response = underTest.updateAsApiResponse(request);
        assertTrue(response.getMeta().getErrorMessage().contains(fakeId));
    }
}