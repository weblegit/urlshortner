package com.weblegit.urlshortner;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "urlshortner", description = "Operations pertaining to url shortner")
public class Controller {

	private static final Logger logger = LoggerFactory.getLogger(Controller.class);

	@Autowired
	private UrlShortnerService service;

	@ApiOperation(value = "Create a short url for the provided URL", response = ShortUrl.class, produces = "application/json")
	@RequestMapping(value = "shorturl", method = RequestMethod.POST)
	public ShortUrl createShortUrl(@RequestParam String url) {
		logger.info("Request for url shortening for {}", url);
		return service.createShortUrl(url, null);
	}

	@ApiOperation(value = "List all the short urls and their corresponding redirect urls", response = Iterable.class, produces = "application/json")
	@RequestMapping(value = "shorturl/list", method = RequestMethod.GET)
	public List<ShortUrl> listShortUrls() {
		return service.getShortUrlList();
	}

	@ApiOperation(value = "Update the short code to point to another redirect URL", response = ShortUrl.class, produces = "application/json")
	@RequestMapping(value = "shorturl", method = RequestMethod.PATCH)
	public ShortUrl updateShortUrl(@RequestParam String existingCode, @RequestParam String newUrl) {
		return service.updateShortUrl(existingCode, newUrl);
	}

	@ApiOperation(value = "Deletes the short url for the given code", response = Void.class, produces = "application/json")
	@RequestMapping(value = "shorturl", method = RequestMethod.DELETE)
	public void deleteShortUrl(@RequestParam String code) {
		service.deleteShortUrl(code);
	}

}
