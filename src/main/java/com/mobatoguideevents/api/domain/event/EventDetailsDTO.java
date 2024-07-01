package com.mobatoguideevents.api.domain.event;

import java.util.Date;
import java.util.UUID;

public record EventDetailsDTO(
        UUID id,
        String title,
        String description,
        Date date,
        String city,
        String states,
        String imgUrl,
        String eventUrl
) {}
