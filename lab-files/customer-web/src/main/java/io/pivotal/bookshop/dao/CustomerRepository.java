package io.pivotal.bookshop.dao;

import io.pivotal.bookshop.domain.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {

}
