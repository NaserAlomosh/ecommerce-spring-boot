package com.smart.ecommerce.service;

import com.smart.ecommerce.dto.guest.GuestOrderDtos.*;
import com.smart.ecommerce.entity.GuestOrderLink;
import com.smart.ecommerce.exception.ResourceNotFoundException;
import com.smart.ecommerce.repository.GuestOrderLinkRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GuestOrderLinkService {
  private final GuestOrderLinkRepository links;

  @Transactional(readOnly = true)
  public List<GuestLinkResponse> list() {
    return links.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
        .stream().map(this::response).toList();
  }

  @Transactional(readOnly = true)
  public GuestLinkResponse getActive(String slug) {
    return response(requireActive(slug));
  }

  @Transactional
  public GuestLinkResponse create(GuestLinkRequest request) {
    String slug = normalize(request.slug());
    if (links.existsBySlug(slug))
      throw new IllegalArgumentException("guest_link.error.slug_exists");
    GuestOrderLink link = new GuestOrderLink();
    update(link, request, slug);
    return response(links.save(link));
  }

  @Transactional
  public GuestLinkResponse update(Long id, GuestLinkRequest request) {
    GuestOrderLink link = find(id);
    String slug = normalize(request.slug());
    if (links.existsBySlugAndIdNot(slug, id))
      throw new IllegalArgumentException("guest_link.error.slug_exists");
    update(link, request, slug);
    return response(link);
  }

  @Transactional
  public GuestLinkResponse setActive(Long id, GuestLinkStatusRequest request) {
    GuestOrderLink link = find(id);
    link.setActive(request.active());
    return response(link);
  }

  @Transactional(readOnly = true)
  public GuestOrderLink requireActive(String slug) {
    GuestOrderLink link = links.findBySlug(normalize(slug)).orElseThrow(
        () -> new ResourceNotFoundException("guest_link.error.not_found"));
    if (!link.isActive())
      throw new IllegalArgumentException("guest_link.error.inactive");
    return link;
  }

  private GuestOrderLink find(Long id) {
    return links.findById(id).orElseThrow(
        () -> new ResourceNotFoundException("guest_link.error.not_found"));
  }
  private void update(GuestOrderLink link, GuestLinkRequest request,
                      String slug) {
    link.setTitle(request.title().trim());
    link.setSlug(slug);
    link.setActive(request.active());
  }
  private String normalize(String slug) {
    return slug == null ? null : slug.trim().toLowerCase();
  }
  private GuestLinkResponse response(GuestOrderLink link) {
    return new GuestLinkResponse(link.getId(), link.getTitle(), link.getSlug(),
                                 link.isActive(), link.getCreatedAt(),
                                 link.getUpdatedAt());
  }
}
