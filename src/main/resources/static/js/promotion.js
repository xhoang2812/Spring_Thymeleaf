function loading(){
    fetchPromotion();
    listenToSSE();
    const thongBao = sessionStorage.getItem("statusCheck");
    if (thongBao !== null){
        showToast(thongBao, "success");
        sessionStorage.removeItem("statusCheck");
    }
}
function checkUser(){
    $.ajax({
        url: "/api/vouchers/userData",
        method: "GET",
        success: function (data) {
            console.log(data)
            if (data === "") {
                document.querySelectorAll("#i-1").forEach(i => i.remove());
                document.querySelectorAll("#i-2").forEach(i => i.remove());
                document.querySelectorAll("#i-3").forEach(i => i.remove());
            }
        },
        error: function (error) {
            console.log(error)
        }
    })
}
function renderPromotion(promotion) {
    let dateUpdate = promotion.dateUpdate;
    let status = "";
    console.log(promotion.condition)
    if(promotion.condition === true){
        status = "Hoạt động";
    }else {
        status = "Đã huỷ";
    }
    if(promotion.dateUpdate === null) {
        dateUpdate = "";
    }
    const tr = document.createElement("tr");
    tr.setAttribute("id", "row"+promotion.id);
    tr.innerHTML= `
                    <td>${promotion.id}</td>
                    <td>${promotion.code}</td>
                    <td>${promotion.name}</td>
                    <td>${promotion.price}</td>
                    <td>${promotion.dateStart}</td>
                    <td>${promotion.dateEnd}</td>

                    <td>
                        <button class="btn btn-primary rounded-pill">${promotion.status}</button>
                    </td>
                    <td>
                        <button class="btn btn-primary rounded-pill">${status}</button>
                    </td>
                    <td>
                        <button id="i-3" onclick="updatePromotion('${promotion.id}', '${promotion.status}', '${status}')" class="btn btn-warning">
                            <i class="fas fa-edit"></i>
                        </button>
                        <a href="/promotions/promotionDetails/${promotion.id}" class="btn btn-dark">
                            <i class="fas fa-eye"></i>
                        </a>
                        <button id="i-2" class="btn btn-danger" onclick="deletePromotion(${promotion.id})"><i class="fa-solid fa-trash"></i></button>
                    </td>
                `;
    return tr;
}
function fetchPromotion() {
    $.ajax({
        url: '/khuyenmai',
        method: 'GET',
        success: function(data) {
            const div = document.getElementById("listPromotion");
            data.forEach(item => {
                div.append(renderPromotion(item))
            });
            $("#multi-filter-select").DataTable({
                pageLength: 5,
                order: [[0, 'desc']]
            });
        },
        complete : function (){
            checkUser();
        }
    });
}
function listenToSSE() {
    const eventSource = new EventSource("http://localhost:8080/api/stream/promotion");

    eventSource.addEventListener("promotionUpdate", function (event) {
        const updatePromotion = JSON.parse(event.data);
        updatePromotionUI(updatePromotion);
    });

    eventSource.onerror = function (err) {
        console.error("EventSource failed: ", err);
    };
}
function updatePromotionUI(promotion) {
    const voucherRow = document.getElementById("row" + promotion.id);
    if (voucherRow) {
        const conditionButton = voucherRow.querySelector("td button");
        // console.log(conditionButton)
        if (conditionButton) {
            conditionButton.innerHTML = promotion.status;
        }
    }
}
function updatePromotion(idPromotion, status, condition){
    if(status === "Đã kết thúc" || condition === "Đã huỷ"){
        showToast("Không thể cập nhật  đợt giảm giá khi ở tình trạng đã kết thúc hoặc đã huỷ.", "error")
    }else {
        window.location.href = `/promotions/edit/${idPromotion}`;
    }
}
function deletePromotion(id){
    Swal.fire({
        title: 'Xác nhận huỷ đợt giảm giá',
        html: 'Bạn có chắc chắn muốn huỷ đợt giảm giá không?<br><strong>Lưu ý</strong> : Sau khi huỷ sẽ <strong>không thể kích hoạt</strong> lại đợt giảm giá.',
        icon: 'warning',
        showCancelButton: true,       // Hiển thị nút "Hủy"
        confirmButtonText: 'Xác nhận', // Nút xác nhận đăng xuất
        cancelButtonText: 'Hủy'        // Nút hủy
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: '/khuyenmai/updateStatusPromotion/' + id,
                method: 'DELETE',
                success: function(data) {
                    if(data === ""){
                        showToast("Không thể kích hoạt đợt giảm giá khi đã huỷ", "error")
                    }else {
                        $('#multi-filter-select').DataTable().destroy();
                        const oldRow = document.getElementById("row" + id);
                        const newRow = renderPromotion(data);
                        document.getElementById("listPromotion").replaceChild(newRow,oldRow);
                        showToast("Hủy đợt giảm giá thành công", "success");
                        $("#multi-filter-select").DataTable({
                            pageLength: 5,
                        });
                    }
                }
            });
        }
    });
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