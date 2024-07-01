package com.mobatoguideevents.api.domain.event;

import java.util.Date;
import java.util.UUID;

public record EventResponseDTO(
        UUID id,
        String title,
        String description,
        Date date,
        String city,
        String states,
        Boolean remote,
        String eventUrl,
        String imageUrl
) {}
