package com.example.filter.web.rest;

import java.util.concurrent.TimeUnit;

import com.example.filter.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.reactivex.Observable;
import io.reactivex.Single;

@RestController
public class DemoRestController {
  final Logger log = LoggerFactory.getLogger(DemoRestController.class);

  @GetMapping(value = "/rx/stream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
  public Observable<Person> reactiveVersion2() {
    return Observable
        .just(new Person(15, "Jim", "Raynor"),
            new Person(18, "Sarah", "Kerrigan"),
            new Person(16, "Tychus", "Findlay"),
            new Person(16, "Matt", "Horner"))
        .concatMap(person -> Observable.just(person).delay(1, TimeUnit.SECONDS)).doOnNext(person -> {
          log.info("person : " + person.getName());
        });
  }

  @GetMapping(value = "/rx/single", produces = MediaType.APPLICATION_JSON_VALUE)
  public Single<Person> reactiveSingleGet() {
    return Single.just(new Person(15, "Zeratul", "Protos"));
  }

  @PostMapping(value = "/rx/singlepost", produces = MediaType.APPLICATION_JSON_VALUE)
  public Single<Person> reactiveSinglePost(@RequestBody Person person) {
    log.info("rest post: " + person.getName());
    person.setName(person.getName() + " updated");
    return Single.just(person);
  }
}
