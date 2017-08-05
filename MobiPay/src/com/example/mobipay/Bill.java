package com.example.mobipay;

/**
 * Bill绫荤敤浜庝繚瀛樼敤鎴疯处鍗曚俊鎭�
 * @author hzw
 */
public class Bill {
    //  璐﹀崟ID
    private String bill_id;
    //  鐢ㄦ埛鍚�
    private String user_id;
    //  鍟嗗鍚�
    private String merchant_id;
    //  鏃堕棿
    private String date;
    //  閲戦
    private String amount;
    //  璐﹀崟璇︽儏
    private String detail;

    //  bill_id
    public void setBill_id(String bill_id) {
        this.bill_id = bill_id;
    }

    public String getBill_id() {
        return bill_id;
    }

    //  user_id
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    //  merchant_id
    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    //  date
    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    //  amount
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    //  detail
    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }

    public Bill getBill() {
        return this;
    }
}

