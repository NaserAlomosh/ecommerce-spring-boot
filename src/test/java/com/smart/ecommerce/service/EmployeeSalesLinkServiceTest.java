package com.smart.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.smart.ecommerce.dto.order.GuestSalesDtos.CreateSalesLinkRequest;
import com.smart.ecommerce.entity.EmployeeSalesLink;
import com.smart.ecommerce.entity.User;
import com.smart.ecommerce.enums.Role;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.repository.EmployeeSalesLinkRepository;
import com.smart.ecommerce.repository.OrderRepository;
import com.smart.ecommerce.repository.ProductRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class EmployeeSalesLinkServiceTest {
  @Mock EmployeeSalesLinkRepository links;
  @Mock CustomerContextService context;
  @Mock TokenHashService hashes;
  @Mock ProductService productService;
  @Mock ProductRepository products;
  @Mock OrderRepository orders;
  @Mock OrderNumberGenerator numbers;
  @Mock OrderCalculationService calculation;
  @Mock InventoryService inventory;
  @Mock OrderMapper mapper;
  EmployeeSalesLinkService service;

  @BeforeEach
  void setUp() {
    service = new EmployeeSalesLinkService(links, context, hashes, productService,
                                           products, orders, numbers, calculation,
                                           inventory, mapper);
  }

  @Test
  void createsOneTimePublicTokenForEmployee() {
    User employee = new User();
    employee.setRole(Role.SUB_ADMIN);
    when(context.currentCustomer()).thenReturn(employee);
    when(hashes.sha256(any())).thenReturn("a".repeat(64));
    when(links.save(any(EmployeeSalesLink.class)))
        .thenAnswer(invocation -> {
          EmployeeSalesLink link = invocation.getArgument(0);
          link.setId(11L);
          return link;
        });

    var result = service.create(new CreateSalesLinkRequest(null));

    assertThat(result.id()).isEqualTo(11L);
    assertThat(result.token()).isNotBlank();
    assertThat(result.publicPath()).contains(result.token()).endsWith("/products");
  }

  @Test
  void rejectsExpiredLinkBeforeShowingProducts() {
    when(hashes.sha256("expired-token")).thenReturn("b".repeat(64));
    EmployeeSalesLink link = new EmployeeSalesLink();
    link.setActive(true);
    link.setExpiresAt(Instant.now().minusSeconds(1));
    when(links.findByTokenHash("b".repeat(64))).thenReturn(Optional.of(link));

    assertThatThrownBy(() -> service.products("expired-token", null, null,
                                               Pageable.unpaged()))
        .isInstanceOf(ResourceNotFoundException.class);
  }
}
