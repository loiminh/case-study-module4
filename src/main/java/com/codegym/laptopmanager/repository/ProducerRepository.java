package com.codegym.laptopmanager.repository;

import com.codegym.laptopmanager.model.Producer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProducerRepository extends PagingAndSortingRepository<Producer, Long> {
    Page<Producer> findAllByName(String name , Pageable pageable);
    
}
