package com.fund.app.box.repository;

import com.fund.app.box.model.FundraisingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundraisingEventRepository extends JpaRepository<FundraisingEvent, Long> {
    boolean existsByEventName(String eventName);
}
