package com.example.giaodoan.model;

public class data_user{
    private Integer user_id;
    private String username;
    private String password;
    private  String email;
    private String phone;
    private String address;
    private  String role;

    public void set(Integer user_id,String username,String password,String email,String phone,String address,String role){
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }
    public Integer getUser_id(){
        return this.user_id;
    }
    public String getUsername(){
        return this.username;
    }
    public String getPassword(){
        return this.password;
    }
    public String getEmail(){
        return this.email;
    }
    public String getPhone(){
        return this.phone;
    }
    public String getAddress(){
        return this.address;
    }
    public String getRole(){
        return this.role;
    }
}
