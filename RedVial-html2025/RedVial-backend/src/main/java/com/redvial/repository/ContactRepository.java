package com.redvial.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.redvial.model.ContactMessage;

public interface ContactRepository extends JpaRepository<ContactMessage, Long> {}
