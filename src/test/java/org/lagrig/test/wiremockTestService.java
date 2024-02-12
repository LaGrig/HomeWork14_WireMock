package org.lagrig.test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

public class wiremockTestService {

    int port = 9098;
    OkHttpClient client = new OkHttpClient.Builder().build();

    WireMockServer wireMockServer
            = new WireMockServer(new WireMockConfiguration().port(port));

    @BeforeClass
    public void beforeClass(){
       wireMockServer.start();
       WireMock.configureFor(port);
       WireMock.stubFor(
               WireMock.get(
                       WireMock.urlEqualTo("/cross-origin-resource-sharing")
               ).willReturn(
                       WireMock.aResponse().withStatus(222)
               )
       );
    }

    @AfterClass
    public void afterClass(){ wireMockServer.stop(); }

    @Test
    public void checkStatus() throws IOException {
        var request = new Request.Builder()
                .url("http://localhost:" + port +"/cross-origin-resource-sharing")
                .header("Authorization", "Random")
                .build();
        long start = System.currentTimeMillis();
        try (var response = client.newCall(request).execute()){
            long delay = System.currentTimeMillis() - start;
            var code = response.code();
            System.out.println("Actual response code: " + code);
            System.out.println("Actual delay: " + delay + " ms");
            Assert.assertEquals(code, 222, "We are expecting 222 response and actual is:" + code);
            Assert.assertTrue(delay <= 2015, "We are expecting delay more than or equal to 2015 ms and actual is:" + delay);
        }
    }

}
