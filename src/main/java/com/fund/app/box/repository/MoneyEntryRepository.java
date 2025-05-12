package com.fund.app.box.repository;

import com.fund.app.box.model.MoneyEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneyEntryRepository extends JpaRepository<MoneyEntry, Long> {
}
