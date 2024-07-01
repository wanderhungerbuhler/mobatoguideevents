package com.mobatoguideevents.api.repositories;

import com.mobatoguideevents.api.domain.address.Address;
import com.mobatoguideevents.api.domain.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {}
