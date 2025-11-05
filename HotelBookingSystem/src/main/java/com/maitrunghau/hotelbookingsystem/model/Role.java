package com.maitrunghau.hotelbookingsystem.model;

import lombok.Getter;

@Getter
public enum Role {
    Admin("Quản trị viên"),
    Customer("Khách hàng");
    private final String label;

    Role(String label) {
        this.label = label;
    }

}


//Receptionist("Lễ tân"),
//Housekeeping("Nhân viên dọn phòng"),
//Finance("Kế toán / Thu ngân"),
//ServiceManager("Quản lý dịch vụ");  // hoặc STAFF chung

//Admin: người của công ty phần mềm (CMS) quản lý cấu hình hệ thống, tài khoản nhân viên.
//Service Manager: trưởng bộ phận khách sạn — có thể thêm/sửa dịch vụ, quản lý doanh thu từng ngày. quản lý theo từng hotel mà có nó có quyền
//Receptionist: nhân viên lễ tân xử lý đặt phòng và check-in.
//        Customer: khách hàng đặt phòng qua web.
// dọn phòng nhận và duy trì

//https://console.cloudinary.com/app/c-8546920b6b61d78d938f60367a9acc/assets/media_library/folders/cd27f1ba5989461cb35f8d44f5dd189017?view_mode=mosaic