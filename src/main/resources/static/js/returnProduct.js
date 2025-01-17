const bill = JSON.parse(sessionStorage.getItem("bill"));
const listProductDetails = document.getElementById("listProductDetail");
const listProductTrade = document.getElementById("listProductTrade");

function showProductTrade() {
    const show = document.getElementById("product-trade");
    if (show.style.display === "none") {
        show.style.display = "block";
    } else {
        show.style.display = "none";
    }
}

function loading() {
    loadProductDetailOfBill(bill.id);
    showProductTradeFromTrade(bill.id);
    loadCustomer(bill);
    loadPrice();
    renderBank();
    renderBankInfo();
    // checkReturn();

}

function renderBank() {
    $.ajax({
        url: "https://api.vietqr.io/v2/banks",
        method: "GET",
        success: function (res) {
            console.log(res.data)
            let select = document.getElementById('bankLists');
            select.innerHTML = ``;
            res.data.forEach(item => {
                let option = document.createElement('option');
                option.setAttribute("data-id", item.id);
                option.value = `${item.shortName} - ${item.name}`;
                select.appendChild(option);
            });
        }
    })
}

function loadCustomer(bill) {
    const customerInfo = document.getElementById("customer-info");

    let phone = "Tại quầy";
    let address = "Tại quầy";
    let description = "Tại quầy";
    if (bill.customer === null) {
        bill.customer = "Khách lẻ";
    } else {
        bill.customer = bill.customer.name;
    }
    if (bill.address !== null) {
        phone = bill.address.phone;
        address = `${bill.address.addreseDetail}, ${bill.address.ward}, ${bill.address.district}, ${bill.address.province}`;
        description = bill.description;
    }

    if (bill.paymentType === false) {
        bill.paymentType = "Tại quầy";
    } else {
        bill.paymentType = "Online";
    }

    customerInfo.innerHTML = `
    <div class="col-6 row">
              <div class="col-4">
                <p>Tên khách hàng:</p>
              </div>
              <div class="col-8">
                <p>${bill.customer}</p>
              </div>
            </div>
            <div class="col-6 row">
              <div class="col-4">
                <p>Ngày yêu cầu:</p>
              </div>
              <div class="col-8">
                <p id="requestDate"></p>
              </div>
            </div>
            <div class="col-6 row">
              <div class="col-4">
                <p>Số điện thoại:</p>
              </div>
              <div class="col-8">
                <p>${phone}</p>
              </div>
            </div>
            <div class="col-6 row">
              <div class="col-4">
                <p>Ngày hoàn thành:</p>
              </div>
              <div class="col-8">
                <p id="responseDate"></p>                                                                   
              </div>
            </div>
            <div class="col-6 row">
              <div class="col-4">
                <p>Địa chỉ:</p>
              </div>
              <div class="col-8">
                <p>${address}</p>
              </div>
            </div>
            <div class="col-6 row">
              <div class="col-4">
                <p>Trạng thái:</p>
              </div>
              <div class="col-8">
                <button class="btn btn-primary rounded-pill" style="margin-top: -8px;">Thành
                  công</button>
              </div>
            </div>
            <div class="col-6 row">
              <div class="col-4">
                <p>Ghi chú:</p>
              </div>
              <div class="col-8">
                <p>${description}</p>
              </div>
            </div>
            <div class="col-6 row">
              <div class="col-4">
                <p>Loại:</p>
              </div>
              <div class="col-8">
                <button class="btn btn-primary rounded-pill"
                        style="margin-top: -6px;">${bill.paymentType}</button>
              </div>
            </div>
    `;
}

function loadProductDetailOfBill(idBill) {
    $.ajax({
        url: "/api/returnProduct/bill/billDetail/" + idBill,
        method: "GET",
        success: function (data) {
            listProductDetails.innerHTML = ``;
            data.forEach(item => renderProduct(item));
            // checkReturn();
        },
        error: function (error) {
            console.log(error)
        }
    })

}

let id = -1;

function getIdBillDetail(idBillDetail) {
    id = idBillDetail;
}

function resetQuantity() {
    document.getElementById("quantity").value = "";
    document.getElementById("errorQuantity").innerText = "";
}

const quantity = document.getElementById("quantity");
const btnSubmit = document.getElementById("btnAddProductDetailToListTrade");

function checkQuantity() {
    const error = document.getElementById("errorQuantity");
    if (quantity.value.trim() === "") {
        error.innerText = "";
        btnSubmit.disabled = true;
    } else if (quantity.value.trim() <= 0) {
        error.innerText = "Số lượng phải lớn hơn 0";
        btnSubmit.disabled = true;
    } else {
        $.ajax({
            url: "/api/returnProduct/checkQuantity/" + id + "?quantity=" + quantity.value,
            method: "GET",
            success: function (data) {
                if (data !== true) {
                    error.innerText = "Số lượng không hợp lệ";
                    btnSubmit.disabled = true;
                } else {
                    error.innerText = "";
                    btnSubmit.disabled = false;
                }
            },
            error: function (error) {
                console.log(error)
            }
        })
    }
}

function checkQuantityTrade(idTradeProduct) {
    console.log(idTradeProduct)
    const quantityTrade = document.getElementById("quantityTrade-" + idTradeProduct);
    const btnSubmitTrade = document.getElementById("btnSubmitTrade");
    console.log(quantityTrade.value)

    if (quantityTrade.value.trim() === "") {
        showToast("Không được bỏ trống số lượng", "info");
        btnSubmitTrade.disabled = true;
    } else {
        if (quantityTrade.value < 1) {
            showToast("Số lượng phải lớn hơn 1", "info");
            quantityTrade.value = 1;
        }
        $.ajax({
            url: "/api/returnProduct/checkQuantityTradeProduct/" + idTradeProduct + "?quantityTrade=" + quantityTrade.value,
            method: "GET",
            success: function (data) {
                console.log(data)

                if (data === "") {
                    showToast("Số lượng không hợp lệ", "info")
                    btnSubmitTrade.disabled = true;
                } else {
                    btnSubmitTrade.disabled = false;
                    const totalPriceOfTrade = document.getElementById("totalPriceOfTrade");
                    totalPriceOfTrade.innerText = data.totalMoney.toLocaleString() + ' VND';
                    loadPrice();
                }
            },
            error: function (error) {
                console.log(error)
            }
        })
    }
}

function addBillDetailToTrade() {
    $.ajax({
        url: "/api/returnProduct/newTradeProduct/" + id + "?quantity=" + quantity.value,
        method: "POST",
        success: function (res) {
            const row = document.getElementById(`return-product-${res.id}`);
            if (row) {
                row.parentNode.remove();
            }
            let options = ``;
            $.ajax({
                url: `/api/returnProduct/getErrorList`,
                method: "GET",
                success: function (data) {
                    data.forEach(item => {
                        options += `<option value="${item.errorName}" statusError="${item.status}">${item.errorName}</option>`
                    })
                    let rowOne = document.createElement('div');
                    rowOne.classList.add('border-0', 'shadow', 'card');
                    rowOne.innerHTML = renderProductTrade(res, options);
                    listProductTrade.append(rowOne);
                },
                error: function (error) {
                    console.log("Error:", error);
                },
                complete: function () {
                    resetQuantity();
                    loadPrice();
                    // checkReturn();
                }
            });
        },
        error: function (error) {
            console.log(error)
        }
    })
}

let i = 0;

function showProductTradeFromTrade(idBill) {
    $.ajax({
        url: "/api/returnProduct/getProductTrade/" + idBill,
        method: "GET",
        success: function (returnLists) {
            listProductTrade.innerHTML = ``;
            console.log(returnLists);
            i = 0;
            let options = ``;
            $.ajax({
                url: `/api/returnProduct/getErrorList`,
                method: "GET",
                success: function (data) {
                    data.forEach(item => {
                        options += `<option value="${item.errorName}" statusError="${item.status}">${item.errorName}</option>`
                    })
                    returnLists.forEach(item => {
                        const row = document.createElement('div');
                        row.classList.add('border-0', 'shadow', 'card');
                        row.innerHTML = renderProductTrade(item, options);
                        listProductTrade.append(row)
                    });
                },
                error: function (error) {
                    console.log("Error:", error);
                }
            });
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function renderProductTrade(item, options) {
    const price = parseInt(item.billDetail.price * item.quantity).toLocaleString();
    item.billDetail.price = item.billDetail.price.toLocaleString();
    let listReturnProducts = ``;
    item.tradeProductItems.forEach(product => {
        listReturnProducts += `<div class="d-flex">
                  <div class="col-4">
                    ${item.billDetail.productDetail.product.name} [${item.billDetail.productDetail.size.name}-${item.billDetail.productDetail.color.name}] <sup>${item.billDetail.price}<sup>đ</sup></sup> 
                  </div>
                  <div class="col-6 text-end">
                    <select style="border: none" class="w-100" oninput="popupOtherError(this)" name="" id="" data-id="${product.id}">
                        ${options}
                        <option value="" style="display: none" id="option-${product.id}"></option>
                        <option value="other">Khác</option>
                    </select>
                  </div>
                  <div class="col-2 text-center d-flex justify-content-center">
                    <p class="col-3" style="cursor: pointer" onclick="deleteTradeProductItem(${product.id}, this)"><i class="fa-solid fa-trash"></i></p>
                  </div>
              </div>`;
    })

    return `<div class="card-body" id="return-product-${item.id}">
              <div class="row d-flex">
                <h5 class="col-10">${item.billDetail.productDetail.product.name} [ ${item.billDetail.productDetail.size.name} - ${item.billDetail.productDetail.color.name} ] <sup id="quantityReturn-${item.id}">Số lượng trả: ${item.quantity}</sup></h5>
                <div class="col-2 d-flex justify-content-end">
                <p style="cursor: pointer; margin-top: -5px;"><i style="height: 20px;width: 20px;" class="fa-solid fa-note-sticky" onclick="popupSelectError(${item.id},'${item.billDetail.productDetail.product.name} [ ${item.billDetail.productDetail.size.name} - ${item.billDetail.productDetail.color.name} ]')"></i></p>
                <p class="ms-3" style="cursor: pointer; margin-top: -5px;"><i style="height: 20px;width: 20px;" class="fa-solid fa-circle-xmark" onclick="deleteProductTrade(${item.id})"></i></p>
                </div>
              </div>
              ${listReturnProducts}
            </div>`;
}

function popupOtherError(select) {
    if (select.value === "other") {
        Swal.fire({
            title: 'Chọn lý do trả hàng khác',
            html: `<input id="otherError" class="w-100" type="text" placeholder="Lý do trả hàng"> 
                   <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" name="inlineRadioOptions" id="inlineRadio1" value="true" checked>
                    <label class="form-check-label" for="inlineRadio1">Tiếp tục bán</label>
                   </div>
                   <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" name="inlineRadioOptions" id="inlineRadio2" value="false">
                    <label class="form-check-label" for="inlineRadio2">Chuyển vào kho lỗi</label>
                   </div>`,
            icon: 'warning',
            showCancelButton: true,       // Hiển thị nút "Hủy"
            confirmButtonText: 'Xác nhận', // Nút xác nhận đăng xuất
            cancelButtonText: 'Hủy',      // Nút hủy
            preConfirm: () => {
                const errorInput = document.getElementById('otherError').value;

                if (errorInput.trim().length < 6) {
                    Swal.showValidationMessage('Lý do trả hàng phải có ít nhất 6 ký tự');
                    return false;
                }

                const selectedRadio = document.querySelector('input[name="inlineRadioOptions"]:checked');
                const radioValue = selectedRadio ? selectedRadio.value : null;

                return { errorInput, radioValue };
            }
        }).then((result) => {
            if (result.isConfirmed) {
                const { errorInput, radioValue } = result.value;

                // Lấy và cập nhật giá trị cho phần tử option
                const otherOption = document.getElementById(`option-${select.getAttribute('data-id')}`);
                otherOption.value = errorInput; // Cập nhật giá trị của option
                otherOption.innerText = errorInput;
                otherOption.setAttribute('statusError',radioValue);
                otherOption.style.display = 'block';
                select.value = errorInput;
            }
        });
    }
}

function popupSelectError(id, productName) {
    $.ajax({
        url: `/api/returnProduct/getErrorList`,
        method: "GET",
        success: function (data) {
            let options = ``;
            data.forEach(item => {
                options += `<option value="${item.errorName}">${item.errorName}</option>`
            })
            Swal.fire({
                title: 'Chọn lý do trả hàng chung cho<br>' + productName,
                html: `
                <select style="border: none; border-bottom: 1px solid" class="w-100" id="errorList">
                    ${options}
                </select>`,
                icon: 'warning',
                showCancelButton: true,       // Hiển thị nút "Hủy"
                confirmButtonText: 'Xác nhận', // Nút xác nhận đăng xuất
                cancelButtonText: 'Hủy'        // Nút hủy
            }).then((result) => {
                if (result.isConfirmed) {
                    setSelects(id);
                }
            });
        },
        error: function (error) {
            console.log("Error:", error);
        }
    });

}

function setSelects(id) {
    const selects = document.querySelectorAll(`#return-product-${id} select`);
    const error = document.getElementById("errorList").value;
    console.log(error);
    selects.forEach(select => {
        console.log(select)
        select.value = error;
    })
}

let index = 0;

function renderProduct(item) {
    console.log(item)
    const oldPrice = item.oldPrice;
    const newPrice = item.price;

    if (item.oldPrice === 0) {
        item.oldPrice = "";
    } else {
        item.oldPrice = item.oldPrice.toLocaleString() + " VND";
    }
    const price = parseInt(item.price * item.quantity).toLocaleString();
    item.price = item.price.toLocaleString();
    index++;
    const row = document.createElement("tr");
    row.innerHTML = `<td class="text-center">${index}</td>
                    <td class="text-center"><img
                            src="/hinh-anh/test/${item.productDetail.image.url1}"
                            style="height: 30px;" class="img-fluid" alt=""></td>
                    <td style="max-width: 300px;">
                      <p>${item.productDetail.product.name} [ ${item.productDetail.size.name} - ${item.productDetail.color.name} ] <br> <span
                              class="text-danger">${item.price} VND</span> <span
                              class="text-decoration-line-through">${item.oldPrice}</span></p>
                    </td>
                    <td class="text-center">${item.quantity}</td>
                    <td class="text-center">${price} VND</td>
                    <td class="text-center"><button
                            class="btn btn-primary rounded-pill">Thành
                      công</button></td>
                    <td class="text-center">
                      <button class="btn btn-info" id="btn-choose-${item.id}" data-bs-toggle="modal"
                              data-bs-target="#exampleModal" onclick="getIdBillDetail(${item.id})"><i
                              class="fa-solid fa-share-from-square"></i></button>
                    </td>`;
    listProductDetails.append(row);
}


function deleteProductTrade(idTradeProduct) {
    $.ajax({
        url: "/api/returnProduct/deleteTradeProduct/" + idTradeProduct,
        method: "DELETE",
        success: function (data) {
            const row = document.getElementById(`return-product-${idTradeProduct}`);
            row.parentNode.remove();
            loadPrice();
            // checkReturn();
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function showErrorProduct(idTradeProduct) {
    const listErrorProduct = document.getElementById("listErrorProduct");
    $.ajax({
        url: "/api/returnProduct/getErrorProductDetail/" + bill.id,
        method: "GET",
        success: function (data) {
            listErrorProduct.innerHTML = ``;
            data.forEach(item => {
                const row = renderErrorProduct(item);
                listErrorProduct.append(row);
            })
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function renderErrorProduct(item) {
    console.log(item)
    const row = document.createElement("tr");
    row.setAttribute("id", item.productDetail.id);
    row.setAttribute("data-id", item.id);
    row.innerHTML = `<td class="text-center">
                <img src="/hinh-anh/test/${item.productDetail.image.url1}" style="height: 30px;" class="img-fluid" alt="">
                </td>
                <td>
                <p>${item.productDetail.product.name} [ ${item.productDetail.size.name} - ${item.productDetail.color.name} ] <br> 
                </td>
                <td>${item.quantityReturn}</td>
                <td><input type="number" id="input-quantity-${item.productDetail.id}" value="${item.quantityError}" min="0" style="max-width: 100px" class="form-control" onchange="checkQuantityError(${item.productDetail.id})"> <span id="input-error-${item.productDetail.id}" style="color: red"></span></td>
                <td><div class="input-group">
                  <input type="text" id="ip-${item.productDetail.id}" value="${item.describe}" onchange="checkQuantityError(${item.productDetail.id})" class="form-control" aria-label="Text input with dropdown button">
                  <button class="btn btn-outline-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">Chọn lỗi</button>
                  <ul class="dropdown-menu">
                    <li><span class="dropdown-item" onclick="fillToInput('Khách hàng không ưng ý', ${item.productDetail.id})">Khách hàng không ưng ý</span></li>
                    <li><span class="dropdown-item" onclick="fillToInput('Sản phẩm lỗi size', ${item.productDetail.id})">Sản phẩm lỗi size</span></li>
                    <li><span class="dropdown-item" onclick="fillToInput('Không thích màu này', ${item.productDetail.id})">Không thích màu này</span></li>
                  </ul>
                  <div class="col-12">
                  <span class="text-danger"></span>
                  </div>
                </div>
                </td>
                `;
    return row;
}

function fillToInput(error, id) {
    const inputError = document.getElementById("ip-" + id);
    inputError.value = error;
    checkQuantityError();
}

function checkQuantityError() {
    let btnSubmit = document.getElementById("btn-errorProductDetail");
    let btnSubmitTrade = document.getElementById("btnSubmitTrade");
    if (quantity.value < 0) {
        quantity.value = 0;
    }
    const table = document.querySelectorAll("#table-error-product tbody tr");

    for (const row of table) {
        let idProductDetail = row.id;
        const id = row.dataset.id;
        let quantity = document.getElementById("input-quantity-" + idProductDetail);
        const quantityError = row.querySelector('input[type="number"]').value;
        const description = row.querySelector('input[type="text"]').value.trim();
        const errorSpan = row.querySelector('span.text-danger');
        const span = document.getElementById("input-error-" + idProductDetail);
        span.textContent = '';
        errorSpan.textContent = '';

        let stopLoop = false; // Đặt cờ hiệu để dừng vòng lặp

        $.ajax({
            url: "/api/returnProduct/checkQuantityError/" + idProductDetail + "/" + bill.id,
            method: "GET",
            async: false,
            success: function (data) {
                if (quantityError > 0 && description === "") {
                    errorSpan.textContent = 'Mô tả không được để trống khi số lượng lớn hơn 0';
                    span.textContent = '';
                    btnSubmit.disabled = true;
                    stopLoop = true;
                }
                    // else if ((quantityError === "" || quantityError === "0") && description !== "") {
                    //     errorSpan.textContent = '';
                    //     span.textContent = 'Số lượng phải lớn hơn 0 khi có mô tả';
                    //     btnSubmit.disabled = true;
                    //     stopLoop = true;
                // }
                else if (quantityError > data) {
                    span.innerText = "Số lượng phải nhỏ hơn hoặc bằng " + data;
                    btnSubmit.disabled = true;
                    stopLoop = true;
                } else if (data >= quantityError && description !== "") {
                    btnSubmit.disabled = false;
                }
            },
            error: function (error) {
                console.log(error);
            }
        });

        if (stopLoop) break;
    }


    // quantity.value = parseInt(quantity.value, 10).toString();
}

function addErrorProduct() {
    const table = document.querySelectorAll("#table-error-product tbody tr");
    let errorProductDetails = [];
    table.forEach(row => {
        const idProductDetail = row.id;
        const id = row.dataset.id;
        const soLuong = row.querySelector('input[type="number"]').value;
        const moTa = row.querySelector('input[type="text"]').value.trim();
        const productDetailError = {
            id: id,
            productDetail: {
                id: idProductDetail
            },
            quantity: soLuong,
            describe: moTa
        }
        errorProductDetails.push(productDetailError);
    })

    console.log(errorProductDetails);
    $.ajax({
        url: '/api/returnProduct/addErrorProductDetail/' + bill.id,
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(errorProductDetails),
        success: function () {

        },
        error: function (error) {
            console.error('Có lỗi xảy ra:', error);
        }
    });
}

function returnAll() {
    let dataReturn = [];
    const selects = document.querySelectorAll('select');
    selects.forEach(select => {
        const data = {
            id: select.getAttribute('data-id'),
            errorList: {
                id: select.value
            }
        };
        dataReturn.push(data);
    })
    if (dataReturn.length !== 0) {
        showToast("Vui lòng xóa hết sản phẩm trả hàng hiện tại!", "info")
        return;
    }
    $.ajax({
        url: "/api/returnProduct/returnAll/" + bill.id,
        method: "POST",
        success: function () {
            showProductTradeFromTrade(bill.id);
            loadPrice();
        }
    })
}

function loadPrice() {
    const payInfor = document.getElementById("pay-infor");
    const totalPay = document.getElementById("totalPay");
    $.ajax({
        url: "/api/returnProduct/get/" + bill.id,
        method: "GET",
        success: function (data) {
            console.log(data)
            payInfor.innerHTML = ``;
            payInfor.append(renderPay(data));
            totalPay.innerText = parseInt(data.payMoney).toLocaleString() + " VND";
            console.log(document.getElementById("listProductTrade"))
            const requestDate = document.getElementById("requestDate");
            const responseDate = document.getElementById("responseDate");
            if (data.requestDate !== null) {
                requestDate.innerText = formatDate(data.requestDate);
            } else {
                responseDate.innerText = ""
            }
            if (data.responseDate !== null) {
                responseDate.innerText = formatDate(data.responseDate);
            } else {
                responseDate.innerText = ""
            }


        },
        error: function (error) {
            console.log(error);
        }
    });
}


function renderPay(item) {
    console.log(item)
    const pay = document.createElement("div");
    pay.className = "row"
    const oldMoney = parseInt(item.oldProductDetailMoney).toLocaleString('vi-VN');
    const oldVoucher = item.oldVoucherMoney.toLocaleString();
    const newVoucher = item.newVoucherMoney.toLocaleString();
    const productDetailMoney = parseInt(item.productDetailMoney).toLocaleString('vi-VN');
    const backProductDetailMoney = parseInt(item.backProductDetailMoney).toLocaleString('vi-VN');

    pay.innerHTML = `<div class="col-6">
              <p>Tổng giá hàng gốc:</p>
            </div>
            <div class="col-6">
              <span>${oldMoney} VND</span>
            </div>
            <div class="col-6">
              <p>Tiền voucher cũ:</p>
            </div>
            <div class="col-6">
              <span>${oldVoucher} VND</span>
            </div>
<!--            <div class="col-6">-->
<!--              <p>Tiền ship:</p>-->
<!--            </div>-->
<!--            <div class="col-6">-->
<!--              <span>30.000 VND</span>-->
<!--            </div>-->
            <div class="col-6">
              <p>Tổng tiền thanh toán:</p>
            </div>
            <div class="col-6">
              <span>${productDetailMoney} VND</span>
            </div>
            <div class="col-6">
              <p>Tổng tiền trả hàng:</p>
            </div>
            <div class="col-6">
              <span>${backProductDetailMoney} VND</span>
            </div>
            <div class="col-6">
              <p>Tiền voucher mới:</p>
            </div>
            <div class="col-6">
              <span>${newVoucher} VND</span>
            </div>
<!--            <div class="col-6">-->
<!--              <p>Tổng tiền đổi hàng:</p>-->
<!--            </div>-->
<!--            <div class="col-6">-->
<!--              <span>50.000 VND</span>-->
<!--            </div>-->
            <hr>`;
    return pay;
}

function submitForm() {
    const data = validateForm();
    console.log(data);
    if (data) {
        Swal.fire({
            title: 'Xác nhận trả hàng',
            html: 'Bạn có chắc muốn trả hàng cho hoá đơn này không ?<br><strong>Lưu ý</strong> : Hệ thống sẽ gửi email thông báo đến admin về thông tin trả hàng của đơn hàng này.',
            icon: 'warning',
            showCancelButton: true,       // Hiển thị nút "Hủy"
            confirmButtonText: 'Xác nhận', // Nút xác nhận đăng xuất
            cancelButtonText: 'Hủy'        // Nút hủy
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: `/api/returnProduct/confirmReturnProduct/${bill.id}`,
                    method: "POST",
                    contentType: "application/json",
                    data: JSON.stringify(data),
                    success: function (data) {
                        console.log(data)
                        window.location.href = "/bill";
                    },
                    error: function (error) {
                        console.log("Error:", error);
                    }
                });
            }
        });
    }
}

function validateForm() {
    // Lấy danh sách các input cần kiểm tra
    const bankNameInput = document.querySelector('#bankName');
    const accountNumberInput = document.querySelector('input[placeholder="Số tài khoản"]');
    const recipientNameInput = document.querySelector('input[placeholder="Tên người nhận"]');
    const emailInput = document.querySelector('input[placeholder="Email"]');
    const phoneInput = document.querySelector('input[placeholder="Số điện thoại"]');
    const imageUploadInput = document.getElementById('imageUpload');
    const newUploadDiv = document.getElementById("new-upload-div");

    // Xóa lỗi cũ
    const inputs = [bankNameInput, accountNumberInput, recipientNameInput, emailInput, phoneInput];
    inputs.forEach(input => {
        input.classList.remove('border-danger');
        input.style.border = 'none';
        input.style.borderBottom = '1px solid';
    });

    // Kiểm tra Ngân hàng
    if (bankNameInput.value.trim() === '') {
        bankNameInput.classList.add('border-danger');
        bankNameInput.style.border = '1px solid';
        showToast("Tên ngân hàng không được để trống!", "info");
        return null;
    }

    // Kiểm tra Số tài khoản
    if (accountNumberInput.value.trim() === '') {
        accountNumberInput.classList.add('border-danger');
        accountNumberInput.style.border = '1px solid';
        showToast("Số tài khoản không được để trống!", "info");
        return null;
    }

    // Kiểm tra Người nhận (Ít nhất 8 ký tự)
    if (recipientNameInput.value.trim() === '') {
        recipientNameInput.classList.add('border-danger');
        recipientNameInput.style.border = '1px solid';
        showToast("Tên người nhận không được bỏ trống!", "info");
        return null;
    } else if (recipientNameInput.value.trim().length < 8) {
        recipientNameInput.classList.add('border-danger');
        recipientNameInput.style.border = '1px solid';
        showToast("Tên người nhận ít nhất 8 ký tự!", "info");
        return null;
    }

    // Kiểm tra Email (Regex kiểm tra email hợp lệ)
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (emailInput.value.trim() === '') {
        emailInput.classList.add('border-danger');
        emailInput.style.border = '1px solid';
        showToast("Email không được để trống!", "info");
        return null;
    } else if (!emailPattern.test(emailInput.value.trim())) {
        emailInput.classList.add('border-danger');
        emailInput.style.border = '1px solid';
        showToast("Sai định dạng email!", "info");
        return null;
    }

    // Kiểm tra Số điện thoại (Phải là 10 chữ số và bắt đầu bằng 0)
    const phonePattern = /^0\d{9}$/;  // Kiểm tra số điện thoại bắt đầu bằng '0' và có 10 chữ số
    if (phoneInput.value.trim() === '') {
        phoneInput.classList.add('border-danger');
        phoneInput.style.border = '1px solid';
        showToast("Số điện thoại không được bỏ trống!", "info");
        return null;
    } else if (!phonePattern.test(phoneInput.value.trim())) {
        phoneInput.classList.add('border-danger');
        phoneInput.style.border = '1px solid';
        showToast("Số điện thoại sai!", "info");
        return null;
    }

    // Kiểm tra ảnh nếu có (Ví dụ: ảnh ngân hàng)
    let imgUrl = "";
    if (document.getElementById("img-bankInfo") !== null) {
        imgUrl = document.getElementById("img-bankInfo").src;
    }

    let dataReturn = [];
    const selects = document.querySelectorAll('select');
    selects.forEach(select => {
        const option = select.querySelector(`option[value='${select.value}']`);
        const data = {
            id: select.getAttribute('data-id'),
            description: option.value,
            error: option.getAttribute('statusError')
        };
        dataReturn.push(data);
    })

    if (dataReturn.length === 0) {
        showToast("Không có sản phẩm trả hàng nào!", "info");
        return null;
    }

    // Lấy dữ liệu từ các input
    // Trả về formData nếu tất cả các trường hợp hợp lệ
    return {
        trade: {
            nameBank: bankNameInput.value.trim(),
            bankInfo: accountNumberInput.value.trim(),
            userInfo: recipientNameInput.value.trim(),
            email: emailInput.value.trim(),
            qrInfo: imgUrl,
            phoneInfo: phoneInput.value.trim()
        },
        tradeProductItems: dataReturn
    };
}

function checkReturn() {
    const returnAll = document.getElementById("btn-returnAll");
    if ($("#listProductTrade tr").length > 0) {
        returnAll.disabled = true;
        document.getElementById("btnSubmitTrade").disabled = false;
    } else {
        returnAll.disabled = false;
        document.getElementById("btnSubmitTrade").disabled = true;
    }
}

function formatDate(inputDate) {
    let date = new Date(inputDate);
    let day = String(date.getDate()).padStart(2, '0');
    let month = String(date.getMonth() + 1).padStart(2, '0');
    let year = date.getFullYear();
    let hours = String(date.getHours()).padStart(2, '0');
    let minutes = String(date.getMinutes()).padStart(2, '0');
    let seconds = String(date.getSeconds()).padStart(2, '0');
    return `${day}-${month}-${year} ${hours}:${minutes}:${seconds}`;
}

const cloudName = 'dbe1h6ajz';
const uploadPreset = 'hws3gmku';

function uploadImage() {
    const imageFile = document.getElementById("imageUpload").files[0];
    const status = document.getElementById("status");
    const imageContainer = document.getElementById("imageContainer");
    const spinner = document.getElementById("new-spinner");
    const plusSign = document.querySelector(".new-plus-sign");
    const uploadDiv = document.getElementById("new-upload-div");

    spinner.style.display = "block";
    plusSign.style.display = "none";
    imageContainer.innerHTML = "";

    const formData = new FormData();
    formData.append("file", imageFile);
    formData.append("upload_preset", uploadPreset);

    axios.post(`https://api.cloudinary.com/v1_1/${cloudName}/image/upload`, formData, {
        headers: {"Content-Type": "multipart/form-data"}
    })
        .then(response => {
            const url = response.data.secure_url;
            status.textContent = "";
            spinner.style.display = "none";
            plusSign.style.display = "block";
            uploadDiv.style.display = "none";

            imageContainer.innerHTML = `
                        <img src="${url}" id="img-bankInfo" alt="Uploaded Image" onclick="openImageModal('${url}')" style="cursor: pointer; border: 1px solid #ccc; border-radius: 5px;" />
                        <button class="new-delete-icon" onclick="deleteImage()">×</button>
                    `;
        })
        .catch(error => {
            console.error("Lỗi khi tải lên:", error);
            status.textContent = "Tải lên thất bại!";
            spinner.style.display = "none";
            plusSign.style.display = "block";
        });
}

function renderBankInfo() {
    const bankNameInput = document.querySelector('#bankName');
    const accountNumberInput = document.querySelector('input[placeholder="Số tài khoản"]');
    const recipientNameInput = document.querySelector('input[placeholder="Tên người nhận"]');
    const emailInput = document.querySelector('input[placeholder="Email"]');
    const phoneInput = document.querySelector('input[placeholder="Số điện thoại"]');
    const newUploadDiv = document.getElementById("new-upload-div");
    const imageContainer = document.getElementById("imageContainer");

    $.ajax({
        url: "/api/returnProduct/get/" + bill.id,
        method: "GET",
        success: function (item) {
            if (item.qrInfo !== "") {
                imageContainer.innerHTML = `
                        <img src="${item.qrInfo}" id="img-bankInfo" alt="Hình ảnh" style="cursor: pointer; border: 1px solid #ccc; border-radius: 5px;" />
                    `;
            }

            bankNameInput.value = item.nameBank;
            accountNumberInput.value = item.bankInfo;
            recipientNameInput.value = item.userInfo;
            emailInput.value = item.email;
            phoneInput.value = item.phoneInfo;

            const url = document.getElementById('img-bankInfo');
            console.log(url)

        },
        error: function (error) {
            console.log(error);
        }
    });
}

function openImageModal(url) {
    window.open(url);
}

function deleteImage() {
    const imageContainer = document.getElementById("imageContainer");
    const uploadDiv = document.getElementById("new-upload-div");

    // Xóa nội dung ảnh
    imageContainer.innerHTML = "";

    // Hiển thị lại ô upload
    uploadDiv.style.display = "block";
}

function errorList() {
    let options = ``;
    $.ajax({
        url: `/api/returnProduct/getErrorList`,
        method: "GET",
        success: function (data) {
            data.forEach(item => {
                options += `<option value="${item.id}">${item.errorName}</option>`
            })
            document.querySelectorAll('select').forEach(select => {
                select.innerHTML = ``;
                select.innerHTML = options;
            })
        },
        error: function (error) {
            console.log("Error:", error);
        }
    });
}

function deleteTradeProductItem(id, html) {
    $.ajax({
        url: `/api/returnProduct/deleteTradeProductItem/${id}`,
        method: "DELETE",
        success: function (data) {
            if (data.quantity === 0) {
                document.getElementById(`return-product-${data.id}`).remove();
            } else {
                html.parentNode.parentNode.remove();
                document.getElementById(`quantityReturn-${data.id}`).innerText = 'Số lượng trả:' + data.quantity;
            }
        },
        error: function (error) {
            console.log("Error:", error);
            html.parentNode.parentNode.remove();
        },
        complete: function () {
            loadPrice();
        }
    });
}

function renderQRCode() {
    const bankNameInput = document.querySelector('#bankName');
    const accountNumberInput = document.querySelector('input[placeholder="Số tài khoản"]');
    const img = document.getElementById('imageContainer');
    if (bankNameInput.value.trim() !== '' && accountNumberInput.value.trim() !== '') {
        $.ajax({
            url: "https://api.vietqr.io/v2/banks",
            method: "GET",
            success: function ({data}) {
                let check = false;
                data.forEach(item => {
                    const nameBank = `${item.shortName} - ${item.name}`;
                    if (nameBank === bankNameInput.value.trim()) {
                        const input = {
                            accountNo: accountNumberInput.value.trim(),
                            accountName: "ABCDEF",
                            acqId: item.bin,
                            addInfo: "Hoan tien cho hoa don #" + bill.id,
                            format: "text",
                            template: "compact"
                        }
                        $.ajax({
                            url: "https://api.vietqr.io/v2/generate",
                            method: "POST",
                            contentType: "application/json",
                            data: JSON.stringify(input),
                            success: function (data) {
                                console.log(data)
                                if (data.code === "00") {
                                    img.innerHTML = `<img src="${data.data.qrDataURL}" id="img-bankInfo" alt="Uploaded Image" style="cursor: pointer; border: 1px solid #ccc; border-radius: 5px;" />`;
                                    document.getElementById("btnSubmitTrade").disabled = false;
                                } else {
                                    img.innerHTML = ``;
                                    showToast("Số tài khoản không hợp lệ!", "info");
                                    document.getElementById("btnSubmitTrade").disabled = true;
                                }
                            }
                        })
                        check = true;
                    }
                })
                if (!check) {
                    img.innerHTML = ``;
                    showToast("Tên tài khoản không hợp lệ!", "info")
                }
            }
        })
    }
}

function showToast(message, status) {
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
        icon: status,
        title: message
    });
}


