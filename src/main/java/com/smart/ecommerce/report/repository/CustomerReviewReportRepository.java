package com.smart.ecommerce.report.repository;
import com.smart.ecommerce.entity.*;
import com.smart.ecommerce.enums.*;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
public interface CustomerReviewReportRepository
    extends JpaRepository<User, Long> {
  interface CustomerSummary {
    long getTotalCustomers();
    long getActiveCustomers();
    long getInactiveCustomers();
    long getCustomersWithOrders();
    long getNewToday();
    long getNewMonth();
    BigDecimal getRevenue();
    long getCompletedOrders();
  }
  @Query(
      "select count(u) totalCustomers,sum(case when " +
      "u.status=com.smart.ecommerce.enums.UserStatus.ACTIVE then 1 else 0 " +
      "end) activeCustomers,sum(case when " +
      "u.status<>com.smart.ecommerce.enums.UserStatus.ACTIVE then 1 else 0 " +
      "end) inactiveCustomers,count(distinct o.customer.id) " +
      "customersWithOrders,sum(case when u.createdAt>=:today then 1 else 0 " +
      "end) newToday,sum(case when u.createdAt>=:month then 1 else 0 end) " +
      "newMonth,(select coalesce(sum(co.totalAmount),0) from Order co where " +
      "co.status=com.smart.ecommerce.enums.OrderStatus.COMPLETED) " +
      "revenue,(select count(co) from Order co where " +
      "co.status=com.smart.ecommerce.enums.OrderStatus.COMPLETED) " +
      "completedOrders from User u left join Order o on o.customer=u where " +
      "u.role=com.smart.ecommerce.enums.Role.CUSTOMER")
  CustomerSummary
  customerSummary(@Param("today") Instant today, @Param("month") Instant month);
  interface TopCustomer {
    Long getCustomerId();
    String getFullName();
    long getCompletedOrders();
    BigDecimal getTotalSpent();
    long getTotalPurchasedItems();
    Instant getLastOrderDate();
  }
  @Query(
      "select u.id customerId,concat(u.firstName,' ',u.lastName) " +
      "fullName,count(distinct o.id) " +
      "completedOrders,coalesce(sum(o.totalAmount),0) " +
      "totalSpent,coalesce(sum(o.totalItems),0) " +
      "totalPurchasedItems,max(coalesce(o.completedAt,o.createdAt)) " +
      "lastOrderDate from Order o join o.customer u where " +
      "o.status=com.smart.ecommerce.enums.OrderStatus.COMPLETED and " +
      "coalesce(o.completedAt,o.createdAt)>=:from and " +
      "coalesce(o.completedAt,o.createdAt)<:to and (:name is null or " +
      "lower(concat(u.firstName,' ',u.lastName)) like " +
      "lower(concat('%',:name,'%'))) and (:active is null or (:active=true " +
      "and u.status=com.smart.ecommerce.enums.UserStatus.ACTIVE) or " +
      "(:active=false and " +
      "u.status<>com.smart.ecommerce.enums.UserStatus.ACTIVE)) group by " +
      "u.id,u.firstName,u.lastName")
  Page<TopCustomer>
  topCustomers(@Param("from") Instant from, @Param("to") Instant to,
               @Param("name") String name, @Param("active") Boolean active,
               Pageable p);
  interface HistSummary {
    Long getCustomerId();
    String getCustomerName();
    long getTotalOrders();
    long getCompletedOrders();
    long getCancelledOrders();
    BigDecimal getTotalSpent();
    Instant getFirstOrderDate();
    Instant getLastOrderDate();
  }
  @Query(
      "select u.id customerId,concat(u.firstName,' ',u.lastName) " +
      "customerName,count(o.id) totalOrders,sum(case when " +
      "o.status=com.smart.ecommerce.enums.OrderStatus.COMPLETED then 1 else " +
      "0 end) completedOrders,sum(case when " +
      "o.status=com.smart.ecommerce.enums.OrderStatus.CANCELLED then 1 else " +
      "0 end) cancelledOrders,coalesce(sum(case when " +
      "o.status=com.smart.ecommerce.enums.OrderStatus.COMPLETED then " +
      "o.totalAmount else 0 end),0) totalSpent,min(o.createdAt) " +
      "firstOrderDate,max(o.createdAt) lastOrderDate from User u left join " +
      "Order o on o.customer=u and o.createdAt>=:from and o.createdAt<:to " +
      "where u.id=:id and u.role=com.smart.ecommerce.enums.Role.CUSTOMER " +
      "group by u.id,u.firstName,u.lastName")
  HistSummary
  historySummary(@Param("id") Long id, @Param("from") Instant from,
                 @Param("to") Instant to);
  interface HistOrder {
    String getOrderNumber();
    OrderStatus getStatus();
    BigDecimal getTotalAmount();
    Instant getCreatedAt();
    Instant getCompletedAt();
    long getItemsCount();
  }
  @Query("select o.orderNumber orderNumber,o.status status,o.totalAmount " +
         "totalAmount,o.createdAt createdAt,o.completedAt " +
         "completedAt,count(i.id) itemsCount from Order o left join o.items " +
         "i where o.customer.id=:id and o.createdAt>=:from and " +
         "o.createdAt<:to group by " +
         "o.id,o.orderNumber,o.status,o.totalAmount,o.createdAt,o.completedAt")
  Page<HistOrder>
  historyOrders(@Param("id") Long id, @Param("from") Instant from,
                @Param("to") Instant to, Pageable p);
  interface ReviewSummary {
    long getTotalReviews();
    BigDecimal getAverageRating();
    long getProductsReviewed();
    long getCustomersReviewed();
    long getFiveStars();
    long getFourStars();
    long getThreeStars();
    long getTwoStars();
    long getOneStar();
  }
  @Query("select count(r) totalReviews,coalesce(avg(r.rating),0) " +
         "averageRating,count(distinct r.product.id) " +
         "productsReviewed,count(distinct r.customer.id) " +
         "customersReviewed,sum(case when r.rating=5 then 1 else 0 end) " +
         "fiveStars,sum(case when r.rating=4 then 1 else 0 end) " +
         "fourStars,sum(case when r.rating=3 then 1 else 0 end) " +
         "threeStars,sum(case when r.rating=2 then 1 else 0 end) " +
         "twoStars,sum(case when r.rating=1 then 1 else 0 end) oneStar from " +
         "Review r where (:rating is null or r.rating=:rating)")
  ReviewSummary
  reviewSummary(@Param("rating") Integer rating);
  interface RatedProduct {
    Long getProductId();
    String getProductName();
    BigDecimal getAverageRating();
    long getTotalReviews();
    int getCurrentStock();
  }
  @Query(
      "select p.id productId,p.nameEn productName,coalesce(avg(r.rating),0) " +
      "averageRating,count(r.id) totalReviews,p.stockQuantity currentStock " +
      "from Review r join r.product p where r.createdAt>=:from and " +
      "r.createdAt<:to and (:name is null or lower(p.nameEn) like " +
      "lower(concat('%',:name,'%')) or lower(p.nameAr) like " +
      "lower(concat('%',:name,'%'))) group by p.id,p.nameEn,p.stockQuantity " +
      "having count(r.id)>=:minReviews")
  Page<RatedProduct>
  ratedProducts(@Param("from") Instant from, @Param("to") Instant to,
                @Param("name") String name,
                @Param("minReviews") long minReviews, Pageable p);
  @Query("select p.id productId,p.nameEn productName,p.averageRating " +
         "averageRating,p.reviewsCount totalReviews,p.stockQuantity " +
         "currentStock from Product p where p.reviewsCount>0 and (:name is " +
         "null or lower(p.nameEn) like lower(concat('%',:name,'%')) or " +
         "lower(p.nameAr) like lower(concat('%',:name,'%')))")
  Page<RatedProduct>
  mostReviewed(@Param("name") String name, Pageable p);
  interface NoReview {
    Long getProductId();
    String getProductName();
    int getStock();
    Instant getCreatedAt();
  }
  @Query("select p.id productId,p.nameEn productName,p.stockQuantity " +
         "stock,p.createdAt createdAt from Product p where p.deleted=false " +
         "and p.reviewsCount=0 and (:categoryId is null or " +
         "p.category.id=:categoryId) and (:name is null or lower(p.nameEn) " +
         "like lower(concat('%',:name,'%')) or lower(p.nameAr) like " +
         "lower(concat('%',:name,'%')))")
  Page<NoReview>
  noReviews(@Param("categoryId") Long categoryId, @Param("name") String name,
            Pageable p);
}
