package com.smart.ecommerce.report.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.smart.ecommerce.enums.InventoryMovementType;
import com.smart.ecommerce.report.dto.InventoryFilters.Period;
import com.smart.ecommerce.report.dto.InventoryReportDtos.MovementDirection;
import com.smart.ecommerce.report.dto.InventoryReportDtos.MovementRow;
import com.smart.ecommerce.report.repository.InventoryReportQueryRepository;
import com.smart.ecommerce.report.service.DateRangeResolver.ResolvedDateRange;
import com.smart.ecommerce.report.util.ReportPeriod;
import java.time.*;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

class InventoryReportServiceTest {
  @Test
  void statusAdjustmentRestorationCountsAsCancelledOrderStock() {
    var queries = mock(InventoryReportQueryRepository.class);
    var ranges = mock(DateRangeResolver.class);
    var pageable = PageRequest.of(0, 20);
    var now = Instant.parse("2026-07-25T12:00:00Z");
    var movement =
        new MovementRow(1L, 2L, "Product", "Category",
                        InventoryMovementType.ORDER_STATUS_ADJUSTMENT, 4, 3, 7,
                        MovementDirection.INCREASE, 5L, "ORD-5", 6L,
                        "Customer", 7L, "Admin", "Cancelled by admin", now);
    when(ranges.resolve(any(), isNull(), isNull()))
        .thenReturn(new ResolvedDateRange(
            LocalDate.of(2026, 7, 25), LocalDate.of(2026, 7, 25),
            now.minusSeconds(3600), now.plusSeconds(3600), ZoneOffset.UTC));
    when(queries.movements(anyString(), anyMap(), eq(pageable)))
        .thenReturn(new PageImpl<>(java.util.List.of(movement), pageable, 1));

    var report =
        new InventoryReportService(queries, ranges)
            .restorations(new Period(ReportPeriod.TODAY, null, null), null,
                          pageable);

    assertThat(report.totalRestorationMovements()).isEqualTo(1);
    assertThat(report.totalUnitsRestored()).isEqualTo(3);
    assertThat(report.cancelledOrderUnitsRestored()).isEqualTo(3);
    assertThat(report.returnedProductUnitsRestored()).isZero();
    assertThat(report.rows().content()).singleElement().satisfies(row -> {
      assertThat(row.movementType())
          .isEqualTo(InventoryMovementType.ORDER_STATUS_ADJUSTMENT);
      assertThat(row.quantityRestored()).isEqualTo(3);
    });
  }
}
