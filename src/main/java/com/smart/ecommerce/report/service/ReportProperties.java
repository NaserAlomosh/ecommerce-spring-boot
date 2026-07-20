package com.smart.ecommerce.report.service;
import java.math.BigDecimal;import org.springframework.boot.context.properties.ConfigurationProperties;
@ConfigurationProperties(prefix="app.reports")
public record ReportProperties(int maxPdfRows,int defaultExportRangeDays,int maxExportRangeDays,String companyName,String logoPath,String footerText,Pdf pdf,BigDecimal vipMinSpending,long vipMinOrders,long minProductReviews){
 public ReportProperties{ if(maxPdfRows<=0)maxPdfRows=5000; if(defaultExportRangeDays<=0)defaultExportRangeDays=31; if(maxExportRangeDays<=0)maxExportRangeDays=366; if(companyName==null||companyName.isBlank())companyName="Smart E-Commerce"; if(footerText==null)footerText="Confidential administrative report"; if(pdf==null)pdf=new Pdf("classpath:fonts/DejaVuSans.ttf.base64","classpath:fonts/DejaVuSans.ttf.base64","classpath:fonts/DejaVuSans.ttf.base64"); if(vipMinSpending==null)vipMinSpending=new BigDecimal("1000.00"); if(vipMinOrders<=0)vipMinOrders=10; if(minProductReviews<=0)minProductReviews=5; }
 public record Pdf(String regularFont,String boldFont,String arabicFont){}
}
