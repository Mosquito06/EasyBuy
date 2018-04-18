package kr.or.dgit.bigdata.easybuy.kr.or.dgit.bigdata.dto;

import java.util.Date;

/**
 * Created by DGIT3-12 on 2018-04-18.
 */

public class User {
    public int clientNum;
    public String id;
    public String name;
    public String email;
    public String phone;
    public String homeTel;
    public String address;

    @Override
    public String toString() {
        return "User{" +
                "clientNum=" + clientNum +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", homeTel='" + homeTel + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
