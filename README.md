# urlshortner
A simple, scalable and most economical URL Shortener for Web

The Url Shortener is modeled after the existing free tools with extended funtionality to allow the endpoint url to be changed even afterwards.
This is really helpful in situations like:
1. There is new content that you want your users to see with the same URL
2. Recalling an email is not that easy but recalling a short link sent in URL is made possible using this.
      
      
Stack:
1. The application is written in Java with Spring boot       
2. It uses AWS S3 for storage

Scalability & Performance:
1. Scalablity and Performance is directly derrived from AWS S3
2. The S3 bucket could be fronted by Cloudfront for better performance

Cost:
Couple of $$ at the most for regular use cases



Pre-requisite:
1.Install gradle (if you do not have it already)
2. Install Java SDK (minimum Java 8)(if you do not have it already)
3. Aws Account, create one if you do not have it already 

SetUp:
1. Create a bucket in S3 where you want the links to be stored.
2. Create another bucket with the similar name but "-dummy" at the end
3. Create AWS access credentials which have permission to read and write those buckets
4. Open the application.properties file and add the above information
          AWS_ACCESS_KEY=
          AWS_SECRET_KEY=
          AWS_REGION=
          AWS_S3_BUCKET=  
          
5. Run the Application.java if you are using IDE or execute gradle bootrun in the project folder from command line
6. The application should be up and accessible at http://localhost:8090/swagger-ui.html


