package com.fund.app.box.repository;

import com.fund.app.box.model.CollectionBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionBoxRepository extends JpaRepository<CollectionBox, Long> {
    Optional<CollectionBox> findByUniqueIdentifier(String uniqueIdentifier);
}
