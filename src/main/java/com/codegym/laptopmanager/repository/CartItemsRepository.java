package com.codegym.laptopmanager.repository;

import com.codegym.laptopmanager.model.CartItems;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemsRepository extends PagingAndSortingRepository<CartItems, Long> {
}
