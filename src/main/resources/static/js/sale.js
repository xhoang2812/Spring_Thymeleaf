console.log("Call Sale JS");
//api key ghn : e72e960b-9e77-11ef-a35f-3e447ea83dcd
const apiUrls = {
    getAttributeProduct: 'http://localhost:8080/api/sale/get-attribute-product',
    getProducts: 'http://localhost:8080/api/sale/get-products',
    getCustomers: 'http://localhost:8080/api/sale/get-customers',
    filterProduct: 'http://localhost:8080/api/sale/filter-product',
    createCart: 'http://localhost:8080/api/sale/create-cart',
    removeCart: 'http://localhost:8080/api/sale/remove-cart',
    removeCustomer: 'http://localhost:8080/api/sale/remove-customer-in-cart',
    getCarts: 'http://localhost:8080/api/sale/get-carts',
    getCart: 'http://localhost:8080/api/sale/get-cart',
    getIdCartSelect: 'http://localhost:8080/api/sale/get-cartId-select',
    getAddressForCustomerInCart: 'http://localhost:8080/api/sale/get-address-for-cart',
    getVoucherForCustomer: 'http://localhost:8080/api/sale/get-voucher-for-customer',
    setAddressInCart: 'http://localhost:8080/api/sale/set-address-to-cart',
    setIdCartSelect: 'http://localhost:8080/api/sale/set-cartId-select',
    setCustomerInCart: 'http://localhost:8080/api/sale/set-customer-in-cart',
    setVoucherInCart: 'http://localhost:8080/api/sale/set-voucher-for-cart',
    getProductsInCart: 'http://localhost:8080/api/sale/get-product-in-cart',
    addToCart: 'http://localhost:8080/api/sale/add-product-to-cart',
    deleteProductInCart: 'http://localhost:8080/api/sale/delete-product-in-cart',
    deleteVoucherInCart: 'http://localhost:8080/api/sale/delete-voucher-in-cart',
    updateQuantityProductInCart: 'http://localhost:8080/api/sale/update-quantity-product-in-cart',
    pay: 'http://localhost:8080/api/sale/pay',
    setShipToCart: 'http://localhost:8080/api/sale/set-ship-to-cart'
};

const popupSelectProduct = document.getElementById("popupSelectProduct");
const searchProduct = document.getElementById("searchProduct");
let carts = [];
var shipPrice = 0.0;
var idCustomer = 0;
var paymentMethods = -1;
var isPaymentMethod = true;
// var excessMoney=0;

const GHN_TOKEN = 'e72e960b-9e77-11ef-a35f-3e447ea83dcd'; // Thay YOUR_GHN_TOKEN bằng token thật của bạn
const SHOP_ID = 5445966; // Thay bằng shop_id của bạn

function loading() {
    // setTimeout(() => {
    //     $.ajax({
    //         url: "/api/vouchers/userData",
    //         method: "GET",
    //         success: function (data) {
    //             if (data === "") {
    //                 document.body.classList.add('show');
    //             } else {
    //                 sessionStorage.setItem("notificationForMap", "Bạn không phải là nhân viên");
    //                 window.location.href = "/";
    //             }
    //         },
    //         error: function (error) {
    //             console.log(error)
    //         }
    //     })
    // }, 300);
}

// Hàm lấy `service_id` từ GHN
function getServiceId(fromDistrictId, toDistrictId, weight) {
    return fetch('https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/available-services', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Token': GHN_TOKEN
        },
        body: JSON.stringify({
            "shop_id": SHOP_ID,
            "from_district": fromDistrictId,
            "to_district": toDistrictId
        })
    })
        .then(response => response.json())
        .then(data => {
            if (data.code === 200 && data.data.length > 0) {
                // Kiểm tra trọng lượng để chọn `service_id`
                const service = data.data.find(s => (weight < 5000 && s.short_name === "Hàng nhẹ") || (weight >= 5000 && s.short_name === "Hàng nặng"));
                return service ? service.service_id : null;
            } else {
                throw new Error('Không có dịch vụ nào khả dụng hoặc lỗi khi lấy service_id');
            }
        })
        .catch(error => {
            console.error('Lỗi khi lấy service_id:', error);
        });
}

function loadProvinces() {
    if (document.getElementById('province').options.length === 1) {
        return fetch('https://online-gateway.ghn.vn/shiip/public-api/master-data/province', {
            headers: {
                'Content-Type': 'application/json',
                'Token': GHN_TOKEN
            }
        })
            .then(response => response.json())
            .then(data => {
                let provinceSelect = document.getElementById('province');
                provinceSelect.innerHTML = '<option value="">Chọn tỉnh/thành phố</option>';
                data.data.forEach(province => {
                    let option = document.createElement('option');
                    option.value = province.ProvinceID;
                    option.text = province.ProvinceName;
                    provinceSelect.add(option);
                });
            })
            .catch(error => console.error('Error fetching provinces:', error));
    }
}

function loadDistricts() {
    let provinceId = document.getElementById('province').value;
    let districtSelect = document.getElementById('district');
    districtSelect.innerHTML = '<option value="">Chọn quận/huyện</option>';
    document.getElementById('ward').innerHTML = '<option value="">Chọn phường/xã</option>';

    if (provinceId) {
        return fetch('https://online-gateway.ghn.vn/shiip/public-api/master-data/district', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Token': GHN_TOKEN
            },
            body: JSON.stringify({"province_id": parseInt(provinceId)})
        })
            .then(response => response.json())
            .then(data => {
                data.data.forEach(district => {
                    let option = document.createElement('option');
                    option.value = district.DistrictID;
                    option.text = district.DistrictName;
                    districtSelect.add(option);
                });
            })
            .catch(error => console.error('Error fetching districts:', error));
    } else {
        return Promise.resolve();
    }
}

function loadWards() {
    let districtId = document.getElementById('district').value;
    let wardSelect = document.getElementById('ward');
    wardSelect.innerHTML = '<option value="">Chọn phường/xã</option>';

    if (districtId) {
        return fetch('https://online-gateway.ghn.vn/shiip/public-api/master-data/ward', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Token': GHN_TOKEN
            },
            body: JSON.stringify({"district_id": parseInt(districtId)})
        })
            .then(response => response.json())
            .then(data => {
                data.data.forEach(ward => {
                    let option = document.createElement('option');
                    option.value = ward.WardCode;
                    option.text = ward.WardName;
                    wardSelect.add(option);
                });
            })
            .catch(error => console.error('Error fetching wards:', error));
    } else {
        return Promise.resolve();
    }
}


const delivery = document.getElementById("delivery");
const hideDelivery = document.getElementById("hideDelivery");
document.getElementById("province").addEventListener("click", function () {
    loadProvinces();
});

hideDelivery.addEventListener("click", function () {
    delivery.style.display = this.checked ? "block" : "none";
    showPrice();
    fetch(`${apiUrls.setShipToCart}?idCart=${currentCartId}`, {method: 'PUT'})
        .then(handleFetchResponse)
        .then(data => {
            if (!data) {
                showErrors('Lỗi')
            }
        })
        .catch(handleError);
});


// Load products and attributes on popup open
popupSelectProduct.addEventListener("click", function () {
    fetchData(apiUrls.getProducts, renderProducts);
    fetchData(apiUrls.getAttributeProduct, renderAttributes);
});

// Fetch data from API
function fetchData(url, callback) {
    fetch(url)
        .then(handleFetchResponse)
        .then(callback)
        .catch(handleError);
}

// Handle response and check for errors
function handleFetchResponse(response) {
    if (!response.ok) {
        return response.text().then(text => {
            throw new Error(`Failed to fetch data from API: ${response.status} - ${text}`);
        });
    }

    // Kiểm tra xem phản hồi có chứa dữ liệu không
    if (response.status === 204) { // No Content
        return []; // Trả về mảng rỗng thay vì null
    }

    return response.json().then(data => {
        return data; // Trả về dữ liệu để xử lý tiếp
    }).catch(err => {
        return []; // Trả về mảng rỗng nếu có lỗi
    });
}


// Populate select options
function populateSelect(selectId, data, defaultText) {
    const selectElement = document.getElementById(selectId);
    selectElement.innerHTML = `<option value="0" selected>${defaultText}</option>`;
    data.forEach(item => {
        const option = document.createElement('option');
        option.value = item.id;
        option.text = item.name;
        selectElement.add(option);
    });
}

// Render products in the table
function renderProducts(products) {
    const tbodyProduct = document.getElementById('tbodyProduct');
    tbodyProduct.innerHTML = '';
    products.forEach((product) => {
        const row = `
            <tr ${product.quantity < 1 ? `style="background-color: grey;"` : ``}>
                <th scope="row">${product.id}</th>
                <td><a target="_blank" href="san-pham-chi-tiet/detail/${product.id}">${product.name}</a><br><div style="font-size: 12px;color: #6c757d;">${product.namePromotion || ""}</div></td>
                <td>${product.style}</td>
                <td>${product.material}</td>
                <td>${product.color}</td>
                <td>${product.size}</td>
                <td>${product.quantity}</td>
                <td>
                    ${product.newPrice === 0
            ? `${product.price.toLocaleString()} đ`
            : `${product.newPrice.toLocaleString()} <del class="discount-price">${product.price.toLocaleString()}</del> đ <br> <div style="font-size: 12px;color: #6c757d; " ></div>`}
                <td>
                ${product.quantity > 0 ? `<button class="btn" id="popupInputQuantity" data-bs-toggle="modal" onclick="setProductAdd(${product.id})"
//                                             data-bs-target="#myModalInputQuantityProduct">
                        <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAAXNSR0IArs4c6QAAAYVJREFUSEvN1D9IlVEYx/HPJV0abBAUbIggaAinhnQJhxpdgqAhCMQ/oEIt4qijREtEQZEENQQtLm4FgYOog4tNNRokhYug4KTvI+eFy8u9933pdqVnOXDOc37f53eeh1PT4ah1WN+5Ak6Sm0Ms4/G/cFfvIAfkunfxpV1IoyeaxxJWMdoEkBdT+sSNEnqxhwu4gt0GkLYAofcBD/EU4agYbQOGsY59XMZxgdA2IPS+4QYe4T1eY7JJT95gCi8wi+d4ErmtmhRiIbqFW+jGGoYKkA3cTnt/cAnX8b0McDGr6hd6cBPb6MNOWuP+bwym9UEm/BGb9UWUjVlu+R3GUpXhIJxEROXhIOIz7mACb3OXZYBr+JGaHNUfpIt5L+LtIwbwE0foR/wGZ1EGiJyvGMkuzeFZkyYvYiF9MeP1OVUA9/GpiXBxO8Y7f7LKDrrSRFwtgazgXjGnioOKxTdOqwp4mY3idDaerzBTkGp1VqnJoVf/lReLanVWGdBxB3/dh6o9+H8BpwsdRBk/cucSAAAAAElFTkSuQmCC"/>
                    </button>` : `Hết hàng`}
                    
                </td>
            </tr>`;
        tbodyProduct.insertAdjacentHTML('beforeend', row);
    });
}

var idProductAdd = 0;

function setProductAdd(idProduct) {
    idProductAdd = idProduct;
}

function renderVoucher() {
    fetch(apiUrls.getVoucherForCustomer + '?idCustomer=' + idCustomer)
        .then(handleFetchResponse)
        .then(function (listVoucher) {
            const tbodyVoucher = document.getElementById('tbodyVoucher');
            tbodyVoucher.innerHTML = '';
            listVoucher.forEach((voucher, index) => {
                const isEligible = totalPrice >= voucher.minimumOrder;
                let discountText = '';
                if (voucher.styleDiscount) {
                    discountText = `${voucher.discount.toLocaleString()} %`;
                } else {
                    discountText = `${voucher.discount} đ`;
                }
                const row = `
                            <tr>
                                <td>${index + 1}</td>
                                <td>${voucher.name}</td>
                                <td>${voucher.fromVoucher ? "Công khai" : "Riêng tư"}</td>
                                <td>${voucher.code}</td>
                                <td>${discountText}</td>
                                <td>${voucher.minimumOrder.toLocaleString()}</td>
                                <td>${voucher.maximumReduction.toLocaleString()}</td>
                                <td>
                                    ${isEligible ? `<a class="btn" data-bs-dismiss="modal" onclick="setVoucherInCart(${voucher.id})">
                                                    <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAAXNSR0IArs4c6QAAAYVJREFUSEvN1D9IlVEYx/HPJV0abBAUbIggaAinhnQJhxpdgqAhCMQ/oEIt4qijREtEQZEENQQtLm4FgYOog4tNNRokhYug4KTvI+eFy8u9933pdqVnOXDOc37f53eeh1PT4ah1WN+5Ak6Sm0Ms4/G/cFfvIAfkunfxpV1IoyeaxxJWMdoEkBdT+sSNEnqxhwu4gt0GkLYAofcBD/EU4agYbQOGsY59XMZxgdA2IPS+4QYe4T1eY7JJT95gCi8wi+d4ErmtmhRiIbqFW+jGGoYKkA3cTnt/cAnX8b0McDGr6hd6cBPb6MNOWuP+bwym9UEm/BGb9UWUjVlu+R3GUpXhIJxEROXhIOIz7mACb3OXZYBr+JGaHNUfpIt5L+LtIwbwE0foR/wGZ1EGiJyvGMkuzeFZkyYvYiF9MeP1OVUA9/GpiXBxO8Y7f7LKDrrSRFwtgazgXjGnioOKxTdOqwp4mY3idDaerzBTkGp1VqnJoVf/lReLanVWGdBxB3/dh6o9+H8BpwsdRBk/cucSAAAAAElFTkSuQmCC"/>
                                                    </a>` : 'Chưa đủ điều kiện'}
                                </td>
                            </tr>
                        `;
                tbodyVoucher.insertAdjacentHTML('beforeend', row);
            });

        })
        .catch(handleError);

}

function renderAddress() {
    fetch(apiUrls.getAddressForCustomerInCart)
        .then(handleFetchResponse)
        .then(function (listAddress) {
            const tbodyAddress = document.getElementById('tbodyAddress');
            tbodyAddress.innerHTML = '';
            listAddress.forEach((address, index) => {
                const row = `
                            <tr>
                                <th scope="row">${index + 1}</th>
                                <td>${address.nameCustomer}</td>
                                <td>${address.phone}</td>
                                <td>${address.address}</td>
                                <td>
                                    <a class="btn" data-bs-dismiss="modal" onclick="addAddressToCart(${address.id})">
                                        <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAAXNSR0IArs4c6QAAAYVJREFUSEvN1D9IlVEYx/HPJV0abBAUbIggaAinhnQJhxpdgqAhCMQ/oEIt4qijREtEQZEENQQtLm4FgYOog4tNNRokhYug4KTvI+eFy8u9933pdqVnOXDOc37f53eeh1PT4ah1WN+5Ak6Sm0Ms4/G/cFfvIAfkunfxpV1IoyeaxxJWMdoEkBdT+sSNEnqxhwu4gt0GkLYAofcBD/EU4agYbQOGsY59XMZxgdA2IPS+4QYe4T1eY7JJT95gCi8wi+d4ErmtmhRiIbqFW+jGGoYKkA3cTnt/cAnX8b0McDGr6hd6cBPb6MNOWuP+bwym9UEm/BGb9UWUjVlu+R3GUpXhIJxEROXhIOIz7mACb3OXZYBr+JGaHNUfpIt5L+LtIwbwE0foR/wGZ1EGiJyvGMkuzeFZkyYvYiF9MeP1OVUA9/GpiXBxO8Y7f7LKDrrSRFwtgazgXjGnioOKxTdOqwp4mY3idDaerzBTkGp1VqnJoVf/lReLanVWGdBxB3/dh6o9+H8BpwsdRBk/cucSAAAAAElFTkSuQmCC"/>
                                    </a>
                                </td>
                            </tr>`;

                tbodyAddress.insertAdjacentHTML('beforeend', row);
            });
        })
        .catch(handleError);

}

async function showAddress(data) {
    const nameCustomer = document.getElementById('nameCustomer');
    const phoneCustomer = document.getElementById('phoneCustomer');
    const province = document.getElementById('province');
    const district = document.getElementById('district');
    const ward = document.getElementById('ward');
    const addressDetailCustomer = document.getElementById('addressDetailCustomer');
    const describeCustomerBill = document.getElementById('describeCustomerBill');
    const describeCustomerAddress = document.getElementById('describeCustomerAddress');

    if (data != null) {
        nameCustomer.value = data.nameRecipient;
        phoneCustomer.value = data.phone;
        addressDetailCustomer.value = data.addreseDetail;
        await loadProvinces();
        // Chọn tỉnh/thành phố và tải quận/huyện
        for (const option of province.options) {
            if (option.text === data.province) {
                option.selected = true;
                await loadDistricts(); // Đợi dữ liệu quận/huyện được tải trước khi tiếp tục
                break;
            }
        }

        // Chọn quận/huyện và tải phường/xã
        for (const option of district.options) {
            if (option.text === data.district) {
                option.selected = true;
                await loadWards(); // Đợi dữ liệu phường/xã được tải trước khi tiếp tục
                break;
            }
        }

        // Chọn phường/xã
        for (const option of ward.options) {
            if (option.text === data.ward) {
                option.selected = true;
                break;
            }
        }
    } else {
        shipPrice = 0;
        nameCustomer.value = null;
        phoneCustomer.value = null;
        addressDetailCustomer.value = null;
        describeCustomerAddress.value = null;
        province.selectedIndex = 0;
        district.selectedIndex = 0;
        ward.selectedIndex = 0;
    }
}


function addAddressToCart(idAddress) {
    fetch(`${apiUrls.setAddressInCart}?idAddress=${idAddress}`, {method: 'PUT'})
        .then(handleFetchResponse)
        .then(data => {
            if (data !== null) {
                showSuccesss("Chọn địa chỉ thành công");
                showAddress(data);
            } else {
                showErrors("Lỗi hệ thống, vui lòng liên hệ admin")
            }
        })
        .catch(handleError);
}


// Render attributes in selects
function renderAttributes(data) {
    const [categories, nsx, colors, materials, sizes, styles] = data;
    populateSelect('styleSelect', styles, 'Chọn kiểu dáng');
    populateSelect('materialSelect', materials, 'Chọn chất liệu');
    populateSelect('nsxSelect', nsx, 'Chọn NSX');
    populateSelect('categorySelect', categories, 'Chọn danh mục');
    populateSelect('sizeSelect', sizes, 'Chọn kích thước');
    populateSelect('colorSelect', colors, 'Chọn màu sắc');
}

// Filter products based on selected attributes
function filterProducts() {
    const filters = {
        idStyle: document.getElementById('styleSelect').value,
        idMaterial: document.getElementById('materialSelect').value,
        idBrand: document.getElementById('nsxSelect').value,
        idCategory: document.getElementById('categorySelect').value,
        idSize: document.getElementById('sizeSelect').value,
        idColor: document.getElementById('colorSelect').value,
        keyWord: searchProduct.value
    };

    const apiUrl = new URL(apiUrls.filterProduct);
    Object.entries(filters).forEach(([key, value]) => {
        if (value) apiUrl.searchParams.append(key, value);
    });

    fetch(apiUrl)
        .then(handleFetchResponse)
        .then(renderProducts)
        .catch(handleError);
}


function pay() {
    if (carts.length !== 0) {
        const addressRequest = validateAddress();
        if (addressRequest !== null) {
            Swal.fire({
                title: 'Xác nhận thanh toán',
                text: isPaymentMethod ? 'Bạn có chắc chắn muốn thanh toán không?' : 'Thanh toán số tiền chênh lệch?',
                icon: 'warning',
                showCancelButton: true,       // Hiển thị nút "Hủy"
                confirmButtonText: 'Thanh toán', // Nút xác nhận đăng xuất
                cancelButtonText: 'Hủy'        // Nút hủy
            }).then((result) => {
                if (result.isConfirmed) {
                    fetch(`${apiUrls.pay}?idCart=${currentCartId}`, {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify(addressRequest)
                    })
                        .then(handleFetchResponse)
                        .then(data => {
                            switch (data) {
                                case -1: {
                                    showErrors("Hóa đơn đang ở trạng thái không thể chỉnh sửa, vui lòng thực hiện hóa đơn khác.");
                                    removeCart(currentCartId, true);
                                    break;
                                }
                                case 1: {
                                    showErrors("Chưa có sản phẩm nào trong giỏ.");
                                    break;
                                }
                                case 2: {
                                    showErrors("Chưa có địa chỉ.");
                                    break;
                                }
                                case 3: {
                                    showErrors('Lỗi hệ thống, hãy tải lại trang và thử lại.');
                                    break;
                                }
                                default: {
                                    removeCart(currentCartId, false);
                                    showSuccesss("Thanh toán thành công");
                                    console.log("BillID:" + data)
                                    printBill(data);
                                    paymentMethods = -1;
                                    break;
                                }
                            }

                        })
                        .catch(handleError);
                }
            });
        }
    } else {
        showErrors("Vui lòng chọn/tạo giỏ hàng trước.");
    }
}

function printBill(id) {
    if (document.getElementById('printfBill').checked) {
        exportToPDF(id);
    }
}

function setPaymentMethods(id) {
    console.log(id)
    paymentMethods = id;
}

function validateAddress() {
    console.log("Thanh toán:" + totalPriceo)
    if (totalPrice === 0) {
        showErrors('Chưa có sản phẩm nào');
        return null;
    } else {
        const describeCustomerBill = document.getElementById('describeCustomerBill').value;
        if (hideDelivery.checked) {
            const nameCustomer = document.getElementById('nameCustomer').value;
            const phoneCustomer = document.getElementById('phoneCustomer').value;
            const province = document.getElementById('province');
            const district = document.getElementById('district');
            const ward = document.getElementById('ward');
            const addressDetailCustomer = document.getElementById('addressDetailCustomer').value;
            const describeCustomerAddress = document.getElementById('describeCustomerAddress').value;
            const saveAddress = document.getElementById('saveAddress');

            if (nameCustomer.length === 0) {
                showErrors('Nhập họ tên khách hàng');
                return null;
            }
            if (phoneCustomer.length === 0) {
                showErrors('Nhập số điện thoại khách hàng');
                return null;
            }
            if (phoneCustomer.length !== 10) {
                showErrors('Số điện thoại có 10 chữ số.');
                return null;
            }
            if (province.selectedIndex === 0) {
                showErrors('Chọn tỉnh/thành phố');
                return null;
            }
            if (district.selectedIndex === 0) {
                showErrors('Chọn quận/huyện');
                return null;
            }
            if (ward.selectedIndex === 0) {
                showErrors('Chọn phường/xã');
                return null;
            }
            if (addressDetailCustomer.length === 0) {
                showErrors('Nhập địa chỉ cụ thể');
                return null;
            }
            if (totalPriceo > 0 && isPaymentMethod) {
                if (paymentMethods === -1) {
                    showErrors('Chọn phương thức thanh toán.');
                    return null;
                }
                if (document.getElementById('inputMoney').value === "" && paymentMethods === 1) {
                    showErrors("Nhập tiền khách đưa")
                    return null;
                }
                if (document.getElementById('inputMoney').value < totalPrice - reducedPrice + shipPrice - oldTotalPrice && paymentMethods === 1) {
                    showErrors("Số tiền thanh toán không đủ.")
                    return null;
                }
            }
            return {
                'nameCustomer': nameCustomer,
                'phoneCustomer': phoneCustomer,
                'province': province.options[province.selectedIndex].text,
                'district': district.options[district.selectedIndex].text,
                'ward': ward.options[ward.selectedIndex].text,
                'addressDetailCustomer': addressDetailCustomer,
                'describeCustomerAddress': describeCustomerAddress,
                'shipPrice': shipPrice,
                'saveAddress': saveAddress.checked,
                'idPaymentMethods': paymentMethods,
                'describeCustomerBill': describeCustomerBill,
            };
        }
        if (totalPriceo > 0 && isPaymentMethod) {
            if (paymentMethods === -1) {
                showErrors('Chọn phương thức thanh toán.');
                return null;
            }
            if (document.getElementById('inputMoney').value === "" && paymentMethods === 1) {
                showErrors("Nhập tiền khách đưa")
                return null;
            }
            if (document.getElementById('inputMoney').value < totalPrice - reducedPrice + shipPrice - oldTotalPrice && paymentMethods === 1) {
                showErrors("Số tiền thanh toán không đủ.")
                return null;
            }
        }
        return {
            'nameCustomer': null,
            'phoneCustomer': null,
            'province': null,
            'district': null,
            'ward': null,
            'addressDetailCustomer': null,
            'describeCustomer': null,
            'shipPrice': 0.0,
            'saveAddress': false,
            'idPaymentMethods': paymentMethods,
            'describeCustomerBill': describeCustomerBill,
        };
    }

}

// Create a new cart
function createCart() {
    fetchData(apiUrls.createCart, cart => {
        if (cart !== null) {
            if (carts.length !== 20) {
                carts.push(cart.id);
                currentCartId = cart.id;
                addTabAndPanel(cart.id, null);
                handleClickCart(cart.id);
            } else {
                showErrors("Tạo quá nhiều hóa đơn, vui lòng xóa bớt hóa đơn thừa.");
            }
        } else {
            showErrors("Không thể tạo giỏ hàng mới");
        }
    });
}


// Load carts from API
function loadCarts() {
    getCurrentCartId();
    fetchData(apiUrls.getCarts, data => {
        if (data != null || data !== undefined) {
            if (Array.isArray(data)) {
                carts = data.map(cart => cart.id);
                idEdits = data.map(cart => cart.billEdit ? cart.billEdit.id : null);
                var i = 0;
                carts.forEach(cartId => {
                    addTabAndPanel(cartId, idEdits.at(i));
                    i++;
                });
                handleClickCart(currentCartId);
            }
        }
    });
}

// Add tab and panel for cart
function addTabAndPanel(cartId, idBillEdit) {
    addTab(cartId, idBillEdit);
    addPanel(cartId);
}

// Add tab for cart
function addTab(cartId, idBillEdit) {
    const tabContent = `
        <li class="nav-item" id="tab-${cartId}"   onclick="handleClickCart(${cartId})">
            <a class="nav-link"  ${idBillEdit !== null ? 'style="background-color: grey"' : ''} id="pills-home-tab-${cartId}" data-bs-toggle="pill"
               href="#pills-home-${cartId}" role="tab" aria-controls="pills-home-${cartId}" aria-selected="false">
                ${idBillEdit !== null ? 'Hóa đơn ' + cartId + ` ( #${idBillEdit})` : 'Hóa đơn ' + cartId}
               <span class="close-tab" onclick="handleRemoveCart(${cartId},true,${idBillEdit})" style="margin-left: 10px; cursor: pointer;">&times;</span>
            </a>
        </li>`;
    document.querySelector("#pills-tab-without-border").insertAdjacentHTML('beforeend', tabContent);
}

// Add panel for cart
function addPanel(cartId) {
    const panelContent = `
        <div class="tab-pane fade" id="pills-home-${cartId}" role="tabpanel" aria-labelledby="pills-home-tab-${cartId}">
            <div class="card-table">
                <div class="card-header">
                    <div class="card-title">Giỏ Hàng ${cartId}</div>
                </div>
                <div class="card-body" style="height: 250px;overflow-y: auto;">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>Sản phẩm</th>
                                <th>Đơn giá</th>
                                <th>Số lượng</th>
                                <th>Thành tiền</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody id="cart-${cartId}-items"></tbody>
                    </table>
                </div>
            </div>
        </div>`;
    document.querySelector("#pills-without-border-tabContent").insertAdjacentHTML('beforeend', panelContent);
}

function handleRemoveCart(cartId, status, idBillEdit) {
    if (status) {
        var d = idBillEdit !== null ? 'hủy sửa' : 'xóa';
        var e = idBillEdit !== null ? 'Hủy sửa' : 'Xóa';
        Swal.fire({
            title: e + ' hóa đơn',
            text: 'Bạn có chắc muốn ' + d + " hóa đơn không?",
            icon: 'warning',
            showCancelButton: true,       // Hiển thị nút "Hủy"
            confirmButtonText: 'Xóa', // Nút xác nhận đăng xuất
            cancelButtonText: 'Hủy'        // Nút hủy
        }).then((result) => {
            if (result.isConfirmed) {
                removeCart(cartId, status);
            }
        });
    } else {
        removeCart(cartId, status);
    }

}

function removeCart(cartId, status) {
    fetch(`${apiUrls.removeCart}?idCart=${cartId}&status=${status}`, {method: 'DELETE'})
        .then(handleFetchResponse)
        .then(data => {
            if (data) {
                document.getElementById(`tab-${cartId}`).remove();
                document.getElementById(`pills-home-${cartId}`).remove();
                carts = carts.filter(id => id !== cartId);
                oldTotalPrice = 0
                oldTotalPricex = 0
                showPrice();
                setDefaultForm();
                // Kiểm tra xem còn hóa đơn nào không
                currentCartId = null
                handleClickCart(carts[carts.length - 1]); // Thiết lập tab cuối cùng là active
            } else {
                showErrors("Không thể xóa giỏ hàng.");
            }
        })
        .catch(handleError);
}


function removeProductInCart(idCartDetail, intoPrice) {
    if (voucher !== null) {
        if (totalPrice - intoPrice < voucher.minimumOrder) {// ko đủ đkien voucher thì con confirm
            Swal.fire({
                title: 'Bớt sản phẩm này thì giá tối thiểu của voucher sẽ không đạt',
                text: 'Bạn có chắc muốn bớt không?',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonText: 'Ok',
                cancelButtonText: 'Hủy'
            }).then(result => {
                if (result.isConfirmed) {// nhấn ok thì xóa voucher
                    removeVoucherInCart(currentCartId);
                    removeCartDetail(idCartDetail);
                }
            });
        } else {// đủ đk thì xóa thẳng
            removeCartDetail(idCartDetail);
        }
    } else {
        removeCartDetail(idCartDetail);
    }
}

function removeCartDetail(idCartDetail) {
    fetch(`${apiUrls.deleteProductInCart}?idCartDetail=${idCartDetail}`, {method: 'DELETE'})
        .then(handleFetchResponse)
        .then(data => {
            if (data) {
                loadProductsInCart(currentCartId)
            } else {
                showErrors("Không thể xóa sản phẩm.");
            }
        })
        .catch(handleError);
}

function removeVoucherInCart(idCart) {
    fetch(`${apiUrls.deleteVoucherInCart}?idCart=${idCart}`, {method: 'DELETE'})
        .then(handleFetchResponse)
        .then(data => {
            if (!data) {
                showErrors("Không thể xóa voucher.");
            }
            reducedPrice = 0;
            voucher = null;
            getCart();
        })
        .catch(handleError);
}

function updateQuantityProductInCart(idInputCart, idCartDetail, status) {
    var quantity = parseInt(document.getElementById(idInputCart).value, 10);
    fetch(`${apiUrls.updateQuantityProductInCart}?idCartDetail=${idCartDetail}&quantity=${quantity}&status=${status}`, {method: 'PUT'})
        .then(handleFetchResponse)
        .then(data => {
            switch (data) {
                case 0:
                    console.log("cập nhật số lượng")
                    break;
                case 1:
                    showErrors("Số lượng sản phẩm ko đủ");
                    break;
                case 2: {
                    console.log("Show alert")
                    Swal.fire({
                        title: 'Bớt sản phẩm này thì giá tối thiểu của voucher sẽ không đạt',
                        text: 'Bạn có chắc muốn bớt không?',
                        icon: 'warning',
                        showCancelButton: true,
                        confirmButtonText: 'Ok',
                        cancelButtonText: 'Hủy'
                    }).then(result => {
                        if (result.isConfirmed) {// nhấn ok thì xóa voucher
                            removeVoucherInCart(currentCartId);
                        } else {// nhấn hủy thì cập nhật lại số lượng
                            if (status) {// nếu có thì cộng lại số lượng
                                document.getElementById(idInputCart).value = oldQuantity;
                            }
                            updateQuantityProductInCart(idInputCart, idCartDetail, false);
                        }
                    });
                    break;
                }
            }
            loadProductsInCart(currentCartId);
        })
        .catch(handleError);
}

let oldQuantity = 0

function setOldQuantity(idInputCart) {
    oldQuantity = parseInt(document.getElementById(idInputCart).value, 10);
}

var totalPrice = 0;

// Load products in cart
function loadProductsInCart(cartId) {
    totalPrice = 0;
    fetch(`${apiUrls.getProductsInCart}?idCart=${cartId}`)
        .then(handleFetchResponse)
        .then(cartDetails => {
            if (cartDetails.length !== 0) {
                if (Array.isArray(cartDetails)) {
                    const itemsContainer = document.getElementById(`cart-${cartId}-items`);
                    itemsContainer.innerHTML = '';
                    totalPrice = 0;
                    cartDetails.forEach(cartDetail => {
                        totalPrice += cartDetail.price * cartDetail.quantity;
                        const row = `
                        <tr>
                            <td>${cartDetail.nameProduct}</td>
                            <td>
                                ${cartDetail.oldPrice === 0
                            ? `${cartDetail.price.toLocaleString()} đ`
                            : `${cartDetail.price.toLocaleString()} <del class="discount-price">${cartDetail.oldPrice.toLocaleString()}</del> đ <br>
                                <div style="font-size: 12px;color: #6c757d;">${cartDetail.namePromotion}</div>`}
                            </td>
                            <td><input type="number" min="1" class="form-control" id="input-quantity-${cartDetail.id}"}  style="width: 100px" value="${cartDetail.quantity}" onfocus="setOldQuantity('input-quantity-${cartDetail.id}')" ${!cartDetail.isEditQuantity ? 'disabled' : ''}  onchange="updateQuantityProductInCart('input-quantity-${cartDetail.id}',${cartDetail.id},true)"></td>
                            <td style="font-size: 20px">${(cartDetail.price * cartDetail.quantity).toLocaleString()} đ</td>
                            <td><button class="btn" onclick="removeProductInCart(${cartDetail.id},${(cartDetail.price * cartDetail.quantity)})"><img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAAXNSR0IArs4c6QAAAKRJREFUSEvtlUEKgCAQRV93CYK6TsdpGXSZrlPQaYpAXVjDt8xd7mR03v8z6FQUXlXh/ChAD4xAYwjZgAGYLaEKsACtcLkC3VvA7i5aQlRclkglUPELwF/I7X1wHFsvDvDKpfXIonn+dfO+BsQK1T7wUx2ohNkl+gGXd/W0JH8P5NeUXSJFeAxIGTQx9HbwWC/5HJUTUCvpLm6OTjUyE/Pbx4oDDlBhOBmYaWrOAAAAAElFTkSuQmCC"/></button></td>
                        </tr>`;
                        itemsContainer.insertAdjacentHTML('beforeend', row);
                    });
                    showPrice();
                }
            } else {
                showPrice();
                const itemsContainer = document.getElementById(`cart-${cartId}-items`);
                itemsContainer.innerHTML = '';
                itemsContainer.insertAdjacentHTML('beforeend', '<label style="margin-top: 85px; margin-left: 529px;"><div style="font-size: 15px">Chưa có sản phẩm</div></label>');
            }
        })
        .catch(handleError);
}

let totalPriceo;

function showPrice() {
    countShipPrice();
    countReducedPrice();
    coculateMoneyInput();
    totalPriceo = totalPrice + shipPrice - reducedPrice - oldTotalPrice;
    if (totalPriceo > 0) {
        document.getElementById('returnPrice').innerHTML = 'Thanh toán'
    } else {
        document.getElementById('returnPrice').innerHTML = 'Trả khách'
    }
    document.getElementById('totalPrice').innerHTML = totalPrice.toLocaleString() + ' đ';
    document.getElementById('reducedPrice').innerHTML = '- ' + reducedPrice.toLocaleString() + ' đ';
    document.getElementById('payPriceOld').innerHTML = '- ' + oldTotalPrice.toLocaleString() + ' đ';
    document.getElementById('payPriceOldx').innerHTML = '- ' + oldTotalPricex.toLocaleString() + ' đ';
    document.getElementById('payPrice').innerHTML = (totalPriceo >= 0 ? totalPriceo : -totalPriceo).toLocaleString() + ' đ';
    document.getElementById('viewTotalPricePay').innerHTML = 'Số tiền: ' + totalPriceo.toLocaleString() + ' đ';
    document.getElementById('QRPay').src = 'https://img.vietqr.io/image/mbbank-400290412004-s.png?amount=' + totalPriceo + '&addInfo= Chuyển tiền thanh toán QR CODE &accountName=Cao Thanh Tùng';
}

// Set active tab
function handleClickCart(cartId) {
    paymentMethods = -1;
    document.querySelectorAll('.nav-link').forEach(tab => {
        tab.classList.remove('active');
        tab.setAttribute('aria-selected', 'false');
    });
    currentCartId = cartId;
    document.querySelectorAll('.tab-pane').forEach(panel => {
        panel.classList.remove('show', 'active');
    });

    const activeTab = document.getElementById(`pills-home-tab-${cartId}`);
    const activePanel = document.getElementById(`pills-home-${cartId}`);
    if (activeTab && activePanel) {
        activeTab.classList.add('active');
        activeTab.setAttribute('aria-selected', 'true');
        activePanel.classList.add('show', 'active');
    }
    setCurrentCartId(cartId);
    loadProductsInCart(currentCartId);
    getCart();
}

function setDefaultForm() {
    document.getElementById('popupSelectAddress').style.display = 'none';
    document.getElementById('showSaveAddress').style.display = 'none';
    document.getElementById('hideDelivery').checked = false;
    delivery.style.display = "none";
    document.getElementById('nameCustomerInCart').innerHTML = 'Khách lẻ';
    document.getElementById('describeCustomerAddress').value = '';
    document.getElementById('describeCustomerBill').value = '';
    document.getElementById('popupSelectCustomer').innerHTML = "Chọn khách hàng\n" +
        "                                <img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAAXNSR0IArs4c6QAAAchJREFUSEu1lTtPVUEUhb8VaxOJRo1EGwosrfgBxhcaSyFW/AITgiRYGeiESLSwpwUt1Uh4WdraGQsaDChGo1FLyPLsZCDXwx3mesidds5e336s2Ud0+ajL+nQEsH27SmQS6EsJrQMPJb0uJVgE2J4AHmWExiXNHgY5FGD7OrAIfAECtJTErgHTwFngiqTVHKQEWA4BYFjS81YR20PAAvBG0s2mgB/ACeC4pD81wEngG7AlqbcpYBs4DZyS9D0D2JYUrWp7Si2Knl8FhiS9yLRoUdJgU0DrkMeBmMmxBJ0BzhxpyJGV7TEgxEK49ewCYdOnjW26F2i7H7gPhD13gLVq8LOSPh75oZUESvfZIdsOe0bWA8Cl5KZWva/Ae+BdquZ3O1hbgO1wxVwaYinJuN8CRiSt1D8+ALB9C3iVPnwJTAHrkn7WbBoVxvKL+4iJMygpVsv++Qdgu6daCx9S5tOSHnSSvu3HqZ2fgYuSfu3F1QGjwBPgraTLnYgnK4dOPMrYW/ckPcsBYqHdKT2edmDbN2LxVRXMV/a9mwNsAOeBnnrPS9XYPgdsAp8kXcgBHBeSij+iTBUH4hsJlarJuuh/Ajv9tusV/AXpWpsZNntrxAAAAABJRU5ErkJggg==\"/>\n";
    document.getElementById('totalPrice').innerHTML = '0 đ';
    document.getElementById('payPrice').innerHTML = '0 đ';
    document.getElementById('shipPrice').innerHTML = '0 đ';
    document.getElementById('viewTotalPricePay').innerHTML = 'Số tiền: 0 đ';
    document.getElementById('QRPay').src = 'https://img.vietqr.io/image/mbbank-400290412004-s.png?amount=0&addInfo= Chuyển tiền thanh toán QR CODE &accountName=Cao Thanh Tùng';
    showAddress(null);
}

var reducedPrice = 0;
var oldTotalPrice = 0;
var oldTotalPricex = 0;

function getCart() {
    fetch(`${apiUrls.getCart}?idCart=${currentCartId}`, {method: 'GET'}) // Đổi thành POST
        .then(handleFetchResponse)
        .then(data => {
            if (data.billEdit !== null) {
                document.getElementById('oldTotalPrice').style.display = "block";
                document.getElementById('oldTotalPricex').style.display = "block";
                document.getElementById('clickPay').innerHTML = "Lưu hóa đơn"
                document.getElementById('paymenMethor').style.display = "none";
                document.getElementById('describeCustomerAddress').value = data.descriptionAddress || '';
                document.getElementById('describeCustomerBill').value = data.descriptionBill || '';
                isPaymentMethod = false;
                oldTotalPrice = data.billEdit.totalPrice + data.billEdit.shipPrice - data.billEdit.tienGiam;
                oldTotalPricex = data.billEdit.totalPrice
            } else {
                document.getElementById('oldTotalPrice').style.display = "none";
                document.getElementById('oldTotalPricex').style.display = "none";
                document.getElementById('clickPay').innerHTML = "Xác nhận thanh toán";
                isPaymentMethod = true
                document.getElementById('paymenMethor').style.display = "block";
                oldTotalPrice = 0.;
                oldTotalPricex = 0.;
            }
            if (data.customer === null) {
                idCustomer = 0;
                document.getElementById('popupSelectAddress').style.display = 'none';
                document.getElementById('showSaveAddress').style.display = 'none';
            } else {
                idCustomer = data.customer.id;
                document.getElementById('popupSelectAddress').style.display = 'block'
                document.getElementById('showSaveAddress').style.display = 'block';

            }
            document.getElementById('hideDelivery').checked = !!data.ship;
            document.getElementById('inputMoney').value = null;
            document.getElementById('excessMoney').innerHTML = "0 đ"
            if (data.ship) {
                delivery.style.display = "block";
                document.getElementById('hideDelivery').checked = true;
            } else {
                document.getElementById('hideDelivery').checked = false;
                delivery.style.display = "none";
            }
            if (data.customer !== null) {
                document.getElementById('nameCustomerInCart').innerHTML = data.customer.name;
                document.getElementById('btnRemoveCustomer').style.display = 'block';
                document.getElementById('popupSelectCustomer').innerHTML = "Thay đổi <img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAAXNSR0IArs4c6QAAAbZJREFUSEu1lM+LT2EUh5+nDAtFycLOYrJSYjdKwx9gsrQiSywJMzGU/Eph5V9gY8GUpSLsjcTWTrMQNTU1Q457dK/u3L4/3i/zfTe37vuez/OezznnlTEvx6zPyICIuK3OlV5sJECKA7NqcVzxwUY8b77hgIi4Dsy3bHkDPAAW1J+D7CrKoAeg0fwCnFBf9IP0BUTEKWBS/XPzDmQSuACcBn4Bx9UnvSA9ARGxC/gMbAEOqO9qyA3gclODiDgLPAS+AnvUb11IP8AtIFsxPT7WDspM1KvNv4h4CuSZ/H+tFPAB2AtMq68HFTEipoFXwKK6vxTwA9gEbFVXhgC2A9+BZXVbKWANmBgnYBHYV2jRTNYK+KimretWvyKfA+4Bz4GZqnhRd9FF9W6rwBn/FjgI3FfPlwJ2ADlEm4HHwMmc2IhI0BX1Zg08DLysOm4V2K0uFQHq4KNADk/OwifgTC2W23Pqnfpcft+rj4oHrWXBoWrQngGZUXfNqzl4A9fQtygidlYtmN5OVRkcaaltDKBT0Hx31ln03xn0APz1f5h47g+1qAO41BS3RHwkQKlgcZv+q2A37jcAGK0Zon5JNAAAAABJRU5ErkJggg==\"/>";
            } else {
                document.getElementById('nameCustomerInCart').innerHTML = 'Khách lẻ';
                document.getElementById('btnRemoveCustomer').style.display = 'none';
                document.getElementById('popupSelectCustomer').innerHTML = "Chọn khách hàng\n" +
                    "                                <img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAAXNSR0IArs4c6QAAAchJREFUSEu1lTtPVUEUhb8VaxOJRo1EGwosrfgBxhcaSyFW/AITgiRYGeiESLSwpwUt1Uh4WdraGQsaDChGo1FLyPLsZCDXwx3mesidds5e336s2Ud0+ajL+nQEsH27SmQS6EsJrQMPJb0uJVgE2J4AHmWExiXNHgY5FGD7OrAIfAECtJTErgHTwFngiqTVHKQEWA4BYFjS81YR20PAAvBG0s2mgB/ACeC4pD81wEngG7AlqbcpYBs4DZyS9D0D2JYUrWp7Si2Knl8FhiS9yLRoUdJgU0DrkMeBmMmxBJ0BzhxpyJGV7TEgxEK49ewCYdOnjW26F2i7H7gPhD13gLVq8LOSPh75oZUESvfZIdsOe0bWA8Cl5KZWva/Ae+BdquZ3O1hbgO1wxVwaYinJuN8CRiSt1D8+ALB9C3iVPnwJTAHrkn7WbBoVxvKL+4iJMygpVsv++Qdgu6daCx9S5tOSHnSSvu3HqZ2fgYuSfu3F1QGjwBPgraTLnYgnK4dOPMrYW/ckPcsBYqHdKT2edmDbN2LxVRXMV/a9mwNsAOeBnnrPS9XYPgdsAp8kXcgBHBeSij+iTBUH4hsJlarJuuh/Ajv9tusV/AXpWpsZNntrxAAAAABJRU5ErkJggg==\"/>\n";

            }
            voucher = data.voucher;
            if (voucher != null) {
                document.getElementById('popupSelectVoucher').innerHTML = "Hủy mã";
                document.getElementById('popupSelectVoucher').onclick = () => removeVoucherInCart(currentCartId);
                document.getElementById('popupSelectVoucher').setAttribute('data-bs-target', '#');
            } else {
                document.getElementById('popupSelectVoucher').innerHTML = "Chọn mã";
                document.getElementById('popupSelectVoucher').onclick = () => renderVoucher();
                document.getElementById('popupSelectVoucher').setAttribute('data-bs-target', '#myModalVoucher');
            }
            showAddress(data.address)
            countReducedPrice()
            setTimeout(showPrice, 500);
        })
        .catch(handleError);
}

let voucher;

function countReducedPrice() {
    const reduced = document.getElementById('reducedPrice');
    const codeVoucher = document.getElementById('codeVoucher');
    if (voucher !== null) {
        if (voucher.styleVoucher) {//giảm theo phần trăm
            reducedPrice = voucher.discount * totalPrice / 100;
            if (reducedPrice > voucher.maximumReduction) {
                reducedPrice = voucher.maximumReduction;
            }
        } else {//Giảm theo tiền
            reducedPrice = voucher.discount;
            if (totalPrice < reducedPrice) {
                reducedPrice = totalPrice;
            }
        }
        codeVoucher.value = voucher.code
        reduced.innerHTML = reducedPrice.toLocaleString() + ' đ'
    } else {
        reducedPrice = 0;
        reduced.innerHTML = '0 đ'
        codeVoucher.value = null
    }
}

// Handle errors
function handleError(error) {
    console.error('There was a problem with the fetch operation:', error.message);
}

// Initialize events on DOMContentLoaded
document.addEventListener("DOMContentLoaded", () => {
    loadCarts();
    document.getElementById("create-cart").addEventListener("click", createCart);

    // Add event listeners for filtering products
    const filterSelects = ['styleSelect', 'materialSelect', 'nsxSelect', 'categorySelect', 'sizeSelect', 'colorSelect'];
    filterSelects.forEach(selectId => {
        document.getElementById(selectId).addEventListener('change', filterProducts);
    });
    searchProduct.addEventListener('input', filterProducts);
});
let currentCartId = null; // Biến lưu ID giỏ hàng hiện tại


document.getElementById("quantityAddProduct").addEventListener("keypress", function (evt) {
    if (evt.keyCode === 13) { // Kiểm tra mã phím Enter (13)
        document.getElementById("payButton").click();
    }
});
document.getElementById('myModalInputQuantityProduct').addEventListener('shown.bs.modal', function () {
    document.getElementById("quantityAddProduct").focus();
});

// Cập nhật hàm `addToCart` để thêm sản phẩm vào giỏ hàng hiện tại
function addToCart() {
    let quantity = document.getElementById("quantityAddProduct").value.trim();
    document.getElementById("quantityAddProduct").value = "";
    document.getElementById("quantityAddProduct").value = "";
    if (quantity.length === 0) {
        quantity = 1;
    } else {
        const isNumeric = /^[0-9]+$/.test(quantity);
        if (!isNumeric) {
            showErrors("Hãy nhập ký tự số. Bạn đang nhập sai kiểu của số.");
            return;
        }

        try {
            quantity = parseInt(quantity);
            if (quantity <= 0) {
                showErrors("Số lượng phải lớn hơn hoặc bằng 1.");
                return;
            }
        } catch (err) {
            console.log(err);
            showErrors("Đã xảy ra lỗi khi xử lý số lượng.");
            return;
        }
    }
    if (carts.length !== 0) {
        fetch(`${apiUrls.addToCart}?idCart=${currentCartId}&idProduct=${idProductAdd}&quantity=${quantity}`, {method: 'POST'}) // Đổi thành POST
            .then(handleFetchResponse)
            .then(data => {
                if (data === 0) {
                    loadProductsInCart(currentCartId);
                }
                if (data === 1) {
                    showErrors('Số lượng sản phẩm không đủ')
                }
                if (data === 2) {
                    showErrors('QR không hợp lệ')
                }
            })
            .catch(handleError);
    } else {
        showErrors("Vui lòng chọn/tạo giỏ hàng trước.");
    }
}


// Khi mở popup sản phẩm, thiết lập `currentCartId` thành giỏ hàng hiện tại
popupSelectProduct.addEventListener("click", function () {
    fetchData(apiUrls.getProducts, renderProducts);
    fetchData(apiUrls.getAttributeProduct, renderAttributes);
    setCurrentCartId(currentCartId)
});

function setCurrentCartId(cartId) {
    currentCartId = cartId;
    if (currentCartId != null) {
        fetch(`${apiUrls.setIdCartSelect}?cartId_select=${currentCartId}`, {method: 'POST'}) // Đổi thành POST
            .then(handleFetchResponse)
            .then(data => {
                if (!data) {
                    showErrors('Không lấy được id select.')
                }
            })
            .catch(handleError);
    }
}

function getCurrentCartId() {
    fetch(`${apiUrls.getIdCartSelect}`, {method: 'GET'}) // Đổi thành POST
        .then(handleFetchResponse)
        .then(data => {
            currentCartId = data;
        })
        .catch(handleError);
}


function renderCustomer() {
    var keyWordSearchCustomer = document.getElementById('keyWordSearchCustomer').value;
    fetch(`${apiUrls.getCustomers}?keyWord=${keyWordSearchCustomer}`)
        .then(handleFetchResponse)
        .then(function (customers) {
            const tbodyCustomer = document.getElementById('tbodyCustomer');
            tbodyCustomer.innerHTML = '';
            customers.forEach((customer, index) => {
                const row = `
                            <tr>
                                <th scope="row">${index + 1}</th>
                                <td>${customer.name}</td>
                                <td>${customer.phone}</td>
                                <td>${customer.email}</td>
                                <td>${customer.gender}</td>
                                <td>${customer.dateOfBirth}</td>
                                <td>${customer.address}</td>
                                <td>
                                    <a class="btn" data-bs-dismiss="modal" onclick="addCustomerToCart(${customer.id})">
                                        <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAAXNSR0IArs4c6QAAAYVJREFUSEvN1D9IlVEYx/HPJV0abBAUbIggaAinhnQJhxpdgqAhCMQ/oEIt4qijREtEQZEENQQtLm4FgYOog4tNNRokhYug4KTvI+eFy8u9933pdqVnOXDOc37f53eeh1PT4ah1WN+5Ak6Sm0Ms4/G/cFfvIAfkunfxpV1IoyeaxxJWMdoEkBdT+sSNEnqxhwu4gt0GkLYAofcBD/EU4agYbQOGsY59XMZxgdA2IPS+4QYe4T1eY7JJT95gCi8wi+d4ErmtmhRiIbqFW+jGGoYKkA3cTnt/cAnX8b0McDGr6hd6cBPb6MNOWuP+bwym9UEm/BGb9UWUjVlu+R3GUpXhIJxEROXhIOIz7mACb3OXZYBr+JGaHNUfpIt5L+LtIwbwE0foR/wGZ1EGiJyvGMkuzeFZkyYvYiF9MeP1OVUA9/GpiXBxO8Y7f7LKDrrSRFwtgazgXjGnioOKxTdOqwp4mY3idDaerzBTkGp1VqnJoVf/lReLanVWGdBxB3/dh6o9+H8BpwsdRBk/cucSAAAAAElFTkSuQmCC"/>
                                    </a>
                                </td>
                            </tr>`;

                tbodyCustomer.insertAdjacentHTML('beforeend', row);
            });
        })
        .catch(handleError);

}

function addCustomerToCart(idCustomer) {
    if (carts.length !== 0) {
        fetch(`${apiUrls.setCustomerInCart}?idCart=${currentCartId}&idCustomer=${idCustomer}`, {method: 'PUT'})
            .then(handleFetchResponse)
            .then(data => {
                if (data.name !== null) {
                    showSuccesss("Đã thêm thông tin khách hàng vào giỏ");
                    getCart()
                } else {
                    showErrors("Khách hàng đang có hóa đơn #" + data.id + " chưa hoàn thành!");
                }
            })
            .catch(handleError);
    } else {
        showErrors("Vui lòng chọn/tạo giỏ hàng trước.");
    }
}

function setVoucherInCart(idVoucher) {

    if (carts.length !== 0) {
        fetch(`${apiUrls.setVoucherInCart}?idVoucher=${idVoucher}`, {method: 'PUT'})
            .then(handleFetchResponse)
            .then(data => {
                if (data === 0) {
                    showSuccesss("Đã áp dụng mã giảm giá.");
                    getCart()
                } else {
                    showErrors('Lỗi hệ thông, xin load lại trang và thử lại')
                }
            })
            .catch(handleError);
    } else {
        showErrors("Vui lòng chọn/tạo giỏ hàng trước.");
    }

}

function removeCustomer() {
    fetch(`${apiUrls.removeCustomer}`, {method: 'DELETE'})
        .then(handleFetchResponse)
        .then(data => {
            if (data) {
                getCart()
            } else {
                showErrors('Lỗi hệ thông, xin load lại trang và thử lại')
            }
        })
        .catch(handleError);
}

function showErrors(messsages) {
    const Toast = Swal.mixin({
        toast: true,
        position: "top-end",
        showConfirmButton: false,
        timer: 2500,
        timerProgressBar: true,
        didOpen: (toast) => {
            toast.onmouseenter = Swal.stopTimer;
            toast.onmouseleave = Swal.resumeTimer;
        }
    });
    Toast.fire({
        icon: "error",
        title: messsages
    });
}

function showSuccesss(messsages1) {
    const Toast = Swal.mixin({
        toast: true,
        position: "top-end",
        showConfirmButton: false,
        timer: 2500,
        timerProgressBar: true,
        didOpen: (toast) => {
            toast.onmouseenter = Swal.stopTimer;
            toast.onmouseleave = Swal.resumeTimer;
        }
    });
    Toast.fire({
        icon: "success",
        title: messsages1
    });
}

function calculateShippingFee() {
    const fromDistrictId = 3440; // ID quận/huyện gửi hàng
    const weight = 500;

    // Sử dụng setInterval để kiểm tra mỗi 500ms xem người dùng đã chọn quận/huyện hay chưa
    const intervalId = setInterval(() => {
        const toDistrictId = document.getElementById('district').value; // ID quận/huyện nhận hàng

        // Nếu người dùng đã chọn quận/huyện, tiếp tục tính phí và dừng vòng lặp
        if (toDistrictId) {
            clearInterval(intervalId); // Dừng vòng lặp

            getServiceId(parseInt(fromDistrictId), parseInt(toDistrictId), weight)
                .then(serviceId => {
                    if (!serviceId) {
                        console.warn('Không tìm thấy mã dịch vụ phù hợp.');
                        return;
                    }
                    return fetch('https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Token': GHN_TOKEN
                        },
                        body: JSON.stringify({
                            "from_district_id": parseInt(fromDistrictId),
                            "service_id": serviceId,

                            "to_district_id": parseInt(toDistrictId),
                            "weight": 500,
                            "length": 20,
                            "width": 15,
                            "height": 5
                        })
                    });
                })
                .then(response => response.json())
                .then(data => {
                    if (data && data.code === 200) {
                        shipPrice = data.data.total;
                        showPrice();
                    } else {
                        console.error('Lỗi tính phí ship:', data.message);
                    }
                })
                .catch(error => console.error('Lỗi tính phí ship:', error));
        } else {
            console.warn('Vui lòng chọn quận/huyện nhận hàng.');
        }
    }, 200); // Kiểm tra mỗi 500ms
}


function countShipPrice() {
    // Kiểm tra nếu không chọn tỉnh hoặc không ẩn giao hàng
    if (!hideDelivery.checked) {
        shipPrice = 0;
    } else {
        setTimeout(calculateShippingFee, 500)
    }
    document.getElementById('shipPrice').innerHTML = shipPrice.toLocaleString() + ' đ';
}

let qrScanner;

// Hàm mở popup và bắt đầu quét
function openScanner() {
    if (carts.length !== 0) {
        if (typeof QrScanner === 'undefined') {
            console.error('QrScanner chưa được tải!');
            return;
        }

        QrScanner.WORKER_PATH = 'https://unpkg.com/qr-scanner/qr-scanner-worker.min.js'; // Đường dẫn đến worker

        document.getElementById("popup").style.display = "block";
        document.getElementById("overlay").style.display = "block";

        const video = document.getElementById('video');
        qrScanner = new QrScanner(video, result => {
            const id = parseInt(result);
            if (id > 2147483647) {
                showErrors("QR không hợp lệ.");
            } else {
                idProductAdd = id;
                addToCart();
            }
            closeScanner(); // Đóng popup sau khi quét
        });

        qrScanner.start(); // Bắt đầu quét
    } else {
        showErrors("Vui lòng chọn/tạo giỏ hàng trước.");
    }
}

// Hàm đóng popup và dừng quét
function closeScanner() {
    if (qrScanner) {
        qrScanner.stop(); // Dừng camera
        qrScanner.destroy(); // Giải phóng đối tượng để dùng lại
        qrScanner = null;
    }
    document.getElementById("popup").style.display = "none";
    document.getElementById("overlay").style.display = "none";
}

function coculateMoneyInput() {
    var inputMoney = document.getElementById('inputMoney').value;
    var excessMoney = document.getElementById('excessMoney');
    var excessMoneyPrice = inputMoney - (totalPrice - reducedPrice + shipPrice - oldTotalPrice);
    if (totalPrice === 0) {
        document.getElementById('excessMoneyText').innerHTML = ""
        excessMoney.innerHTML = "Thêm sản phẩm";
        return;
    }
    if (excessMoneyPrice < 0) {
        document.getElementById('excessMoneyText').innerHTML = "Còn thiếu"
        excessMoney.innerHTML = (-1 * excessMoneyPrice).toLocaleString() + " đ";
    } else {
        if (excessMoneyPrice === 0) {
            document.getElementById('excessMoneyText').innerHTML = ""
            excessMoney.innerHTML = "Đã đủ";
        } else {
            document.getElementById('excessMoneyText').innerHTML = "Trả khách"
            excessMoney.innerHTML = excessMoneyPrice.toLocaleString() + " đ";
        }
    }
}

const {jsPDF} = window.jspdf;

async function exportToPDF(id) {
    const billResponse = await fetch(`http://localhost:8080/api/bill/get-bill?idBill=` + id);
    const bill = await billResponse.json();

    const productResponse = await fetch(`http://localhost:8080/api/bill/get-product-in-bill?idBill=` + id);
    const products = await productResponse.json();

    const date = new Date(bill.dateCreate);
    const formattedDate = date.toLocaleString('vi-VN', {
        year: 'numeric', month: 'long', day: 'numeric',
        hour: '2-digit', minute: '2-digit',
    });

    const tableBody = [
        ['Sản phẩm', 'Giá', 'Số lượng', 'Thành tiền'],
        ...products.map(p => [p.nameProduct, p.price.toLocaleString(), p.quantity, p.intoMoney.toLocaleString()])
    ];

    const docDefinition = {
        content: [

            {text: 'F6 SHOP', fontSize: 18, bold: true, alignment: 'left', margin: [0, 0, 0, 10]}, {
                text: [
                    {text: 'Địa chỉ: ', bold: true}, '123 Đường ABC, Quận XYZ\n',
                    {text: 'Số điện thoại: ', bold: true}, '0123456789 | ',
                    {text: 'Email: ', bold: true}, 'info@f6shop.com | ',
                    {text: 'Website: ', bold: true}, 'www.f6shop.com'
                ],
                alignment: 'right',
                fontSize: 10,
                margin: [0, 0, 0, 30]
            },
            {text: `HÓA ĐƠN #${bill.id}`, fontSize: 16, bold: true, alignment: 'center', margin: [0, 0, 0, 20]},

            // Thông tin khách hàng và nhân viên
            {
                columns: [
                    {
                        text: [
                            {text: 'ID Nhân viên: ', bold: true}, `${bill.employee?.id || ''}\n`,
                            {text: 'Nhân viên: ', bold: true}, `${bill.employee?.name || ''}\n`,
                            {text: 'Ngày mua: ', bold: true}, `${formattedDate}\n`,
                            {text: 'Nơi mua: ', bold: true}, `${bill.paymentType ? 'Trực tuyến' : 'Tại cửa hàng'}`
                        ]
                    },
                    {
                        text: [
                            {text: 'ID Khách hàng: ', bold: true}, `${bill.customer?.id || ''}\n`,
                            {text: 'Khách hàng: ', bold: true}, `${bill.customer?.name || 'Khách lẻ'}\n`,
                            {text: 'Số điện thoại: ', bold: true}, `${bill.customer?.phone || ''}`
                        ]
                    }
                ],
                columnGap: 30,
                margin: [0, 0, 0, 10]
            },

            // Bảng sản phẩm
            {
                table: {
                    headerRows: 1,
                    widths: ['*', 'auto', 'auto', 'auto'],
                    body: tableBody
                },
                layout: 'lightHorizontalLines', // Tùy chọn viền nhẹ
                margin: [0, 10, 0, 20]
            }, {
                columns: [
                    {
                        text: [
                            {text: 'Người nhận: ', bold: true}, `${bill.address?.nameRecipient || 'N/A'}\n`,
                            {text: 'Số điện thoại: ', bold: true}, `${bill.address?.phone || 'N/A'}\n`,
                            {text: 'Địa chỉ: ', bold: true},
                            `${bill.address?.addreseDetail || ''}, ${bill.address?.ward || ''}, ${bill.address?.district || ''}, ${bill.address?.province || ''}\n`,
                            {text: 'Ghi chú giao hàng: ', bold: true}, `${bill.descriptionShip || 'N/A'}\n\n`,
                            {text: 'Ghi chú hóa đơn: ', bold: true}, `${bill.descriptionBill || 'N/A'}`
                        ]
                    }
                ],
                margin: [0, 0, 20, 30] // Thêm khoảng cách dưới 20px để cách với phần tiếp theo
            },
// Thông tin thanh toán chia 2 cột 4 hàng không viền
            {
                columns: [
                    [
                        {text: 'Tổng tiền: ', bold: true},
                        {text: 'Giảm giá: ', bold: true},
                        {text: 'Phí vận chuyển: ', bold: true},
                        {text: 'Thanh toán: ', bold: true}
                    ],
                    [
                        {text: `${bill.totalPrice.toLocaleString()} VND`},
                        {text: `-${bill.tienGiam?.toLocaleString() || '0'} VND`},
                        {text: `${bill.shipPrice?.toLocaleString() || '0'} VND`},
                        {text: `${(bill.totalPrice + bill.shipPrice - (bill.tienGiam || 0)).toLocaleString()} VND`}
                    ]
                ],
                columnGap: 50,
                margin: [30, 0, 0, 30]
            }
            // Thông tin shop ở cuối trang

        ]
    };

    pdfMake.createPdf(docDefinition).open();
}

function validateAge(birthDay) {
    const today = new Date(); // Ngày hiện tại
    const birthDate = new Date(birthDay); // Chuyển đổi chuỗi ngày sinh thành Date
    const age = today.getFullYear() - birthDate.getFullYear(); // Tính tuổi cơ bản
    const monthDifference = today.getMonth() - birthDate.getMonth(); // Tính chênh lệch tháng

    // Điều chỉnh tuổi nếu chưa đến sinh nhật năm nay
    if (monthDifference < 0 || (monthDifference === 0 && today.getDate() < birthDate.getDate())) {
        return age - 1;
    }
    return age;
}

// Kiểm tra nếu nhỏ hơn 18 tuổi
function isValidBirthDay(birthDay) {
    return validateAge(birthDay) < 14;
}

function saveNewCustomer() {
    let nameCustomerAdd = document.getElementById("nameCustomerAdd").value;
    let phoneCustomerAdd = document.getElementById("phoneCustomerAdd").value;
    let emailCustomerAdd = document.getElementById("emailCustomerAdd").value;
    let selectedValue = document.querySelector('input[name="genderCustomerAdd"]:checked')?.value;
    let birthDayCustomerAdd = document.getElementById("birthDayCustomerAdd").value;
    if (nameCustomerAdd.length === 0) {
        showErrors("Nhập tên khách hàng");
        return;
    }
    if (phoneCustomerAdd.length === 0) {
        showErrors("Nhập số điện thoại khách hàng");
        return;
    }
    if (phoneCustomerAdd.length !== 10) {
        showErrors("Nhập số điện thoại có 10 số.");
        return;
    }
    if (emailCustomerAdd.length === 0) {
        showErrors("Nhập E-Mail khách hàng");
        return;
    }
    if (!selectedValue) {
        showErrors("Chọn giới tính khách hàng");
        return;
    }
    if (birthDayCustomerAdd.length === 0) {
        showErrors("Chọn sinh nhật khách hàng");
        return;
    }
    if (isValidBirthDay(birthDayCustomerAdd)) {
        showErrors("Độ tuổi phải lơn hơn 14")
        return;
    }
    const customerData = {
        name: nameCustomerAdd,
        phone: phoneCustomerAdd,
        email: emailCustomerAdd,
        gender: selectedValue, // hoặc "Female"
        birthDay: birthDayCustomerAdd // Định dạng yyyy-MM-dd
    };

// Hàm gửi request
    fetch('http://localhost:8080/api/sale/create-customer', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Token': GHN_TOKEN
        },
        body: JSON.stringify(customerData)
    })
        .then(response => response.json())
        .then(data => {
            if (data === -1) {
                showErrors("Không thể tạo giỏ, vui lòng tải lại trang và thử lại")
            } else {
                showSuccesss("Tạo và áp dụng khách hàng");
                getCart()
                document.getElementById('closePopup').click()
            }
        })
        .catch(error => {
            console.error('Lỗi khi lấy service_id:', error);
        });
}