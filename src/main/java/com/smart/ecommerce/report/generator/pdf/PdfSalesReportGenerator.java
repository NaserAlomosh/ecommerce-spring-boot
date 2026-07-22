package com.smart.ecommerce.report.generator.pdf;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.smart.ecommerce.dto.PaginationResponse;
import com.smart.ecommerce.report.dto.ReportDtos.*;
import com.smart.ecommerce.report.dto.CustomerReviewReportDtos.*;
import com.smart.ecommerce.report.service.ReportProperties;
import com.smart.ecommerce.util.MessageUtil;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import org.springframework.core.io.*;
import org.springframework.stereotype.Component;
@Component
public class PdfSalesReportGenerator {
  private final ReportProperties props;
  private final MessageUtil msg;
  private final ResourceLoader loader;
  public PdfSalesReportGenerator(ReportProperties props, MessageUtil msg,
                                 ResourceLoader loader) {
    this.props = props;
    this.msg = msg;
    this.loader = loader;
  }
  public byte[] sales(SalesSummaryResponse s,
                      PaginationResponse<SalesOrderRow> orders, Locale locale,
                      String by, String zone) {
    return doc(
        msg.getMessage("report.pdf.sales.title"), locale, zone, by,
        ()
            -> {},
        new String[] {"Order #", "Customer", "Completed", "Items", "Qty",
                      "Total", "Currency"},
        orders.content()
            .stream()
            .map(o
                 -> new String[] {o.orderNumber(), o.customerName(),
                                  String.valueOf(o.completedAt()),
                                  String.valueOf(o.itemsCount()),
                                  String.valueOf(o.totalQuantity()),
                                  o.totalAmount().toString(), o.currency()})
            .toList(),
        List.of("Revenue: " + s.totalRevenue(),
                "Completed orders: " + s.completedOrders(),
                "Items sold: " + s.totalItemsSold(),
                "Average order: " + s.averageOrderValue(),
                "Unique customers: " + s.uniqueCustomers(),
                "High/Low: " + s.highestOrderValue() + " / " +
                    s.lowestOrderValue()));
  }
  public byte[] topProducts(List<TopProductResponse> rows, Locale l, String by,
                            String z) {
    List<String[]> data = new ArrayList<>();
    int i = 1;
    for (var r : rows)
      data.add(new String[] {
          String.valueOf(i++), r.productName(), r.categoryName(),
          "" + r.quantitySold(), "" + r.ordersCount(), r.revenue().toString(),
          r.averageSellingPrice().toString(), "" + r.currentStock(),
          r.averageRating().toString()});
    return doc(msg.getMessage("report.pdf.top_products.title"), l, z, by,
               ()
                   -> {},
               new String[] {"#", "Product", "Category", "Qty", "Orders",
                             "Revenue", "Avg Price", "Stock", "Rating"},
               data, List.of());
  }
  public byte[] categories(PaginationResponse<CategorySalesResponse> rows,
                           Locale l, String by, String z) {
    return doc(
        msg.getMessage("report.pdf.categories.title"), l, z, by,
        ()
            -> {},
        new String[] {"Category", "Products", "Qty", "Orders", "Revenue",
                      "Share"},
        rows.content()
            .stream()
            .map(r
                 -> new String[] {
                     r.categoryName(), "" + r.productsSold(),
                     "" + r.quantitySold(), "" + r.completedOrders(),
                     r.revenue().toString(), r.revenueSharePercentage() + "%"})
            .toList(),
        List.of());
  }
  public byte[] customers(PaginationResponse<CustomerSalesResponse> rows,
                          Locale l, String by, String z) {
    return doc(msg.getMessage("report.pdf.customers.title"), l, z, by,
               ()
                   -> {},
               new String[] {"Customer", "Orders", "Items", "Spent",
                             "Avg Order", "First", "Last"},
               rows.content()
                   .stream()
                   .map(r
                        -> new String[] {
                            r.customerName(), "" + r.completedOrders(),
                            "" + r.itemsPurchased(), r.totalSpent().toString(),
                            r.averageOrderValue().toString(),
                            String.valueOf(r.firstCompletedOrderAt()),
                            String.valueOf(r.lastCompletedOrderAt())})
                   .toList(),
               List.of());
  }

  public byte[] customerSummary(CustomerSummaryResponse s, Locale l, String by,
                                String z) {
    return doc(
        msg.getMessage("report.pdf.customer_summary.title"), l, z, by,
        ()
            -> {},
        new String[] {"Metric", "Value"},
        List.of(new String[] {"Total Customers", "" + s.totalCustomers()},
                new String[] {"Active Customers", "" + s.activeCustomers()},
                new String[] {"Inactive Customers", "" + s.inactiveCustomers()},
                new String[] {"Customers With Orders",
                              "" + s.customersWithOrders()},
                new String[] {"Customers Without Orders",
                              "" + s.customersWithoutOrders()},
                new String[] {"Revenue",
                              s.totalRevenueGeneratedByCustomers().toString()}),
        List.of());
  }
  public byte[] topCustomers(List<TopCustomerResponse> rows, Locale l,
                             String by, String z) {
    return doc(msg.getMessage("report.pdf.top_customers.title"), l, z, by,
               ()
                   -> {},
               new String[] {"Customer", "Orders", "Spent", "Avg Order",
                             "Items", "Last"},
               rows.stream()
                   .map(r
                        -> new String[] {r.fullName(), "" + r.completedOrders(),
                                         r.totalSpent().toString(),
                                         r.averageOrderValue().toString(),
                                         "" + r.totalPurchasedItems(),
                                         String.valueOf(r.lastOrderDate())})
                   .toList(),
               List.of());
  }
  public byte[] customerHistory(CustomerPurchaseHistoryResponse h, Locale l,
                                String by, String z) {
    return doc(
        msg.getMessage("report.pdf.customer_history.title"), l, z, by,
        ()
            -> {},
        new String[] {"Order #", "Status", "Total", "Created", "Completed",
                      "Items"},
        h.orders()
            .content()
            .stream()
            .map(o
                 -> new String[] {
                     o.orderNumber(), String.valueOf(o.status()),
                     o.totalAmount().toString(), String.valueOf(o.createdAt()),
                     String.valueOf(o.completedAt()), "" + o.itemsCount()})
            .toList(),
        List.of("Customer: " + h.summary().customerName(),
                "Total orders: " + h.summary().totalOrders(),
                "Spent: " + h.summary().totalSpent()));
  }
  public byte[] reviewsSummary(ReviewsSummaryResponse s, Locale l, String by,
                               String z) {
    return doc(
        msg.getMessage("report.pdf.reviews_summary.title"), l, z, by,
        ()
            -> {},
        new String[] {"Metric", "Value"},
        List.of(
            new String[] {"Total Reviews", "" + s.totalReviews()},
            new String[] {"Average Rating", s.averageRating().toString()},
            new String[] {"Products Reviewed", "" + s.totalProductsReviewed()},
            new String[] {"Customers Reviewed",
                          "" + s.totalCustomersReviewed()},
            new String[] {"Five Stars",
                          "" + s.ratingDistribution().fiveStars()}),
        List.of());
  }
  public byte[] ratedProducts(String title, List<RatedProductResponse> rows,
                              Locale l, String by, String z) {
    return doc(
        title, l, z, by,
        ()
            -> {},
        new String[] {"Product", "Rating", "Reviews", "Stock"},
        rows.stream()
            .map(r
                 -> new String[] {r.productName(), r.averageRating().toString(),
                                  "" + r.totalReviews(), "" + r.currentStock()})
            .toList(),
        List.of());
  }
  public byte[]
  mostReviewed(com.smart.ecommerce.dto
                   .PaginationResponse<MostReviewedProductResponse> rows,
               Locale l, String by, String z) {
    return doc(msg.getMessage("report.pdf.most_reviewed.title"), l, z, by,
               ()
                   -> {},
               new String[] {"Product", "Reviews", "Rating"},
               rows.content()
                   .stream()
                   .map(r
                        -> new String[] {r.productName(), "" + r.reviewsCount(),
                                         r.averageRating().toString()})
                   .toList(),
               List.of());
  }
  public byte[] noReviews(
      com.smart.ecommerce.dto.PaginationResponse<ProductNoReviewsResponse> rows,
      Locale l, String by, String z) {
    return doc(msg.getMessage("report.pdf.no_reviews.title"), l, z, by,
               ()
                   -> {},
               new String[] {"Product", "Stock", "Created"},
               rows.content()
                   .stream()
                   .map(r
                        -> new String[] {r.productName(), "" + r.stock(),
                                         String.valueOf(r.createdAt())})
                   .toList(),
               List.of());
  }
  private byte[] doc(String title, Locale locale, String zone, String by,
                     Runnable noop, String[] headers, List<String[]> rows,
                     List<String> summary) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      Document d = new Document(PageSize.A4.rotate(), 36, 36, 54, 54);
      PdfWriter w = PdfWriter.getInstance(d, out);
      w.setPageEvent(new Footer(font(false), props.footerText()));
      d.open();
      Font normal = font(false), bold = font(true);
      Paragraph h = new Paragraph(props.companyName() + "\n" + title, bold);
      h.setAlignment(isAr(locale) ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT);
      d.add(h);
      d.add(new Paragraph("Generated by: " + by + "   Generated at: " +
                              ZonedDateTime.now(ZoneId.of(zone)) +
                              "   Timezone: " + zone,
                          normal));
      for (String s : summary)
        d.add(new Paragraph(s, normal));
      d.add(Chunk.NEWLINE);
      PdfPTable table = new PdfPTable(headers.length);
      table.setWidthPercentage(100);
      table.setRunDirection(isAr(locale) ? PdfWriter.RUN_DIRECTION_RTL
                                         : PdfWriter.RUN_DIRECTION_LTR);
      for (String x : headers) {
        PdfPCell c = new PdfPCell(new Phrase(label(x), bold));
        c.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
        table.addCell(c);
      }
      table.setHeaderRows(1);
      for (String[] r : rows)
        for (String x : r)
          table.addCell(new Phrase(x == null ? "" : x, normal));
      d.add(table);
      d.close();
      return out.toByteArray();
    } catch (Exception e) {
      throw new IllegalStateException("report.error.pdf_generation_failed", e);
    }
  }
  private String label(String x) {
    return msg.getMessage("report.pdf.label." + x.toLowerCase()
                                                    .replace(" #", "_number")
                                                    .replace(' ', '_')
                                                    .replace("/", "_"));
  }
  private boolean isAr(Locale l) {
    return l != null && "ar".equalsIgnoreCase(l.getLanguage());
  }
  private Font font(boolean bold) throws Exception {
    String p = bold ? props.pdf().boldFont() : props.pdf().regularFont();
    Resource r = loader.getResource(p);
    if (!r.exists())
      throw new IllegalStateException("report.error.pdf_font_invalid");
    BaseFont bf;
    if (p.endsWith(".base64")) {
      byte[] bytes = Base64.getMimeDecoder().decode(new String(
          r.getInputStream().readAllBytes(), StandardCharsets.US_ASCII));
      bf = BaseFont.createFont("embedded-report-font.ttf", BaseFont.IDENTITY_H,
                               BaseFont.EMBEDDED, false, bytes, null);
    } else {
      bf = BaseFont.createFont(r.getURL().toString(), BaseFont.IDENTITY_H,
                               BaseFont.EMBEDDED);
    }
    return new Font(bf, bold ? 11 : 9, bold ? Font.BOLD : Font.NORMAL);
  }
  static class Footer extends PdfPageEventHelper {
    Font f;
    String text;
    Footer(Font f, String t) {
      this.f = f;
      this.text = t;
    }
    public void onEndPage(PdfWriter w, Document d) {
      ColumnText.showTextAligned(
          w.getDirectContent(), Element.ALIGN_CENTER,
          new Phrase(text + " - Page " + w.getPageNumber(), f),
          (d.right() + d.left()) / 2, d.bottom() - 20, 0);
    }
  }
}
