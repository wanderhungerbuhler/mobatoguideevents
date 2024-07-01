package com.mobatoguideevents.api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.mobatoguideevents.api.domain.event.Event;
import com.mobatoguideevents.api.domain.event.EventDetailsDTO;
import com.mobatoguideevents.api.domain.event.EventRequestDTO;
import com.mobatoguideevents.api.domain.event.EventResponseDTO;
import com.mobatoguideevents.api.repositories.AddressRepository;
import com.mobatoguideevents.api.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class EventService {

    @Autowired
    private EventRepository repository;

    @Autowired
    private AddressService addressService;

    @Value("${aws.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    public Event createEvent(EventRequestDTO data){
        String imgUrl = null;

        if(data.image() != null) {
            imgUrl = this.uploadImg(data.image());
        }

        Event newEvent = new Event();
        newEvent.setTitle(data.title());
        newEvent.setDescription(data.description());
        newEvent.setEventUrl(data.eventUrl());
        newEvent.setDate(new Date(data.date()));
        newEvent.setImgUrl(imgUrl);
        newEvent.setRemote(data.remote());

        repository.save(newEvent);

        if(!data.remote()) {
            this.addressService.createAddress(data, newEvent);
        }

        return newEvent;
    }

    public List<EventResponseDTO> getUpcomingEvents(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPage = this.repository.findUpcomingEvents(new Date(), pageable);
        return eventsPage.map(event -> new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getAddress() != null ? event.getAddress().getCity() : "",
                event.getAddress() != null ? event.getAddress().getStates() : "",
                event.getRemote(),
                event.getEventUrl(),
                event.getImgUrl())
        ).stream().toList();
    }

    public List<EventResponseDTO> getFilteredEvents(int page, int size, String title, String city, String states, Date startDate, Date endDate){
        title = (title != null) ? title : "";
        city = (city != null) ? city : "";
        states = (states != null) ? states : "";
        startDate = (startDate != null) ? startDate : new Date(0);
        endDate = (endDate != null) ? endDate : new Date();

        Pageable pageable = PageRequest.of(page, size);

        Page<Event> eventsPage = this.repository.findFilteredEvents(title, city, states, startDate, endDate, pageable);
        return eventsPage.map(event -> new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getAddress() != null ? event.getAddress().getCity() : "",
                event.getAddress() != null ? event.getAddress().getStates() : "",
                event.getRemote(),
                event.getEventUrl(),
                event.getImgUrl())
        ).stream().toList();
    }

    public EventDetailsDTO getEventDetails(UUID eventId) {
        Event event = this.repository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        return new EventDetailsDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getAddress() != null ? event.getAddress().getCity() : "",
                event.getAddress() != null ? event.getAddress().getStates() : "",
                event.getImgUrl(),
                event.getEventUrl()
        );
    }

    private String uploadImg(MultipartFile multipartFile) {
        String filename = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        try{
            File file = this.convertMultipartToFile(multipartFile);
            s3Client.putObject(bucketName, filename, file);
            file.delete();
            return s3Client.getUrl(bucketName, filename).toString();
        }catch(Exception error){
            System.out.println("erro ao subir arquivo");
            return "";
        }
    }

    private File convertMultipartToFile(MultipartFile multipartFile) throws IOException {
        File convFile = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(multipartFile.getBytes());
        fos.close();
        return convFile;
    }

}
