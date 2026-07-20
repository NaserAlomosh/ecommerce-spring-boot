package com.smart.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.smart.ecommerce.entity.*;
import com.smart.ecommerce.enums.InventoryMovementType;
import com.smart.ecommerce.repository.InventoryHistoryRepository;
import com.smart.ecommerce.repository.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

class InventoryServiceTest {
 private final ProductRepository products=mock(ProductRepository.class);
 private final InventoryHistoryRepository history=mock(InventoryHistoryRepository.class);
 private final CustomerContextService ctx=mock(CustomerContextService.class);
 private final InventoryMapper mapper=mock(InventoryMapper.class);
 private final InventoryService service=new InventoryService(products,history,ctx,mapper);
 @Test void productCreationCreatesInitialHistory() { Product p=product(20); User admin=user(1L,"Admin","User"); service.recordProductCreated(p,admin); ArgumentCaptor<InventoryHistory> c=ArgumentCaptor.forClass(InventoryHistory.class); verify(history).save(c.capture()); assertEquals(InventoryMovementType.PRODUCT_CREATED,c.getValue().getMovementType()); assertEquals(0,c.getValue().getQuantityBefore()); assertEquals(20,c.getValue().getQuantityChange()); assertEquals(20,c.getValue().getQuantityAfter()); assertSame(admin,c.getValue().getPerformedBy()); }
 @Test void adminAdjustmentUpdatesStockAndCreatesHistory() { Product p=product(17); User admin=user(1L,"Admin","User"); when(ctx.currentCustomer()).thenReturn(admin); when(products.lockWithImagesById(5L)).thenReturn(Optional.of(p)); service.recordAdminAdjustment(5L,10,"Manual stock correction"); assertEquals(27,p.getStockQuantity()); ArgumentCaptor<InventoryHistory> c=ArgumentCaptor.forClass(InventoryHistory.class); verify(history).save(c.capture()); assertEquals(InventoryMovementType.ADMIN_ADJUSTMENT,c.getValue().getMovementType()); assertEquals(17,c.getValue().getQuantityBefore()); assertEquals(10,c.getValue().getQuantityChange()); assertEquals(27,c.getValue().getQuantityAfter()); }
 @Test void rejectsNegativeResultingStock() { Product p=product(2); when(ctx.currentCustomer()).thenReturn(user(1L,"Admin","User")); when(products.lockWithImagesById(5L)).thenReturn(Optional.of(p)); assertThrows(IllegalArgumentException.class, () -> service.recordAdminAdjustment(5L,-3,"Damaged products removed")); assertEquals(2,p.getStockQuantity()); verify(history,never()).save(any()); }
 @Test void orderCreatedStoresOrderAndCustomerSnapshot() { Product p=product(20); User customer=user(7L,"Jane","Buyer"); Order o=new Order(); o.setOrderNumber("ORD-1"); o.setCustomer(customer); service.recordOrderCreated(p,3,o,customer); assertEquals(17,p.getStockQuantity()); ArgumentCaptor<InventoryHistory> c=ArgumentCaptor.forClass(InventoryHistory.class); verify(history).save(c.capture()); assertEquals(InventoryMovementType.ORDER_CREATED,c.getValue().getMovementType()); assertEquals(-3,c.getValue().getQuantityChange()); assertEquals("ORD-1",c.getValue().getOrderNumber()); assertEquals(7L,c.getValue().getCustomerId()); assertEquals("Jane Buyer",c.getValue().getCustomerNameSnapshot()); }
 @Test void searchDelegatesToRepositoryWithPageable() { when(history.search(any(),any(),any(),any(),any(),any(),any(Pageable.class))).thenReturn(Page.empty()); assertNotNull(service.search(null,null,null,null,null,null,Pageable.ofSize(10))); }
 private Product product(int stock){ Product p=new Product(); p.setStockQuantity(stock); p.setNameEn("Phone"); p.setNameAr("هاتف"); p.setSku("SKU"); return p; }
 private User user(Long id,String first,String last){ User u=new User(); u.setId(id); u.setFirstName(first); u.setLastName(last); return u; }
}
