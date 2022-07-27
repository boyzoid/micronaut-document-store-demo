package com.boyzoid.controller


import com.boyzoid.service.ScoreService
import com.fasterxml.jackson.databind.JsonNode
import groovy.transform.CompileStatic
import io.micronaut.core.annotation.Nullable
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces

@CompileStatic
@Controller('/')
class ScoreController {
	private final ScoreService scoreService
	private final Integer defaultLimit = 50
	
	ScoreController(ScoreService scoreService ){
		this.scoreService = scoreService
	}
	
	@Get("/init")
	@Produces(MediaType.TEXT_PLAIN)
	public String index() {
		scoreService.initDatabase()
		def result = scoreService.initDatabase()
		return 'Data initialized. Number of rows added: ' + result.toString() ;
	}
	
	@Get(uri="/list{/limit}", produces = MediaType.APPLICATION_JSON )
	HttpResponse list( @Nullable Integer limit ){
		List<JsonNode> scores = scoreService.getScores( limit ?: defaultLimit)
		return HttpResponse.ok( [ count: scores.size(), scores: scores ] );
	}
	
	@Get(uri="/bestScores{/limit}", produces = MediaType.APPLICATION_JSON )
	HttpResponse bestScores( @Nullable Integer limit ){
		List<JsonNode> scores = scoreService.getBestScores( limit ?: defaultLimit)
		return HttpResponse.ok( [ count: scores.size(), scores: scores ] );
	}
	
	@Get(uri="/getByScore/{score}", produces = MediaType.APPLICATION_JSON )
	HttpResponse getByScore( Integer score ){
		List<JsonNode> scores = scoreService.getByScore( score )
		return HttpResponse.ok( [ count: scores.size(), scores: scores ] );
	}
	
	@Get(uri="/getByGolfer{/lastName}", produces = MediaType.APPLICATION_JSON )
	HttpResponse search( @Nullable String lastName ){
		List<JsonNode> scores = scoreService.getByGolfer( lastName ?: '')
		return HttpResponse.ok( [ count: scores.size(), scores: scores ] );
	}
	
	@Get(uri="/getRoundsUnderPar", produces = MediaType.APPLICATION_JSON )
	HttpResponse getUnderPar(  ){
		List<JsonNode> scores = scoreService.getRoundsUnderPar()
		return HttpResponse.ok( [ count: scores.size(), scores: scores ] );
	}
	
	@Get(uri="/getAverageScorePerGolfer", produces = MediaType.APPLICATION_JSON )
	HttpResponse getGolferAverage(  ){
		List<JsonNode> golferStats = scoreService.getAverageScorePerGolfer()
		return HttpResponse.ok( [ count: golferStats.size(), golferStats: golferStats ] );
	}
	
	@Get(uri="/getCourseScoringData", produces = MediaType.APPLICATION_JSON )
	HttpResponse getCourseScoringData(  ){
		List<JsonNode> courses = scoreService.getCourseScoringData()
		return HttpResponse.ok( [ count: courses.size(), courses: courses ] );
	}
	
	@Get(uri="/getDetailedScoreInfoPerCourse", produces = MediaType.APPLICATION_JSON )
	HttpResponse getDetailedScoreInfoPerCourse(  ){
		List<JsonNode> courses = scoreService.getDetailedScoreInfoPerCourse()
		return HttpResponse.ok( [ count: courses.size(), courses: courses ] );
	}
}
