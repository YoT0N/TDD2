package edu.se.extweb;

import edu.se.extweb.model.Bus;
import edu.se.extweb.request.BusPageRequest;
import edu.se.extweb.response.ApiResponse;
import edu.se.extweb.response.PaginationMetaData;
import edu.se.extweb.service.BusService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Пагінаційні тести для BusService.
 *
 * Корисна математика для перевірки:
 *   size=5  → totalPages=6  (30/5=6),    остання сторінка: index=5, size=5
 *   size=7  → totalPages=5  (30/7≈4.28), остання сторінка: index=4, size=2
 *   size=10 → totalPages=3  (30/10=3),   остання сторінка: index=2, size=10
 *   size=11 → totalPages=3  (30/11≈2.72),остання сторінка: index=2, size=8
 *   size=30 → totalPages=1,              єдина сторінка:   index=0, size=30
 */
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BusServicePagingTest {

    @Autowired
    private BusService underTest;

    private static final int TOTAL = 30;

    @BeforeAll
    void verify() {
        // Переконуємось що в базі справді 30 записів перед стартом тестів
        int size = underTest.getAll().size();
        if (size != TOTAL) {
            throw new IllegalStateException(
                    "Expected " + TOTAL + " buses in DB, but found " + size +
                            ". Please import MOCK_DATA into the test database.");
        }
    }

    // ─── Базовий happy path: page=0, size=5 ─────────────────────────────────

    @Test
    @Order(1)
    void whenPage0Size5_ThenSuccessIsTrue() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, 5));
        assertTrue(response.getMeta().isSuccess());
    }

    @Test
    @Order(2)
    void whenPage0Size5_ThenCodeIs200() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, 5));
        assertEquals(200, response.getMeta().getCode());
    }

    @Test
    @Order(3)
    void whenPage0Size5_ThenDataSizeIs5() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, 5));
        assertEquals(5, response.getData().size());
    }

    @Test
    @Order(4)
    void whenPage0Size5_ThenPageNumberIs0() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, 5));
        assertEquals(0, response.getMeta().getNumber());
    }

    @Test
    @Order(5)
    void whenPage0Size5_ThenTotalElementsIs30() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, 5));
        assertEquals(TOTAL, response.getMeta().getTotalElements());
    }

    @Test
    @Order(6)
    void whenPage0Size5_ThenTotalPagesIs6() {
        // 30 / 5 = 6 рівно
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, 5));
        assertEquals(6, response.getMeta().getTotalPages());
    }

    @Test
    @Order(7)
    void whenPage0Size5_ThenIsFirstIsTrue() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, 5));
        assertTrue(response.getMeta().isFirst());
    }

    @Test
    @Order(8)
    void whenPage0Size5_ThenIsLastIsFalse() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, 5));
        assertFalse(response.getMeta().isLast());
    }

    @Test
    @Order(9)
    void whenPage0Size5_ThenNoErrorMessage() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, 5));
        assertNull(response.getMeta().getErrorMessage());
    }

    @Test
    @Order(10)
    void whenPage0Size5_ThenDataIsNotNull() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, 5));
        assertNotNull(response.getData());
    }

    // ─── Остання сторінка: page=5, size=5 → рівно 5 елементів ───────────────

    @Test
    @Order(11)
    void whenPage5Size5_ThenIsLastIsTrue() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(5, 5));
        assertTrue(response.getMeta().isLast());
    }

    @Test
    @Order(12)
    void whenPage5Size5_ThenIsFirstIsFalse() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(5, 5));
        assertFalse(response.getMeta().isFirst());
    }

    @Test
    @Order(13)
    void whenPage5Size5_ThenDataSizeIs5() {
        // 30 ділиться на 5 рівно, тому остання сторінка теж повна
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(5, 5));
        assertEquals(5, response.getData().size());
    }

    @Test
    @Order(14)
    void whenPage5Size5_ThenPageNumberIs5() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(5, 5));
        assertEquals(5, response.getMeta().getNumber());
    }

    // ─── size=7: page=4 — остання сторінка з 2 елементами ──────────────────

    @Test
    @Order(15)
    void whenPage4Size7_ThenIsLastIsTrue() {
        // 30 / 7 = 4 повні + 2 залишок → 5 сторінок (0..4), остання index=4
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(4, 7));
        assertTrue(response.getMeta().isLast());
    }

    @Test
    @Order(16)
    void whenPage4Size7_ThenDataSizeIs2() {
        // 30 - (4 * 7) = 2 записи на останній сторінці
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(4, 7));
        assertEquals(2, response.getData().size());
    }

    @Test
    @Order(17)
    void whenPage4Size7_ThenTotalPagesIs5() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(4, 7));
        assertEquals(5, response.getMeta().getTotalPages());
    }

    @Test
    @Order(18)
    void whenPage4Size7_ThenTotalElementsIs30() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(4, 7));
        assertEquals(TOTAL, response.getMeta().getTotalElements());
    }

    @Test
    @Order(19)
    void whenPage4Size7_ThenSuccessIsTrue() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(4, 7));
        assertTrue(response.getMeta().isSuccess());
    }

    // ─── size=10: рівний поділ ────────────────────────────────────────────────

    @Test
    @Order(20)
    void whenPage0Size10_ThenTotalPagesIs3() {
        // 30 / 10 = 3
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, 10));
        assertEquals(3, response.getMeta().getTotalPages());
    }

    @Test
    @Order(21)
    void whenPage0Size10_ThenDataSizeIs10() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, 10));
        assertEquals(10, response.getData().size());
    }

    @Test
    @Order(22)
    void whenPage2Size10_ThenIsLastIsTrue() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(2, 10));
        assertTrue(response.getMeta().isLast());
    }

    @Test
    @Order(23)
    void whenPage2Size10_ThenDataSizeIs10() {
        // 30 - (2 * 10) = 10 рівно
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(2, 10));
        assertEquals(10, response.getData().size());
    }

    // ─── Середня сторінка: не перша і не остання ────────────────────────────

    @Test
    @Order(24)
    void whenPage2Size5_ThenIsFirstAndIsLastAreFalse() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(2, 5));
        assertFalse(response.getMeta().isFirst());
        assertFalse(response.getMeta().isLast());
    }

    @Test
    @Order(25)
    void whenPage2Size5_ThenDataSizeIs5() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(2, 5));
        assertEquals(5, response.getData().size());
    }

    @Test
    @Order(26)
    void whenPage2Size5_ThenPageNumberIs2() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(2, 5));
        assertEquals(2, response.getMeta().getNumber());
    }

    // ─── Вся колекція на одній сторінці ─────────────────────────────────────

    @Test
    @Order(27)
    void whenPage0SizeAll_ThenIsFirstAndIsLastAreTrue() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, TOTAL));
        assertTrue(response.getMeta().isFirst());
        assertTrue(response.getMeta().isLast());
    }

    @Test
    @Order(28)
    void whenPage0SizeAll_ThenDataSizeIs30() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, TOTAL));
        assertEquals(TOTAL, response.getData().size());
    }

    @Test
    @Order(29)
    void whenPage0SizeAll_ThenTotalPagesIs1() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, TOTAL));
        assertEquals(1, response.getMeta().getTotalPages());
    }

    @Test
    @Order(30)
    void whenPage0SizeAll_ThenPageNumberIs0() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, TOTAL));
        assertEquals(0, response.getMeta().getNumber());
    }

    // ─── size=11: нерівний поділ ─────────────────────────────────────────────

    @Test
    @Order(31)
    void whenPage0Size11_ThenTotalPagesIs3() {
        // 30 / 11 = 2.72 → 3 сторінки
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, 11));
        assertEquals(3, response.getMeta().getTotalPages());
    }

    @Test
    @Order(32)
    void whenPage2Size11_ThenIsLastIsTrue() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(2, 11));
        assertTrue(response.getMeta().isLast());
    }

    @Test
    @Order(33)
    void whenPage2Size11_ThenDataSizeIs8() {
        // 30 - (2 * 11) = 8 записів на останній сторінці
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(2, 11));
        assertEquals(8, response.getData().size());
    }

    // ─── Перевірка конкретних записів із MOCK_DATA ───────────────────────────
    // Сортування за id ASC (як у getBusesPage), тому порядок залежить від MongoDB ObjectId.
    // Перевіряємо наявність відомих брендів у всій вибірці сторінки, а не за позицією.

    @Test
    @Order(34)
    void whenGetAllPages_ThenGMCIsPresentSomewhere() {
        // Шукаємо GMC, перебираючи всі сторінки (size=30 → 1 сторінка)
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, TOTAL));
        boolean found = response.getData().stream()
                .anyMatch(b -> "072414187".equals(b.getRouteNumber()) && "GMC".equals(b.getBrand()));
        assertTrue(found);
    }

    @Test
    @Order(35)
    void whenGetAllPages_ThenDodgeIsPresentSomewhere() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, TOTAL));
        boolean found = response.getData().stream()
                .anyMatch(b -> "221272361".equals(b.getRouteNumber()) && "Dodge".equals(b.getBrand()));
        assertTrue(found);
    }

    @Test
    @Order(36)
    void whenGetAllPages_ThenMercedesBenzIsPresentSomewhere() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, TOTAL));
        boolean found = response.getData().stream()
                .anyMatch(b -> "107002406".equals(b.getRouteNumber()) && "Mercedes-Benz".equals(b.getBrand()));
        assertTrue(found);
    }

    @Test
    @Order(37)
    void whenGetAllPages_ThenDataContainsExactly6FordBuses() {
        // MOCK_DATA: Ford з'являється 6 разів (routeNumber: 113124637, 101005360, 043304239, 122241941, 081220537, 041001246)
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, TOTAL));
        long fordCount = response.getData().stream()
                .filter(b -> "Ford".equals(b.getBrand()))
                .count();
        assertEquals(6, fordCount);
    }

    @Test
    @Order(38)
    void whenGetAllPages_ThenDataContainsExactly3ChevroletBuses() {
        // MOCK_DATA: Chevrolet → routeNumber: 084104621, 101206279, 026013165
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, TOTAL));
        long count = response.getData().stream()
                .filter(b -> "Chevrolet".equals(b.getBrand()))
                .count();
        assertEquals(3, count);
    }

    @Test
    @Order(39)
    void whenGetAllPages_ThenDataContainsExactly2MazdaBuses() {
        // MOCK_DATA: Mazda → routeNumber: 322079133, 121182014
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, TOTAL));
        long count = response.getData().stream()
                .filter(b -> "Mazda".equals(b.getBrand()))
                .count();
        assertEquals(2, count);
    }

    @Test
    @Order(40)
    void whenGetAllPages_ThenDataContainsExactly2DodgeBuses() {
        // MOCK_DATA: Dodge → routeNumber: 221272361, 055000770
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, TOTAL));
        long count = response.getData().stream()
                .filter(b -> "Dodge".equals(b.getBrand()))
                .count();
        assertEquals(2, count);
    }

    @Test
    @Order(41)
    void whenGetAllPages_ThenDataContainsExactly2LandRoverBuses() {
        // MOCK_DATA: Land Rover → routeNumber: 011307077, 084101051
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, TOTAL));
        long count = response.getData().stream()
                .filter(b -> "Land Rover".equals(b.getBrand()))
                .count();
        assertEquals(2, count);
    }

    // ─── Індекс поза діапазоном ──────────────────────────────────────────────

    @Test
    @Order(42)
    void whenPageOutOfRange_ThenSuccessIsFalse() {
        // size=5 → 6 сторінок (0..5), тому page=6 виходить за діапазон
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(6, 5));
        assertFalse(response.getMeta().isSuccess());
    }

    @Test
    @Order(43)
    void whenPageOutOfRange_ThenCodeIs400() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(6, 5));
        assertEquals(400, response.getMeta().getCode());
    }

    @Test
    @Order(44)
    void whenPageOutOfRange_ThenErrorMessageContainsPageNumber() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(6, 5));
        assertNotNull(response.getMeta().getErrorMessage());
        assertTrue(response.getMeta().getErrorMessage().contains("6"));
    }

    @Test
    @Order(45)
    void whenPageOutOfRange_ThenDataIsNullOrEmpty() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(6, 5));
        assertTrue(response.getData() == null || response.getData().isEmpty());
    }

    @Test
    @Order(46)
    void whenPageFarOutOfRange_ThenSuccessIsFalse() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(999, 5));
        assertFalse(response.getMeta().isSuccess());
    }

    // ─── Метадані відповіді не null ──────────────────────────────────────────

    @Test
    @Order(47)
    void whenAnyValidRequest_ThenResponseNotNull() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, 5));
        assertNotNull(response);
    }

    @Test
    @Order(48)
    void whenAnyValidRequest_ThenMetaNotNull() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(1, 5));
        assertNotNull(response.getMeta());
    }

    @Test
    @Order(49)
    void whenAnyValidRequest_ThenSizeInMetaMatchesRequest() {
        ApiResponse<PaginationMetaData, Bus> response = underTest.getBusesPage(new BusPageRequest(0, 7));
        assertEquals(7, response.getMeta().getSize());
    }

    @Test
    @Order(50)
    void whenAnyValidRequest_ThenTotalElementsAlwaysIs30() {
        // Незалежно від параметрів сторінки — загальна кількість фіксована
        ApiResponse<PaginationMetaData, Bus> response1 = underTest.getBusesPage(new BusPageRequest(0, 3));
        ApiResponse<PaginationMetaData, Bus> response2 = underTest.getBusesPage(new BusPageRequest(1, 10));
        assertEquals(TOTAL, response1.getMeta().getTotalElements());
        assertEquals(TOTAL, response2.getMeta().getTotalElements());
    }
}