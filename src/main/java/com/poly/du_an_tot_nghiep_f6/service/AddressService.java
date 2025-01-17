package com.poly.du_an_tot_nghiep_f6.service;

import com.poly.du_an_tot_nghiep_f6.entity.Address;
import com.poly.du_an_tot_nghiep_f6.entity.Customer;
import com.poly.du_an_tot_nghiep_f6.repository.AddressRepo;
import com.poly.du_an_tot_nghiep_f6.request.PayRequest;
import com.poly.du_an_tot_nghiep_f6.response.AddressResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressRepo addressRepository;

    public Address findById(Integer addressId) {
        return addressRepository.findById(addressId).orElse(null);
    }

    public void save(Address address) {
        addressRepository.save(address);
    }


    public List<Address> findByCustomerId(Integer customerId) {
        return addressRepository.findByCustomerId(customerId);
    }

    public void delete(Integer id) {
        addressRepository.deleteById(id);
    }

    public void deleteEntity(Address address) {
        addressRepository.delete(address);
    }

    public void setDefaultAddress(Integer addressId, Integer customerId) {
        List<Address> addresses = addressRepository.findByCustomerId(customerId);
        for (Address address : addresses) {
            address.setDefault(false);
            addressRepository.save(address);
        }
        Address defaultAddress = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));
        defaultAddress.setDefault(true);
        addressRepository.save(defaultAddress);
    }

    public List<AddressResponse> findByIdCustomer(int idCustomer) {
        return addressRepository.getAddressResponses(idCustomer);
    }


    public Address saveAddressPay(PayRequest payRequest, Customer customer) {
        Address address = new Address(
                null,
                customer,
                payRequest.getNameCustomer(),
                payRequest.getPhoneCustomer(),
                payRequest.getProvince(),
                payRequest.getDistrict(),
                payRequest.getWard(),
                payRequest.getAddressDetailCustomer(),
                new Date(),
                null,
                false
        );
        try {
            return saveReturnAddress(address);
        } catch (Exception e) {
            return null;
        }
    }

    private Address saveReturnAddress(Address address) {
        return addressRepository.save(address);
    }

}