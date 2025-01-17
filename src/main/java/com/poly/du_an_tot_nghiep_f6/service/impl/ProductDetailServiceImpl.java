package com.poly.du_an_tot_nghiep_f6.service.impl;


import com.poly.du_an_tot_nghiep_f6.entity.CartDetailInCounter;
import com.poly.du_an_tot_nghiep_f6.entity.Image;
import com.poly.du_an_tot_nghiep_f6.entity.ProductDetail;
import com.poly.du_an_tot_nghiep_f6.entity.PromotionDetail;
import com.poly.du_an_tot_nghiep_f6.repository.ProductDetailRepo;
import com.poly.du_an_tot_nghiep_f6.response.ProductDetailResponse;
import com.poly.du_an_tot_nghiep_f6.response.ProductShopDetailResponse;
import com.poly.du_an_tot_nghiep_f6.service.IProductDetailService;
import com.poly.du_an_tot_nghiep_f6.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductDetailServiceImpl implements IProductDetailService {

    private final ProductDetailRepo productDetailRepo;
    private final BarCodeServiceImpl barCodeService;

    @Autowired
    PromotionService promotionService;

    public ProductShopDetailResponse ProductShopDetailResponse(Integer productId, Integer colorId, Integer sizeId) {
        return productDetailRepo.findProShopResByIdColorAndIdSize(productId, sizeId, colorId);
    }

    @Override
    public List<ProductDetail> findAll() {
        return productDetailRepo.findAllByOrderByDateCreateDesc();
    }

    @Override
    public ProductDetail findById(int id) {
        try {
            return productDetailRepo.findById(id).get();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void changeStatus(int id) {
        ProductDetail productDetail = findById(id);
        if (productDetail.getStatus() == 1) {
            productDetail.setStatus(0);
        } else productDetail.setStatus(1);
        productDetail.setDateCreate(new Date());
        productDetailRepo.save(productDetail);
    }

    public void changerQuantity(int idProduct, int quantity, boolean status) {
        ProductDetail productDetail = productDetailRepo.findById(idProduct).get();

        int quantityInStoreHouse = productDetail.getQuantity();
        if (status) {
            quantityInStoreHouse += quantity;
        } else {
            quantityInStoreHouse -= quantity;
        }
        System.out.println("Đã giảm số lượng sản phẩm: " + productDetail.getFullName() + " xuống " + quantityInStoreHouse);
        productDetail.setQuantity(quantityInStoreHouse);
        productDetailRepo.save(productDetail);
    }

    @Override
    public List<ProductDetail> findByProductId(int productId) {
        return productDetailRepo.findAllByProductId(productId);
    }

    @Override
    public void deleteById(int id) {
        productDetailRepo.deleteById(id);
    }


    @Override
    public ProductDetail add(ProductDetail productDetail) {
        productDetail.setDateCreate(new Date());
        productDetail.setProductCode(UUID.randomUUID().toString());
        return productDetailRepo.save(productDetail);
    }

    @Override
    public ProductDetail update(ProductDetail productDetail) {
        return productDetailRepo.findById(productDetail.getId()).
                map(o -> {
                    o.setDateUpdate(new Date());
                    o.setWeight(productDetail.getWeight());
                    o.setStatus(productDetail.getStatus());
                    o.setColor(productDetail.getColor());
                    o.setSize(productDetail.getSize());
                    o.setPrice(productDetail.getPrice());
                    o.setQuantity(productDetail.getQuantity());
                    return productDetailRepo.save(o);
                }).orElseThrow(() -> new RuntimeException("Ko tìm được id Product Variant"));
    }

    public ProductDetail updateHaveImage(ProductDetail productDetail) {
        return productDetailRepo.findById(productDetail.getId()).
                map(o -> {
                    o.setDateUpdate(new Date());
                    o.setWeight(productDetail.getWeight());
                    o.setStatus(productDetail.getStatus());
                    o.setImage(productDetail.getImage());
                    o.setColor(productDetail.getColor());
                    o.setSize(productDetail.getSize());
                    o.setPrice(productDetail.getPrice());
                    o.setQuantity(productDetail.getQuantity());
                    return productDetailRepo.save(o);
                }).orElseThrow(() -> new RuntimeException("Ko tìm được id Product Variant"));
    }

    public ProductDetail updateQRCODE(ProductDetail productDetail) {
        return productDetailRepo.findById(productDetail.getId()).
                map(o -> {
                    o.setQrCode(productDetail.getQrCode());
                    return productDetailRepo.save(o);
                }).orElseThrow(() -> new RuntimeException("Ko tìm được id Product Variant"));
    }

    public void updateByIdAndProductId(Double price, Integer quantity, Double weight, Integer id, Image image, String qrCode) {
        productDetailRepo.updateByProductId(image, price, quantity, weight, id, qrCode);
    }

    public List<Integer> countIdColor(Integer idProduct) {
        return productDetailRepo.getIdColorCount(idProduct);
    }


    public List<ProductDetailResponse> findAllResponse() {
        return productDetailRepo.getProductResponse();
    }

    public List<ProductDetailResponse> filterProduct(int idSize, int idColor, int idStyle, int idCategory, int idMaterial, int idBrand, String keyWord) {
        int id = 0;
        try {
            id = Integer.parseInt(keyWord.trim());
        } catch (Exception e) {
        }
        return productDetailRepo.getProductResponseFilter(idSize, idColor, idStyle, idCategory, idMaterial, idBrand, keyWord, id);
    }
}

