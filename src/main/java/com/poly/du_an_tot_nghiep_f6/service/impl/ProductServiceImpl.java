package com.poly.du_an_tot_nghiep_f6.service.impl;

import com.poly.du_an_tot_nghiep_f6.entity.Product;
import com.poly.du_an_tot_nghiep_f6.repository.ProductDetailRepo;
import com.poly.du_an_tot_nghiep_f6.repository.ProductRepo;
import com.poly.du_an_tot_nghiep_f6.response.ProductHomeNewResponse;
import com.poly.du_an_tot_nghiep_f6.response.ProductHomeRespone;
import com.poly.du_an_tot_nghiep_f6.response.ProductResponse;
import com.poly.du_an_tot_nghiep_f6.response.ProductResponseAdmin;
import com.poly.du_an_tot_nghiep_f6.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepo productRepo;
    private final ProductDetailRepo productDetailRepo;

    public Page<ProductResponse> findAllProductResponse(Integer brandId,
                                                        Integer styleId,
                                                        Integer materialId,
                                                        Integer sizeId,
                                                        Integer colorId,
                                                        Integer categoryId, String sort,
                                                        String timTheoTen, Pageable pageable) {
        return productRepo.findAllProducctResponse(pageable,
                brandId, styleId, materialId, sizeId, colorId, categoryId, sort, timTheoTen);
    }

    public ProductResponse findProductResponseById(Integer productId){
        return productRepo.findProducctResponseByIdProdcut(productId);
    }

    public List<ProductResponseAdmin> findProductResponseAdmin(){
        return productRepo.findAllProducctResponse1();
    }

    public List<ProductHomeRespone> findProductResponseHomeCount(Pageable pageable){
        return productRepo.findAllProducctResponseHomeCount(pageable);
    }

    public List<ProductHomeNewResponse> findProductResponseHomeNew(Pageable pageable){
        return productRepo.findAllProducctResponseHomeNew(pageable);
    }

    @Override
    public List<Product> findAllByStatusTrue() {
        return productRepo.findAllByStatusEquals(1);
    }

    @Override
    public List<Product> findAll() {
        return productRepo.findAll();
    }


    @Override
    public Product findById(int id) {
        return productRepo.findById(id).orElseThrow(()-> new RuntimeException("No product variant  found with id: " + id));
    }

    @Override
    public Product findByName(String name) {
        return productRepo.findByName(name);
    }

    @Override
    public Product findByCode(String code) {
        return productRepo.findByCode(code);
    }

    @Override
    public void updateStatus(int id) {
        Product product = productRepo.findById(id).orElseThrow(() -> new RuntimeException("Ko tìm được sp"));
        if (product.getStatus() == 1){
            product.setStatus(0);
            productDetailRepo.updateStatusByIdProduct(id, 0);
        }else {
            product.setStatus(1);
            productDetailRepo.updateStatusByIdProduct(id, 1);
        }
        product.setDateUpdate(new Date());
        productRepo.save(product);
    }

    @Override
    public Product add(Product product) {
        product.setDateCreate(new Date());
        product.setCode(UUID.randomUUID().toString());
        return productRepo.save(product);
    }

    @Override
    public Product update(Product product) {
        return productRepo.findById(product.getId()).
                map(o->{
                    o.setName(product.getName());
                    o.setGender(product.getGender());
                    o.setDateUpdate(new Date());
                    o.setStatus(product.getStatus());
                    o.setMaterial(product.getMaterial());
                    o.setCategory(product.getCategory());
                    o.setStyle(product.getStyle());
                    o.setBrand(product.getBrand());
                    o.setDescription(product.getDescription());
                    return productRepo.save(o);
                }).orElseThrow(()-> new RuntimeException("Can't find color"));
    }
}
