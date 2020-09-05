package com.in28minutes.microservices.currency_conversion_service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversionController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CurrencyExchangeServiceProxy proxy;
	
	@GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrency( @PathVariable String from, @PathVariable
			String to, @PathVariable BigDecimal quantity) {
		
		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		ResponseEntity<CurrencyConversionBean> rensponseEntity = new RestTemplate()
				.getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}"
						, CurrencyConversionBean.class
						, uriVariables);
		
		CurrencyConversionBean rensponse = rensponseEntity.getBody();
		logger.info("{}", rensponse);
	    rensponse.setConversionMultiple(BigDecimal.valueOf(65));
	    
	    //System.out.println("Printint the response: " + rensponse);
	    
		return new CurrencyConversionBean(
				rensponse.getId(),from, to,  
				quantity,rensponse.getConversionMultiple(), 
				quantity.multiply(rensponse.getConversionMultiple()), 
				rensponse.getPort());
	}

	@GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrencyFeign( @PathVariable String from, @PathVariable
			String to, @PathVariable BigDecimal quantity) {
		
		CurrencyConversionBean rensponse = proxy.restriveExchangeValue(from, to);
		rensponse.setConversionMultiple(BigDecimal.valueOf(15));
		return new CurrencyConversionBean(
				rensponse.getId(),from, to,  
				quantity,rensponse.getConversionMultiple(), 
				quantity.multiply(rensponse.getConversionMultiple()), 
				rensponse.getPort());
	}
}
