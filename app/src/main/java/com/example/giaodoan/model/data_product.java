package com.example.giaodoan.model;

public class data_product<T>{
    private Integer product_id;
    private Integer category_id;
    private String category_name;
    private String product_name;
    private Long price;
    private String img;

    public data_product(){

    }

    public data_product(Integer product_id, Integer category_id,String category_name, String product_name, Long price, String img){
        this.product_id = product_id;
        this.category_id = category_id;
        this.category_name = category_name;
        this.product_name = product_name;
        this.price = price;
        this.img = img;
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

    public String getImg(){
        return this.img;
    }
}