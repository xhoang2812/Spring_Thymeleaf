const bill = JSON.parse(sessionStorage.getItem("bill"));
const listProductDetails = document.getElementById("listProductDetail");
function showProductTrade() {
    const show = document.getElementById("product-trade");
    if (show.style.display === "none") {
        show.style.display = "block";
    } else {
        show.style.display = "none";
    }
}

function loading(){
    if(bill.status === 7){
        document.getElementById('statusBill').innerText = 'Chờ hoàn tiền cho khách';
    }else if(bill.status === 14){
        document.getElementById('statusBill').innerText = 'Đã hoàn tiền cho khách';
        document.getElementById('btn-huy').style.display = 'none';
    }

    loadProductDetailOfBill(bill.id);
    showProductTradeFromTrade(bill.id);
    showProductErrorOfBill(bill.id);
    console.log("bill",bill.id);
    loadPrice();
    loadCustomer(bill);
}

function loadCustomer(bill){
    console.log(bill);
    const customerInfo = document.getElementById("customer-info");

    let phone = "Tại quầy";
    let address = "Tại quầy";
    let description = "Tại quầy";
    if(bill.customer === null){
        bill.customer = "Khách lẻ";
        if(bill.address !== null){
            phone = bill.address.phone;
            address = `${bill.address.ward}, ${bill.address.district}, ${bill.address.province}` ;
            description = bill.address.addreseDetail;
        }
    }else {
        bill.customer = bill.customer.name;
    }

    if(bill.paymentType === false){
        bill.paymentType = "Tại quầy";
    }else {
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
                <p>Ngày giao hàng:</p>
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
                <p>Ngày nhận hàng:</p>
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

function loadProductDetailOfBill(idBill){
    $.ajax({
        url : "/api/returnProduct/bill/billDetail/"+ idBill,
        method : "GET",
        success : function (data){
            listProductDetails.innerHTML = ``;
            data.forEach(item => renderProduct(item));
        },
        error : function (error){
            console.log(error)
        }
    })
}

function renderProduct(item){
    if(item.description === null){
        item.description = "Thành công"
    }
    console.log(item)
    const row = document.createElement("tr");
    const price = parseInt(item.price * item.quantity).toLocaleString('vi-VN');
    row.innerHTML = `<td class="text-center">${item.id}</td>
                    <td class="text-center"><img
                            src="/hinh-anh/test/${item.productDetail.image.url1}"
                            style="height: 30px;" class="img-fluid" alt=""></td>
                    <td style="max-width: 300px;">
                      <p>${item.productDetail.product.name} [ ${item.productDetail.size.name} - ${item.productDetail.color.name} ] <br> <span
                              class="text-danger">${item.productDetail.price} VND</span></p>
                    </td>
                    <td class="text-center">${item.quantity}</td>
                    <td class="text-center">${price} VND</td>
                    <td class="text-center"><button
                            class="btn btn-primary rounded-pill">${item.description}</button></td>
                    `;
    listProductDetails.append(row);
}

let i = 0;
const listProductTrade = document.getElementById("listProductTrade");
function showProductTradeFromTrade(idBill) {
    $.ajax({
        url: "/api/returnProduct/getProductTrade/" + idBill,
        method: "GET",
        success: function (returnLists) {
            console.log(returnLists);
            returnLists.forEach(item => {
                const row = document.createElement('div');
                row.classList.add('border-0', 'shadow', 'card');
                row.innerHTML = renderProductTrade(item);
                listProductTrade.append(row)
            });
        },
        error: function (error) {
            console.log(error);
        }
    });
}
function renderProductTrade(item) {
    const price = parseInt(item.billDetail.price * item.quantity).toLocaleString();
    item.billDetail.price = item.billDetail.price.toLocaleString();
    let listReturnProducts = ``;
    item.tradeProductItems.forEach(product => {
        console.log(product)
        listReturnProducts += `<div class="d-flex">
                  <div class="col-4">
                    ${item.billDetail.productDetail.product.name} [ ${item.billDetail.productDetail.size.name} - ${item.billDetail.productDetail.color.name} ] <sup>${item.billDetail.price} VND</sup>
                  </div>
                  <div class="col-8">
                    <p>${product.description} <sup>${product.error === true ? "Nguyên vẹn" : "Lỗi"}</sup></p>
                  </div>
              </div>`;
    })

    return `<div class="card-body" id="return-product-${item.id}">
              <div class="">
                <h5>${item.billDetail.productDetail.product.name} [ ${item.billDetail.productDetail.size.name} - ${item.billDetail.productDetail.color.name} ] <sup id="quantityReturn-${item.id}">Số lượng trả: ${item.quantity}</sup></h5>
              </div>
              ${listReturnProducts}
            </div>`;
}

const listProductError = document.getElementById("listProductError");
function showProductErrorOfBill(idBill) {
    $.ajax({
        url: "/api/returnProduct/getAllErrorProductOfBill/" + idBill,
        method: "GET",
        success: function (data) {
            console.log(data);
            let htmlContent = '';
            i = 0;
            data.forEach(item => {
                htmlContent += renderProductError(item);
            });

        },
        error: function (error) {
            console.log(error);
        }
    });
}

function deleteTrade(){
    $.ajax({
        url : "/api/vouchers/userData",
        method : "GET",
        success : function (data){
            if(data === ""){
                showToast("Bạn không phải Admin", "warning");
                return;
            }
            Swal.fire({
                title: 'Xác nhận hủy trả hàng',
                html: 'Bạn có chắc muốn hủy đơn trả hàng cho hoá đơn này không ?',
                icon: 'warning',
                showCancelButton: true,       // Hiển thị nút "Hủy"
                confirmButtonText: 'Xác nhận', // Nút xác nhận đăng xuất
                cancelButtonText: 'Hủy'        // Nút hủy
            }).then((result) => {
                if (result.isConfirmed) {
                    $.ajax({
                        url: `/api/returnProduct/deleteTrade/${bill.id}`,
                        method: "DELETE",
                        success: function() {
                            window.location.href = "/bill";
                        },
                        error: function(error) {
                            console.log("Error:", error);
                        }
                    });
                }
            });
        },
        error : function (error){
            console.log(error)
        }
    })


}

function renderProductError(item) {
    i++;
    return `
        <tr id="${item.id}">
            <td class="text-center">${i}</td>
            <td class="text-center">
                <img src="/hinh-anh/test/${item.productDetail.image.url1}" style="height: 30px;" class="img-fluid" alt="">
            </td>
            <td>
                <p>${item.productDetail.product.name} [ ${item.productDetail.size.name} - ${item.productDetail.color.name} ] <br> 
                <span class="text-danger">${item.productDetail.price} VND</span> 
                </p>
            </td>
            <td class="text-center">
                ${item.quantity}
            </td>
            <td class="text-center">
                ${item.describe}
            </td>
        </tr>`;
}

function loadPrice(){
    const payInfor = document.getElementById("pay-infor");
    const totalPay = document.getElementById("totalPay");
    $.ajax({
        url: "/api/returnProduct/" + bill.id,
        method: "GET",
        success: function (data) {
            console.log(data);
            payInfor.innerHTML = ``;
            payInfor.append(renderPay(data));
            renderBankInfo(data);
            totalPay.innerText = parseInt(data.payMoney).toLocaleString('vi-VN') + " VND";
            const requestDate = document.getElementById("requestDate");
            const responseDate = document.getElementById("responseDate");
            if(data.requestDate !== null){
                requestDate.innerText = formatDate(data.requestDate);
            }else {
                responseDate.innerText = ""
            }
            if(data.responseDate !== null){
                responseDate.innerText = formatDate(data.responseDate);
            }else {
                responseDate.innerText = ""
            }
        },
        error: function (error) {
            console.log(error);
        }
    });
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

function renderPay(item){
    const pay = document.createElement("div");
    pay.className = "row"
    const oldMoney = parseInt(item.oldProductDetailMoney).toLocaleString('vi-VN');
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
              <span>${item.oldVoucherMoney.toLocaleString()} VND</span>
            </div>

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
              <p>tiền voucher mới:</p>
            </div>
            <div class="col-6">
              <span>${item.newVoucherMoney.toLocaleString()} VND</span>
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

function renderBankInfo(data){
    const bankInfo = document.getElementById("bank-info");
    bankInfo.innerHTML = `<div class="row">
              <div class="col-4">
                <p>Ngân hàng:</p>
              </div>
              <div class="col-8">
                <p id="description">${data.nameBank}</p>
              </div>
              <div class="col-4">
                <p>Số tài khoản:</p>
              </div>
              <div class="col-8">
                <p id="description">${data.bankInfo}</p>
              </div>
              <div class="col-4">
                <p>Tên người nhận:</p>
              </div>
              <div class="col-8">
                <p id="description">${data.userInfo}</p>
              </div>
              <div class="col-4">
                <p>Email:</p>
              </div>
              <div class="col-8">
                <p id="description">${data.email}</p>
              </div>
              <div class="col-4">
                <p>Số điện thoại:</p>
              </div>
              <div class="col-8">
                <p id="description">${data.phoneInfo}</p>
              </div>
              <div class="">
                <p>QR code thanh toán:</p>
                <div id="new-upload-div" style="float: left;">
                  <img src="${data.qrInfo}" style="max-width: 300px" alt="Không có ảnh">
                </div>
              </div>
              <div class="mt-3">
                <p>Minh chứng hoàn tiền:</p>
                <div id="upload-div" style="float: left;">
                  <img src="${data.qrPay}" style="max-width: 300px" alt="Không có ảnh">
                </div>
              </div>
              <div class="col-3 mt-3">
                <p>Mô tả:</p>
              </div>
              <div class="col-9 mt-3">
                <p id="description">${data.description !== null ? data.description : ''}</p>
              </div>`;
}