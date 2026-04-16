package edu.se.extweb;

import edu.se.extweb.service.BusService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BusServiceTest {

    @Autowired
    private BusService underTest;

    @Test
    void whenGetAllBusesListThenSizeIs30() {
        int size = underTest.getAll().size();
        assertEquals(30, size);
    }
}