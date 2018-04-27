package com.example.filter;

import com.example.filter.AuditWebFilterTest.TestController;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.reactivex.Single;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Slf4j
@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = {
//                      AuditConfiguration.class, 
//                      MdcConfiguration.class})
@WebFluxTest(controllers = {TestController.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration
public class AuditWebFilterTest {
  
  @Autowired
  private WebTestClient webClient;

  @Test
  public void validateFilterAuditEnabled(){
    
    webClient.get().uri("/getfoo").accept(MediaType.APPLICATION_JSON)
                                  .exchange()
                                  .expectStatus().isOk()
                                  .expectBody(Foo.class);
  }
  
  @RestController
  static class TestController {
    
    @GetMapping(value="/getfoo", produces = MediaType.APPLICATION_JSON_VALUE)
    public Single<Foo> getFoo(){
      return Single.just(new Foo("Obi-Wan Kenobi"));
    }
    
    @PostMapping(value="/postfoo", produces = MediaType.APPLICATION_JSON_VALUE)
    public Single<Foo> postFoo(){
      return Single.just(new Foo("Yoda"));
    }
  }
  
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  static class Foo{
    private String bar;
  }
}
