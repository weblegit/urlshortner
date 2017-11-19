package com.weblegit.urlshortner;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UrlShortnerServiceIT {

	@Autowired
	private UrlShortnerService service;

	@Test
	public void testShortnerOperations() {
		try {
			ShortUrl shortUrl = service.createShortUrl("https://weblegit.com", null);

			Assert.assertNotNull(shortUrl);
			Assert.assertEquals("https://weblegit.com", shortUrl.getUrl());

			shortUrl = service.updateShortUrl(shortUrl.getShortCode(), "https://google.com");
			Assert.assertNotNull(shortUrl);
			Assert.assertEquals("https://google.com", shortUrl.getUrl());

			List<ShortUrl> shortUrlList = service.getShortUrlList();

			Assert.assertTrue(shortUrlList.size() > 0);
			for (ShortUrl url : shortUrlList) {
				service.deleteShortUrl(url.getShortCode());
			}

		} catch (Throwable t) {
			fail(t.getMessage());
		}
	}

}
