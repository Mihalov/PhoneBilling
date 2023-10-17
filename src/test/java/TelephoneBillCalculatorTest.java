import com.phonecompany.billing.TelephoneBillCalculatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TelephoneBillCalculatorTest {
    private TelephoneBillCalculatorImpl calculator;

    @BeforeEach
    public void setup() {
        calculator = new TelephoneBillCalculatorImpl();
    }

    @Test
    public void testCalculate() {
        String testData = "420774577453,13-01-2020 18:10:15,13-01-2020 18:12:57\n"
                + "420776562353,18-01-2020 08:59:20,18-01-2020 09:10:00";
        BigDecimal result = calculator.calculate(testData);
        BigDecimal expectedBill = new BigDecimal("1.5");
        assertEquals(expectedBill, result);

        String testData2 = "420774577453,13-01-2020 18:10:15,13-01-2020 19:12:57\n"
                +"420776562353,13-01-2020 18:10:15,13-01-2020 18:19:57\n"
                + "420776562353,18-01-2020 08:59:20,18-01-2020 09:10:00";
        BigDecimal result2 = calculator.calculate(testData2);
        BigDecimal expectedBill2 = new BigDecimal("33.0");
        assertEquals(expectedBill2, result2);
    }

    @Test
    public void testCalculatePriceForLine() {
        String startDate = "01-01-2023 09:00:00";
        String endDate = "01-01-2023 09:30:00";
        BigDecimal expectedBill = new BigDecimal("10.0");
        BigDecimal result = calculator.calculatePriceForLine(startDate, endDate);
        assertEquals(expectedBill, result);
    }

    @Test
    public void testGetRoundedMinutes() {
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 8, 30);
        LocalDateTime endTime = LocalDateTime.of(2023, 1, 1, 9, 15, 01);
        long expectedMinutes = 46;
        long result = calculator.getRoundedMinutes(startTime, endTime);
        assertEquals(expectedMinutes, result);
    }

    @Test
    public void testCalculatePrice() {
        long minutes = 4; // Adjust this based on your test case
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 9, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 1, 1, 9, 4);
        BigDecimal expectedBill = new BigDecimal("4.0");
        BigDecimal result = calculator.calculatePrice(minutes, startTime, endTime);
        assertEquals(expectedBill, result);
    }

    @Test
    public void testCalculatePriceOutsideRush() {
        long minutes = 10; // Adjust this based on your test case
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 18, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 1, 1, 18, 10);
        BigDecimal expectedBill = new BigDecimal("5.0");
        BigDecimal result = calculator.calculatePrice(minutes, startTime, endTime);
        assertEquals(expectedBill, result);
    }

    @Test
    public void testCalculatePriceStartOutsideRush() {
        long minutes = 20; // Adjust this based on your test case
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 7, 50);
        LocalDateTime endTime = LocalDateTime.of(2023, 1, 1, 8, 10);
        BigDecimal expectedBill = new BigDecimal("5.5");
        BigDecimal result = calculator.calculatePrice(minutes, startTime, endTime);
        assertEquals(expectedBill, result);

        minutes = 2; // Adjust this based on your test case
        startTime = LocalDateTime.of(2023, 1, 1, 7, 59);
        endTime = LocalDateTime.of(2023, 1, 1, 8, 1);
        expectedBill = new BigDecimal("1.5");
        result = calculator.calculatePrice(minutes, startTime, endTime);
        assertEquals(expectedBill, result);
    }

    @Test
    public void testCalculatePriceStartInRush() {
        long minutes = 15; // Adjust this based on your test case
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 15, 55);
        LocalDateTime endTime = LocalDateTime.of(2023, 1, 1, 16, 10);
        BigDecimal expectedBill = new BigDecimal("7.0");
        BigDecimal result = calculator.calculatePrice(minutes, startTime, endTime);
        assertEquals(expectedBill, result);

        minutes = 4; // Adjust this based on your test case
        startTime = LocalDateTime.of(2023, 1, 1, 15, 57);
        endTime = LocalDateTime.of(2023, 1, 1, 16, 0, 1);
        expectedBill = new BigDecimal("3.5");
        result = calculator.calculatePrice(minutes, startTime, endTime);
        assertEquals(expectedBill, result);
    }

    @Test
    public void testIsTimeInRange() {
        LocalTime time = LocalTime.of(10, 0);
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(16, 0);
        assertTrue(calculator.isTimeInRange(time, startTime, endTime));
    }

    @Test
    public void testCalculaterPrice() {
        long minutes = 10;
        double basePrice = 1.0;
        BigDecimal expectedBill = new BigDecimal("6.0"); // Adjust this based on your logic
        BigDecimal result = calculator.calculaterPrice(minutes, basePrice);
        assertEquals(expectedBill, result);

        minutes = 5;
        expectedBill = new BigDecimal("5.0"); // Adjust this based on your logic
        result = calculator.calculaterPrice(minutes, basePrice);
        assertEquals(expectedBill, result);
    }

}
