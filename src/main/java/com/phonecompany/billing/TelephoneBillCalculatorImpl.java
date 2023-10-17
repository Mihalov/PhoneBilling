package com.phonecompany.billing;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TelephoneBillCalculatorImpl implements TelephoneBillCalculator {
    LocalTime startRushTime = LocalTime.of(8, 0);   // 8:00 AM
    LocalTime endRushTime = LocalTime.of(16, 0);   // 4:00 PM
    // Create a map to store prices and their counts
    Map<String, List<BigDecimal>> priceMap = new HashMap<>();
  @Override
  public BigDecimal calculate(String phoneLog) {
    BigDecimal finalSum = BigDecimal.ZERO;
    String[] lines = phoneLog.split("\n");

    for (String line : lines) {
      String[] fields = line.split(",");
      if (fields.length != 3) {
        System.err.println("Invalid CSV format");
        continue;
      }
      String phoneNumber = fields[0];
      String startTimeStr = fields[1];
      String endTimeStr = fields[2];
      BigDecimal price = calculatePriceForLine(startTimeStr,endTimeStr);
      priceMap.computeIfAbsent(phoneNumber, k -> new ArrayList<>()).add(price);
    }
      // Find the number with the most entries
      String numberWithMostEntries = null;
      int maxEntryCount = 0;
      for (Map.Entry<String, List<BigDecimal>> entry : priceMap.entrySet()) {
          int entryCount = entry.getValue().size();
          if (entryCount > maxEntryCount) {
              maxEntryCount = entryCount;
              numberWithMostEntries = entry.getKey();
          } else if (entryCount == maxEntryCount && numberWithMostEntries != null
                  && Long.parseLong(numberWithMostEntries) < Long.parseLong(entry.getKey())) {
              numberWithMostEntries = entry.getKey();
          }
      }

      // Calculate the total sum for each number (excluding the one with the most entries)
      for (Map.Entry<String, List<BigDecimal>> entry : priceMap.entrySet()) {
          if (!entry.getKey().equals(numberWithMostEntries)) {
              BigDecimal sum = entry.getValue().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
              System.out.println("Number: " + entry.getKey() + ", Total Price: " + sum);
              finalSum = finalSum.add(sum);
          }
      }
    return finalSum;
  }

  public BigDecimal calculatePriceForLine(String startDate, String endDate) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
          LocalDateTime startTime = LocalDateTime.parse(startDate, formatter);
          LocalDateTime endTime = LocalDateTime.parse(endDate, formatter);

      long roundedMinutes = getRoundedMinutes(startTime, endTime);

      BigDecimal bill = calculatePrice(roundedMinutes, startTime , endTime);
          System.out.println("Call Start Time: " + startTime.format(formatter));
          System.out.println("Call End Time: " + endTime.format(formatter));
          System.out.println("Call Duration: " + roundedMinutes + " minutes");
          System.out.println("Your telephone bill is: $" + bill);

      return bill;
  }

    public static long getRoundedMinutes(LocalDateTime startTime, LocalDateTime endTime) {
        Duration duration = Duration.between(startTime, endTime);
        long seconds = duration.getSeconds();

        // Round up to the nearest minute for every started second
        return (seconds + 59) / 60;
    }

  public BigDecimal calculatePrice(long minutes, LocalDateTime startTime, LocalDateTime endTime) {
    BigDecimal bill;
    if (isTimeInRange(startTime.toLocalTime(), startRushTime, endRushTime)
        && isTimeInRange(endTime.toLocalTime(), startRushTime, endRushTime)) {
      bill = calculaterPrice(minutes, 1.0);
    } else if (!isTimeInRange(startTime.toLocalTime(), startRushTime, endRushTime)
        && !isTimeInRange(endTime.toLocalTime(), startRushTime, endRushTime)) {
      bill = BigDecimal.valueOf(minutes * 0.5);
    } else if (!isTimeInRange(startTime.toLocalTime(), startRushTime, endRushTime)
        && isTimeInRange(endTime.toLocalTime(), startRushTime, endRushTime)) {
      long beforeRush = getRoundedMinutes(startTime, LocalDateTime.of(startTime.toLocalDate(), startRushTime));
      long afterRush = getRoundedMinutes(LocalDateTime.of(startTime.toLocalDate(), startRushTime), endTime);
      if (minutes <= 5L) {
        bill = calculaterPrice(beforeRush, 0.5);
        bill = bill.add(calculaterPrice(afterRush, 1.0));
      } else {
        bill = calculaterPrice(beforeRush, 0.5);
        bill = bill.add(BigDecimal.valueOf(afterRush * 0.2));
      }
    } else {
      long rushDuration = getRoundedMinutes(startTime, LocalDateTime.of(startTime.toLocalDate(), endRushTime));
      long afterRush = getRoundedMinutes(LocalDateTime.of(startTime.toLocalDate(), endRushTime), endTime);
      if (minutes <= 5L) {
        bill = calculaterPrice(rushDuration, 1.0);
        bill = bill.add(calculaterPrice(afterRush, 0.5));
      } else {
        bill = calculaterPrice(rushDuration, 1.0);
        bill = bill.add(BigDecimal.valueOf(afterRush * 0.2));
      }
    }
    return bill;
  }

  public static boolean isTimeInRange(LocalTime time, LocalTime startTime, LocalTime endTime) {
        return !time.isBefore(startTime) && !time.isAfter(endTime);
  }

  public static BigDecimal calculaterPrice(Long minutes, Double basePrice) {
      if (minutes > 5) {
          return BigDecimal.valueOf((5 * basePrice) + ((minutes-5) * 0.2));
      } else {
          return BigDecimal.valueOf(minutes * basePrice);
      }
  }
}
