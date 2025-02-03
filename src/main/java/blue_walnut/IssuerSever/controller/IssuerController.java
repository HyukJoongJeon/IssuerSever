package blue_walnut.IssuerSever.controller;

import blue_walnut.IssuerSever.model.CardInfo;
import blue_walnut.IssuerSever.model.PaymentReq;
import blue_walnut.IssuerSever.model.PaymentRes;
import blue_walnut.IssuerSever.service.IssuerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/issuer")
public class IssuerController {

    public final IssuerService issuerService;

    @GetMapping("/validateCard")
    public Boolean validateCard(@RequestParam String userCi,
                                @RequestParam String cardNo,
                                @RequestParam String cardPwd,
                                @RequestParam String vldDt) {
        return issuerService.validateCard(new CardInfo(userCi, cardNo, vldDt, cardPwd));
    }
    @PostMapping("/payment")
    public PaymentRes payment(@RequestBody PaymentReq req) {
        return issuerService.payment(req);
    }
}
