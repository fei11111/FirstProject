package com.fei.firstproject.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/8/26.
 */

public class OrderEntity {


    /**
     * status : 1
     * info : success
     * totalPage : 7
     * data : [{"receipt_tel":"13266505288","N":14.21,"mode":null,"solution_detail_id":null,"K":15.06,"paymentCreateDate":null,"user_service_id":"110011","create_date":"2016-07-04 10:28:38","user_service_name":"深圳市光明新区楼村芭田种植基地","atm_fact":null,"P":16.15,"amt_pay":null,"qty_fact":null,"order_head_id":"DH1467599161288","user_id":"1119200","order_from":"2","note":null,"reserve4":null,"user_tel":"13652025632","user_type":"10,20","atm_shipping":0,"reserve3":null,"reserve2":"1","creator_id":"1119200","reserve1":"2.63","organism":null,"discount":null,"pay_flag_id":null,"creator_desc":"111920","receipt_addr":"北京市密云县 寂图默默寞吗","qty_order":3,"atm_order":7.89,"pay_date":null,"user_desc":"黄锦飞","payment_id":null,"order_flag_id":"1","addr_delivery":null,"Formula_head_id":null,"atm_payable":7.89,"receipt_user_name":"哈哈","order_type":"2"},{"receipt_tel":"13266505288","N":14.21,"mode":null,"solution_detail_id":null,"K":15.06,"paymentCreateDate":null,"user_service_id":"110011","create_date":"2016-07-04 10:28:37","user_service_name":"深圳市光明新区楼村芭田种植基地","atm_fact":null,"P":16.15,"amt_pay":null,"qty_fact":null,"order_head_id":"DH1467599159889","user_id":"1119200","order_from":"2","note":null,"reserve4":null,"user_tel":"13652025632","user_type":"10,20","atm_shipping":0,"reserve3":null,"reserve2":"1","creator_id":"1119200","reserve1":"2.63","organism":null,"discount":null,"pay_flag_id":null,"creator_desc":"111920","receipt_addr":"北京市密云县 寂图默默寞吗","qty_order":9999.99,"atm_order":26299.97,"pay_date":null,"user_desc":"黄锦飞","payment_id":null,"order_flag_id":"1","addr_delivery":null,"Formula_head_id":null,"atm_payable":26299.97,"receipt_user_name":"哈哈","order_type":"2"},{"receipt_tel":"13266505288","N":14.21,"mode":null,"solution_detail_id":null,"K":15.06,"paymentCreateDate":null,"user_service_id":"110011","create_date":"2016-07-04 10:28:36","user_service_name":"深圳市光明新区楼村芭田种植基地","atm_fact":null,"P":16.15,"amt_pay":null,"qty_fact":null,"order_head_id":"DH1467599158927","user_id":"1119200","order_from":"2","note":null,"reserve4":null,"user_tel":"13652025632","user_type":"10,20","atm_shipping":0,"reserve3":null,"reserve2":"1","creator_id":"1119200","reserve1":"2.63","organism":null,"discount":null,"pay_flag_id":null,"creator_desc":"111920","receipt_addr":"北京市密云县 寂图默默寞吗","qty_order":1,"atm_order":2.76,"pay_date":null,"user_desc":"黄锦飞","payment_id":null,"order_flag_id":"1","addr_delivery":null,"Formula_head_id":null,"atm_payable":2.76,"receipt_user_name":"哈哈","order_type":"2"},{"receipt_tel":"13266505288","N":14.21,"mode":null,"solution_detail_id":null,"K":15.06,"paymentCreateDate":null,"user_service_id":"110011","create_date":"2016-07-04 10:28:35","user_service_name":"深圳市光明新区楼村芭田种植基地","atm_fact":null,"P":16.15,"amt_pay":null,"qty_fact":null,"order_head_id":"DH1467599157812","user_id":"1119200","order_from":"2","note":null,"reserve4":null,"user_tel":"13652025632","user_type":"10,20","atm_shipping":0,"reserve3":null,"reserve2":"1","creator_id":"1119200","reserve1":"2.63","organism":null,"discount":null,"pay_flag_id":null,"creator_desc":"111920","receipt_addr":"北京市密云县 寂图默默寞吗","qty_order":1,"atm_order":2.63,"pay_date":null,"user_desc":"黄锦飞","payment_id":null,"order_flag_id":"1","addr_delivery":null,"Formula_head_id":null,"atm_payable":2.63,"receipt_user_name":"哈哈","order_type":"2"},{"receipt_tel":"13266505288","N":14.21,"mode":null,"solution_detail_id":null,"K":15.06,"paymentCreateDate":null,"user_service_id":"110011","create_date":"2016-07-04 10:28:34","user_service_name":"深圳市光明新区楼村芭田种植基地","atm_fact":null,"P":16.15,"amt_pay":null,"qty_fact":null,"order_head_id":"DH1467599156771","user_id":"1119200","order_from":"2","note":null,"reserve4":null,"user_tel":"13652025632","user_type":"10,20","atm_shipping":0,"reserve3":null,"reserve2":"1","creator_id":"1119200","reserve1":"2.63","organism":null,"discount":null,"pay_flag_id":null,"creator_desc":"111920","receipt_addr":"北京市密云县 寂图默默寞吗","qty_order":1,"atm_order":2.63,"pay_date":null,"user_desc":"黄锦飞","payment_id":null,"order_flag_id":"1","addr_delivery":null,"Formula_head_id":null,"atm_payable":2.63,"receipt_user_name":"哈哈","order_type":"2"},{"receipt_tel":"13266505288","N":14.21,"mode":null,"solution_detail_id":null,"K":15.06,"paymentCreateDate":null,"user_service_id":"110011","create_date":"2016-07-04 10:28:32","user_service_name":"深圳市光明新区楼村芭田种植基地","atm_fact":null,"P":16.15,"amt_pay":null,"qty_fact":null,"order_head_id":"DH1467599155583","user_id":"1119200","order_from":"2","note":null,"reserve4":null,"user_tel":"13652025632","user_type":"10,20","atm_shipping":0,"reserve3":null,"reserve2":"1","creator_id":"1119200","reserve1":"2.63","organism":null,"discount":null,"pay_flag_id":null,"creator_desc":"111920","receipt_addr":"北京市密云县 寂图默默寞吗","qty_order":1,"atm_order":2.63,"pay_date":null,"user_desc":"黄锦飞","payment_id":null,"order_flag_id":"1","addr_delivery":null,"Formula_head_id":null,"atm_payable":2.63,"receipt_user_name":"哈哈","order_type":"2"},{"receipt_tel":"13266505288","N":14.21,"mode":null,"solution_detail_id":null,"K":15.06,"paymentCreateDate":null,"user_service_id":"110011","create_date":"2016-07-04 10:28:31","user_service_name":"深圳市光明新区楼村芭田种植基地","atm_fact":null,"P":16.15,"amt_pay":null,"qty_fact":null,"order_head_id":"DH1467599153928","user_id":"1119200","order_from":"2","note":null,"reserve4":null,"user_tel":"13652025632","user_type":"10,20","atm_shipping":0,"reserve3":null,"reserve2":"1","creator_id":"1119200","reserve1":"2.63","organism":null,"discount":null,"pay_flag_id":null,"creator_desc":"111920","receipt_addr":"北京市密云县 寂图默默寞吗","qty_order":1,"atm_order":2.63,"pay_date":null,"user_desc":"黄锦飞","payment_id":null,"order_flag_id":"1","addr_delivery":null,"Formula_head_id":null,"atm_payable":2.63,"receipt_user_name":"哈哈","order_type":"2"}]
     */

    private String status;
    private String info;
    private int totalPage;
    private List<DataBean> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * receipt_tel : 13266505288
         * N : 14.21
         * mode : null
         * solution_detail_id : null
         * K : 15.06
         * paymentCreateDate : null
         * user_service_id : 110011
         * create_date : 2016-07-04 10:28:38
         * user_service_name : 深圳市光明新区楼村芭田种植基地
         * atm_fact : null
         * P : 16.15
         * amt_pay : null
         * qty_fact : null
         * order_head_id : DH1467599161288
         * user_id : 1119200
         * order_from : 2
         * note : null
         * reserve4 : null
         * user_tel : 13652025632
         * user_type : 10,20
         * atm_shipping : 0.0
         * reserve3 : null
         * reserve2 : 1
         * creator_id : 1119200
         * reserve1 : 2.63
         * organism : null
         * discount : null
         * pay_flag_id : null
         * creator_desc : 111920
         * receipt_addr : 北京市密云县 寂图默默寞吗
         * qty_order : 3.0
         * atm_order : 7.89
         * pay_date : null
         * user_desc : 黄锦飞
         * payment_id : null
         * order_flag_id : 1
         * addr_delivery : null
         * Formula_head_id : null
         * atm_payable : 7.89
         * receipt_user_name : 哈哈
         * order_type : 2
         */

        private String receipt_tel;
        private double N;
        private String mode;
        private String solution_detail_id;
        private double K;
        private String paymentCreateDate;
        private String user_service_id;
        private String create_date;
        private String user_service_name;
        private String atm_fact;
        private double P;
        private String amt_pay;
        private String qty_fact;
        private String order_head_id;
        private String user_id;
        private String order_from;
        private String note;
        private String reserve4;
        private String user_tel;
        private String user_type;
        private double atm_shipping;
        private String reserve3;
        private String reserve2;
        private String creator_id;
        private String reserve1;
        private String organism;
        private String discount;
        private String pay_flag_id;
        private String creator_desc;
        private String receipt_addr;
        private double qty_order;
        private double atm_order;
        private String pay_date;
        private String user_desc;
        private String payment_id;
        private int order_flag_id;
        private String addr_delivery;
        private String Formula_head_id;
        private double atm_payable;
        private String receipt_user_name;
        private String order_type;

        public String getReceipt_tel() {
            return receipt_tel;
        }

        public void setReceipt_tel(String receipt_tel) {
            this.receipt_tel = receipt_tel;
        }

        public double getN() {
            return N;
        }

        public void setN(double N) {
            this.N = N;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getSolution_detail_id() {
            return solution_detail_id;
        }

        public void setSolution_detail_id(String solution_detail_id) {
            this.solution_detail_id = solution_detail_id;
        }

        public double getK() {
            return K;
        }

        public void setK(double K) {
            this.K = K;
        }

        public String getPaymentCreateDate() {
            return paymentCreateDate;
        }

        public void setPaymentCreateDate(String paymentCreateDate) {
            this.paymentCreateDate = paymentCreateDate;
        }

        public String getUser_service_id() {
            return user_service_id;
        }

        public void setUser_service_id(String user_service_id) {
            this.user_service_id = user_service_id;
        }

        public String getCreate_date() {
            return create_date;
        }

        public void setCreate_date(String create_date) {
            this.create_date = create_date;
        }

        public String getUser_service_name() {
            return user_service_name;
        }

        public void setUser_service_name(String user_service_name) {
            this.user_service_name = user_service_name;
        }

        public String getAtm_fact() {
            return atm_fact;
        }

        public void setAtm_fact(String atm_fact) {
            this.atm_fact = atm_fact;
        }

        public double getP() {
            return P;
        }

        public void setP(double P) {
            this.P = P;
        }

        public String getAmt_pay() {
            return amt_pay;
        }

        public void setAmt_pay(String amt_pay) {
            this.amt_pay = amt_pay;
        }

        public String getQty_fact() {
            return qty_fact;
        }

        public void setQty_fact(String qty_fact) {
            this.qty_fact = qty_fact;
        }

        public String getOrder_head_id() {
            return order_head_id;
        }

        public void setOrder_head_id(String order_head_id) {
            this.order_head_id = order_head_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getOrder_from() {
            return order_from;
        }

        public void setOrder_from(String order_from) {
            this.order_from = order_from;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getReserve4() {
            return reserve4;
        }

        public void setReserve4(String reserve4) {
            this.reserve4 = reserve4;
        }

        public String getUser_tel() {
            return user_tel;
        }

        public void setUser_tel(String user_tel) {
            this.user_tel = user_tel;
        }

        public String getUser_type() {
            return user_type;
        }

        public void setUser_type(String user_type) {
            this.user_type = user_type;
        }

        public double getAtm_shipping() {
            return atm_shipping;
        }

        public void setAtm_shipping(double atm_shipping) {
            this.atm_shipping = atm_shipping;
        }

        public String getReserve3() {
            return reserve3;
        }

        public void setReserve3(String reserve3) {
            this.reserve3 = reserve3;
        }

        public String getReserve2() {
            return reserve2;
        }

        public void setReserve2(String reserve2) {
            this.reserve2 = reserve2;
        }

        public String getCreator_id() {
            return creator_id;
        }

        public void setCreator_id(String creator_id) {
            this.creator_id = creator_id;
        }

        public String getReserve1() {
            return reserve1;
        }

        public void setReserve1(String reserve1) {
            this.reserve1 = reserve1;
        }

        public String getOrganism() {
            return organism;
        }

        public void setOrganism(String organism) {
            this.organism = organism;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public String getPay_flag_id() {
            return pay_flag_id;
        }

        public void setPay_flag_id(String pay_flag_id) {
            this.pay_flag_id = pay_flag_id;
        }

        public String getCreator_desc() {
            return creator_desc;
        }

        public void setCreator_desc(String creator_desc) {
            this.creator_desc = creator_desc;
        }

        public String getReceipt_addr() {
            return receipt_addr;
        }

        public void setReceipt_addr(String receipt_addr) {
            this.receipt_addr = receipt_addr;
        }

        public double getQty_order() {
            return qty_order;
        }

        public void setQty_order(double qty_order) {
            this.qty_order = qty_order;
        }

        public double getAtm_order() {
            return atm_order;
        }

        public void setAtm_order(double atm_order) {
            this.atm_order = atm_order;
        }

        public String getPay_date() {
            return pay_date;
        }

        public void setPay_date(String pay_date) {
            this.pay_date = pay_date;
        }

        public String getUser_desc() {
            return user_desc;
        }

        public void setUser_desc(String user_desc) {
            this.user_desc = user_desc;
        }

        public String getPayment_id() {
            return payment_id;
        }

        public void setPayment_id(String payment_id) {
            this.payment_id = payment_id;
        }

        public int getOrder_flag_id() {
            return order_flag_id;
        }

        public void setOrder_flag_id(int order_flag_id) {
            this.order_flag_id = order_flag_id;
        }

        public String getAddr_delivery() {
            return addr_delivery;
        }

        public void setAddr_delivery(String addr_delivery) {
            this.addr_delivery = addr_delivery;
        }

        public String getFormula_head_id() {
            return Formula_head_id;
        }

        public void setFormula_head_id(String Formula_head_id) {
            this.Formula_head_id = Formula_head_id;
        }

        public double getAtm_payable() {
            return atm_payable;
        }

        public void setAtm_payable(double atm_payable) {
            this.atm_payable = atm_payable;
        }

        public String getReceipt_user_name() {
            return receipt_user_name;
        }

        public void setReceipt_user_name(String receipt_user_name) {
            this.receipt_user_name = receipt_user_name;
        }

        public String getOrder_type() {
            return order_type;
        }

        public void setOrder_type(String order_type) {
            this.order_type = order_type;
        }
    }
}
