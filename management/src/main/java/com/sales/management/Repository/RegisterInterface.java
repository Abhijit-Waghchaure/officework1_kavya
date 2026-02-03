package com.sales.management.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sales.management.entity.RegisterEntity;

public interface RegisterInterface extends JpaRepository<RegisterEntity, Long> {

	RegisterEntity findByMail(String mail);
	
}
