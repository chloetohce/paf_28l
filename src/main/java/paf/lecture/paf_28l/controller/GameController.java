package paf.lecture.paf_28l.controller;

import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import paf.lecture.paf_28l.repository.GameRepository;


@RestController
@RequestMapping("/api")
public class GameController {
    @Autowired
    private GameRepository repository;

    @GetMapping(path="/comments/{user}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUserCommentsWithGame(@PathVariable String user, @RequestParam(required = false) Integer limit) {
        Document result = repository.joinGameCommentsByUser(user, Optional.ofNullable(limit));
        System.out.println(user);
        return ResponseEntity.ok(result.toJson());
    }
    
    
}
