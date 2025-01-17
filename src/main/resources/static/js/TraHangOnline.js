
document.addEventListener('DOMContentLoaded', () => {
    const offcanvas = document.getElementById('offcanvasRight');
    const toggleButton = document.getElementById('toggleOffcanvas');
    const closeButton = document.getElementById('closeOffcanvas');

    // Toggle mở/tắt offcanvas
    toggleButton.addEventListener('click', () => {
        offcanvas.classList.toggle('open');
    });

    // Đóng offcanvas
    closeButton.addEventListener('click', () => {
        offcanvas.classList.remove('open');
    });

    // // Đóng khi nhấn ngoài vùng offcanvas
    // document.addEventListener('click', (event) => {
    //     if (!offcanvas.contains(event.target) && event.target !== toggleButton) {
    //         offcanvas.classList.remove('open');
    //     }
    // });
});

let id = 0;
function idBill(idBill){
    const listDetail = document.getElementById("listDetail");
    id = idBill;
    $.ajax({
        url : "/api/returnProduct/bill/billDetail/"+ idBill,
        method : "GET",
        success : function (data){
            listDetail.innerHTML = ``;
            data.forEach(item => {
                const row = renderProduct(item);
                listDetail.append(row);
            });

        },
        error : function (error){
            console.log(error)
        }
    })
}

function getBillDetails() {
    const listDetail = document.getElementById("listDetail");
    const details = [];

    // Lấy tất cả các phần tử có class "bill-detail-item"
    const items = listDetail.querySelectorAll("[id^='billDetail-']");

    items.forEach((item, index) => {

        const id = item.id.split('-')[1];
        const input = item.querySelector(".quantity-input");
        const quantity = input.value;

        details.push({ id: id, quantity: quantity });
    });
    if(validateQuantities()){
        $.ajax({
            url: "/home/customer-bill-management/api/returnOnline/createTradeOnline/" + id,
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify(details),
            success: function(response) {

                console.log("Data submitted successfully:", response.bill.id);
            },
            error: function(error) {
                console.error("Error submitting data:", error);
            }
        });

    }
}

function showTradeDetail(){
    $.ajax({
        url: "/home/customer-bill-management/api/returnOnline/tradeDetail/" + id,
        method: "GET",
        success: function(response) {
            console.log("Data submitted successfully:", response);
        },
        error: function(error) {
            console.error("Error submitting data:", error);
        }
    });
}

function renderProduct(item){
    const row = document.createElement("div");
    row.classList.add('bill-detail-item');
    row.innerHTML = `<div class="" id="billDetail-${item.id}">
                <div class="offcanvas-body d-flex">
                    <div class="">
                        <img src="https://www.chuphinhsanpham.vn/wp-content/uploads/2021/06/chup-hinh-giay-dincox-shoes-c-photo-studio-4.jpg"
                             height="80px" alt="">
                    </div>
                    <div class="ml-2">
                        <p>${item.productDetail.product.name} <sup>(${item.quantity})</sup> <br>
                            ${item.productDetail.color.name}, ${item.productDetail.size.name} <br>
                            <input type="number" class="quantity-input" style="width: 60px;"
                               min="0" max="${item.quantity}" value="0">
                        </p>
                    </div>
                </div>
                <hr style="width: 80%; margin-top: -20px;">
            </div>`;
    return row;
}

function validateQuantities() {
    const listDetail = document.getElementById("listDetail");
    const items = listDetail.querySelectorAll(".bill-detail-item");
    let isValid = true;

    items.forEach(item => {
        const input = item.querySelector(".quantity-input");
        const maxQuantity = parseInt(input.getAttribute("max"));
        const currentQuantity = parseInt(input.value);
        if (currentQuantity < 0 || currentQuantity > maxQuantity) {
            isValid = false;
            input.style.borderColor = "red";
        } else {
            input.style.borderColor = "";
        }
    });
    return isValid;
}
