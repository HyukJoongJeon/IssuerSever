package blue_walnut.IssuerSever.model;

public record PaymentReq(String userCi, String token, String trTid, Long amount){}
