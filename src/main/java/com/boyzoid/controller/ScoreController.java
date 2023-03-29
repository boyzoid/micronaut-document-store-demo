package com.boyzoid.controller;

import com.boyzoid.service.ScoreService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.mysql.cj.xdevapi.JsonArray;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;


@Controller
public class ScoreController {
    private final ScoreService scoreService;
    private final Integer defaultLimit = 50;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @Get(value = "/list{/limit}", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<Object> list(@Nullable Integer limit ) throws JsonProcessingException {
        ArrayList<Object> scores = scoreService.getScores(!Objects.isNull(limit) ? limit : defaultLimit );
        LinkedHashMap result = new LinkedHashMap();
        result.put("count", scores.size());
        result.put("scores", scores);
        return HttpResponse.ok(result);
    }
}
