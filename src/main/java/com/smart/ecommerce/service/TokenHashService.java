package com.smart.ecommerce.service;
import java.nio.charset.StandardCharsets;import java.security.*;import java.util.HexFormat;import org.springframework.stereotype.Service;
@Service public class TokenHashService{ public String sha256(String v){try{ return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(v.getBytes(StandardCharsets.UTF_8)));}catch(Exception e){throw new IllegalStateException(e);}} }
