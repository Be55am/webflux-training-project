package com.wiredbraincoffee.productapiannotation.controller;

import com.wiredbraincoffee.productapiannotation.model.ScrappedProduct;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * @author bessam on 28/12/2020
 */


@RestController
@RequestMapping("/also-endpoint")
@AllArgsConstructor
public class ScrapperController {




    @GetMapping("{id}")
    public Mono<ScrappedProduct> scrapeData (@PathVariable String id) {
        try {
            // Here we create a document object and use JSoup to fetch the website
            Document doc = Jsoup
                    .connect("https://www.also.com/ec/cms5/2000/ProductDetailData.do?prodId="+id+"&todo=extendedSpecs&_=1609150666498")
                    .header("Cookie", "JSESSIONID=C8D9A5D95A9C88A13B045F4722B0D72A; ROUTEID=.p4; pegasos.lang.6000=en; T_6000=1; wt_nv=1; wt_nv_s=1; _mkto_trk=id:833-IPQ-934&token:_mch-also.com-1609147940912-98983; wt_geid=68934a3e9455fa72420237eb; C_6000=1; AUTH=58843311903748d0874937f08489059b; T_2000=1; wt3_sid=%3B590341552297239%3B677616829660503; wt_mcp_sid=449432348; wt3_eid=%3B590341552297239%7C2160914793600945959%232160914923352622872%3B677616829660503%7C2160914989000720722%232160915038126739740; wt_rla=590341552297239%2C6%2C1609147977634%3B677616829660503%2C2%2C1609149890417")
                    .get();

            return Mono.just(new ScrappedProduct(
                    doc.select("body table tbody tr td").get(1).text(),
                    doc.select("body table tbody tr td").get(4).text(),
                    doc.select("body table tbody tr td").get(6).text(),
                    doc.select("body table tbody tr td").get(8).text(),
                    doc.select("body table tbody tr td").get(13).text(),
                    doc.select("body table tbody tr td").get(15).text(),
                    doc.select("body table tbody tr td").get(17).text()
            ));


            // In case of any IO errors, we want the messages written to the console
        } catch (IOException e) {
            e.printStackTrace();

        }
        return Mono.empty();
    }
}
