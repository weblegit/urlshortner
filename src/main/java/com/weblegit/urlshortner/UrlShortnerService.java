package com.weblegit.urlshortner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@SuppressWarnings("deprecation")
@Service
public class UrlShortnerService {
	private static final String redirectFile = "<html><head><meta http-equiv='refresh' content='0; url=REPLACE' /></head><body><p><a href='REPLACE'>Go to Page</a></p></body></html>";

	private static final Logger logger = LoggerFactory.getLogger(UrlShortnerService.class);

	public UrlShortnerService(@Value("${AWS_ACCESS_KEY}") String awsAccessKey,
			@Value("${AWS_SECRET_KEY}") String awsSecretKey, @Value("${AWS_REGION}") String region) {
		AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
		AWSCredentialsProvider provider = new StaticCredentialsProvider(credentials);
		this.s3Client = AmazonS3ClientBuilder.standard().withRegion(region).withCredentials(provider).build();
	}

	@Value("${AWS_S3_BUCKET}")
	private String bucket;

	private AmazonS3 s3Client = null;

	private String getObjectCode() {
		while (true) {
			String code = Util.generate();
			try {
				this.s3Client.getObjectMetadata(this.bucket, code);
			} catch (AmazonS3Exception ex) {
				if (ex.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
					return code;
				}
				logger.warn("Unable to get object status", ex);
				throw ex;
			}
		}
	}

	/**
	 * The method creates a short URL code for the given url
	 * 
	 * @param url
	 * @return
	 */
	public ShortUrl createShortUrl(String url, String code) {

		logger.info("storing the url {} in the bucket {}", url, this.bucket);
		// Create the link for the short code
		if (code == null) {
			code = getObjectCode();
		}
		String loadFile = redirectFile.replace("REPLACE", url);
		byte[] fileContentBytes = loadFile.getBytes(StandardCharsets.UTF_8);
		InputStream fileInputStream = new ByteArrayInputStream(fileContentBytes);
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType("text/html");
		metadata.addUserMetadata("url", url);
		metadata.setContentLength(fileContentBytes.length);
		PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucket, code, fileInputStream, metadata)
				.withCannedAcl(CannedAccessControlList.PublicRead);
		this.s3Client.putObject(putObjectRequest);
		createDummyRecord(url, code);
		return new ShortUrl(url, code);
	}

	private void createDummyRecord(String url, String code) {
		// Add a dummy object for pointer
		byte[] dummyFileContentBytes = new String(code).getBytes(StandardCharsets.UTF_8);
		InputStream fileInputStream = new ByteArrayInputStream(dummyFileContentBytes);
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType("text/html");
		metadata.setContentLength(dummyFileContentBytes.length);
		PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucket + "-dummy", code
				+ Base64.encodeBase64String(url.getBytes()), fileInputStream, metadata);
		this.s3Client.putObject(putObjectRequest);
	}

	/**
	 * The method lists down all the short url codes and their corresponding
	 * urls
	 * 
	 * @return
	 */
	public List<ShortUrl> getShortUrlList() {

		ObjectListing objectList = this.s3Client.listObjects(bucket + "-dummy");
		List<ShortUrl> shortUrlList = new LinkedList<>();

		for (S3ObjectSummary s3ObjectSummary : objectList.getObjectSummaries()) {
			String url = s3ObjectSummary.getKey().substring(6);
			shortUrlList.add(new ShortUrl(new String(Base64.decodeBase64(url)), s3ObjectSummary.getKey()
					.substring(0, 6)));
		}
		return shortUrlList;
	}

	/**
	 * This method deletes the url for code
	 * 
	 * @param code
	 */
	public void deleteShortUrl(String code) {
		try {
			// get the object
			ObjectMetadata metaData = this.s3Client.getObjectMetadata(this.bucket, code);
			String url = metaData.getUserMetaDataOf("url");
			logger.info("The url to be deleted {}", url);
			this.s3Client.deleteObject(this.bucket, code);
			this.s3Client.deleteObject(this.bucket + "-dummy", code + Base64.encodeBase64String(url.getBytes()));

		} catch (AmazonS3Exception ex) {
			if (ex.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
				return;
			}
			logger.warn("Unable to get object status", ex);
			throw ex;
		}

	}

	/**
	 * This method updates the short url with newer end point
	 * 
	 * @param code
	 * @param url
	 */
	public ShortUrl updateShortUrl(String code, String url) {
		this.deleteShortUrl(code);
		this.createShortUrl(url, code);
		return new ShortUrl(url, code);
	}
}
