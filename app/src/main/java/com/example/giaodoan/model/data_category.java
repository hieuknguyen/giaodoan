package com.example.giaodoan.model;

public class data_category<T>{
    private Integer category_id;
    private String category_name;
    private  String description;

    public data_category(){

    }

    public data_category(Integer category_id,String category_name,String description){
        this.category_id = category_id;
        this.category_name = category_name;
        this.description = description;
    }
    public Integer getCategory_id(){
        return this.category_id;
    }
    public String getCategory_name(){
        return this.category_name;
    }
    public String getDescription(){
        return this.description;
    }

}
