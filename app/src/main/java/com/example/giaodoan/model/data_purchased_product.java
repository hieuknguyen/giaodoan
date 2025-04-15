package com.example.giaodoan.model;

public class data_purchased_product<T> {
    private Integer purchased_products_id;
    private Integer product_id;
    private Integer user_id;
    private  String product_name;
    private Integer total_amount;
    private Long price;
    private Integer rating;
    private String review;
    private Integer has_review;

   public data_purchased_product(){

   }

    public data_purchased_product(Integer purchased_products_id,Integer product_id, Integer user_id,String product_name,Integer total_amount,Long price,Integer rating, String review, Integer has_review){
        this.purchased_products_id = purchased_products_id;
        this.product_id = product_id;
        this.user_id = user_id;
        this.product_name = product_name;
        this.total_amount = total_amount;
        this.price = price;
        this.rating = rating;
        this.review = review;
        this.has_review = has_review;
    }

    public Integer getPurchased_products_id(){
        return this.purchased_products_id;
    }
    public Integer getProduct_id(){
        return this.product_id;
    }
    public Integer getUser_id(){
        return this.user_id;
    }
    public String getProduct_name(){
        return this.product_name;
    }
    public Integer getTotal_amount(){
        return this.total_amount;
    }
    public Long getPrice(){
        return this.price;
    }
    public Integer getRating(){return this.rating;}
    public String getReview(){return  this.review;}
    public Integer getHas_review(){return  this.has_review;}

}