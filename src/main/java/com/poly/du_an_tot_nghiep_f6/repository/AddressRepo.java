package com.poly.du_an_tot_nghiep_f6.repository;

import com.poly.du_an_tot_nghiep_f6.entity.Address;
import com.poly.du_an_tot_nghiep_f6.response.AddressResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepo extends JpaRepository<Address, Integer> {
    List<Address> findByCustomerId(Integer customerId);

    @Query("""
            SELECT new com.poly.du_an_tot_nghiep_f6.response.AddressResponse(
                address.id,
                address.ward,
                address.district,
                address.province,
                address.customer.name,
                address.phone
            )
            FROM Address address
            WHERE address.customer.id=?1
            """)
    public List<AddressResponse> getAddressResponses(int id);



}
