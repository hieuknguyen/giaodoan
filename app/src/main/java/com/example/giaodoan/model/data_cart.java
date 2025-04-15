package com.example.giaodoan.model;

public class data_cart<T>{
    private Integer cart_id;
    private Integer product_id;
    private String category_name;

    private Integer category_id;
    private  String product_name;
    private Long price;
    private  Integer total_amount;

    public data_cart(){

    }

    public data_cart(Integer cart_id,Integer product_id, Integer category_id, String category_name,String product_name,Long price,Integer total_amount){
        this.cart_id = cart_id;
        this.product_id = product_id;
        this.category_id = category_id;
        this.category_name = category_name;
        this.product_name = product_name;
        this.price = price;
        this.total_amount = total_amount;

    }

    public Integer getCart_id(){
        return this.cart_id;
    }
    public Integer getProduct_id(){
        return this.product_id;
    }
    public Integer getCategory_id(){
        return this.category_id;
    }
    public String getCategory_name(){
        return this.category_name;
    }
    public String getProduct_name(){
        return this.product_name;
    }
    public Long getPrice(){
        return this.price;
    }
    public Integer getTotal_amount(){
        return this.total_amount;
    }

}
