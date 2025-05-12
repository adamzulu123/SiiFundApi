package com.fund.app.box.repository;

import com.fund.app.box.model.FundraisingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FundraisingEventRepository extends JpaRepository<FundraisingEvent, Long> {
    boolean existsByEventName(String eventName);
    Optional<FundraisingEvent> findByEventName(String eventName);
}
