package com.mobatoguideevents.api.service;

import com.mobatoguideevents.api.domain.address.Address;
import com.mobatoguideevents.api.domain.event.Event;
import com.mobatoguideevents.api.domain.event.EventRequestDTO;
import com.mobatoguideevents.api.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public Address createAddress(EventRequestDTO data, Event event) {
        Address address = new Address();
        address.setCity(data.city());
        address.setStates(data.states());
        address.setEvent(event);

        return addressRepository.save(address);
    }
}
