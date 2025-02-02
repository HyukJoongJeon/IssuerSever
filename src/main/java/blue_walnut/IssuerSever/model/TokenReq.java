package blue_walnut.IssuerSever.model;

public record TokenReq(String token, String userCi, Boolean isCancel){
}
