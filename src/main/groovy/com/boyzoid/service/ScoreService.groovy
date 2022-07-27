package com.boyzoid.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.mysql.cj.xdevapi.AddResult
import com.mysql.cj.xdevapi.Client
import com.mysql.cj.xdevapi.ClientFactory
import com.mysql.cj.xdevapi.Collection
import com.mysql.cj.xdevapi.DbDoc
import com.mysql.cj.xdevapi.DocResult
import com.mysql.cj.xdevapi.JsonArray
import com.mysql.cj.xdevapi.JsonParser
import com.mysql.cj.xdevapi.Schema
import com.mysql.cj.xdevapi.Session
import com.mysql.cj.xdevapi.SqlResult
import com.mysql.cj.xdevapi.SqlStatement
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton


import com.mysql.cj.xdevapi.SessionFactory

@Singleton
class ScoreService {
	private final ObjectMapper objectMapper = new ObjectMapper()
	private final ClientFactory clientFactory = new ClientFactory()
	private final Client cli
	@Value('mysqlx://${demo.user}:${demo.password}@${demo.host}:${demo.port}')
	private String url
	@Value('${demo.schema}')
	private String schema
	@Value('${demo.collection}')
	private String collection
	
	Integer initDatabase(){
		Session session = getSession()
		Schema db = session.getSchema(schema)
		db.dropCollection(collection)
		db.createCollection(collection)
		Collection col = db.getCollection(collection)
		JsonArray scores = getDemoData()
		AddResult result = col.add( scores ).execute()
		session.close()
		return result.getAffectedItemsCount()
	}
	
	List getScores ( Integer limit ){
		Session session = getSession()
		Schema  db = session.getSchema(schema)
		Collection col = db.getCollection(collection)
		DocResult result = col.find().limit(limit).execute()
		List<DbDoc> docs = result.fetchAll()
		session.close()
		docs.collect{ doc -> objectMapper.readTree( doc.toString() ) }
	}
	
	List getBestScores ( Integer limit ){
		Session session = getSession()
		Schema  db = session.getSchema(schema)
		Collection col = db.getCollection(collection)
		DocResult result = col.find()
			.fields(
				'firstName as firstName',
				'lastName as lastName',
				'score as score',
				'course.name as courseName',
				'`date` as datePlayed'
			)
			.sort(
			        'score asc',
			        '`date` desc'
			).limit(limit).execute()
		List<DbDoc> docs = result.fetchAll()
		session.close()
		docs.collect{ doc -> objectMapper.readTree( doc.toString() ) }
	}
	
	List getByScore ( Integer score ){
		Session session = getSession()
		Schema  db = session.getSchema(schema)
		Collection col = db.getCollection(collection)
		DocResult result = col.find('score = :score')
			.bind('score', score)
			.sort('`date` desc')
			.execute()
		List<DbDoc> docs = result.fetchAll()
		session.close()
		docs.collect{ doc -> objectMapper.readTree( doc.toString() ) }
	}
	
	List getByGolfer ( str ){
		Session session = getSession()
		Schema  db = session.getSchema(schema)
		Collection col = db.getCollection(collection)
		DocResult result =col.find("lastName like :lName").bind("lName", str + "%").execute()
		List<DbDoc> docs = result.fetchAll()
		session.close()
		docs.collect{ doc -> objectMapper.readTree( doc.toString() ) }
	}
	
	List getRoundsUnderPar (){
		Session session = getSession()
		Schema  db = session.getSchema(schema)
		Collection col = db.getCollection(collection)
		DocResult result = col.find( 'score < course.par').sort('`date` desc').execute()
		List<DbDoc> docs = result.fetchAll()
		session.close()
		docs.collect{ doc -> objectMapper.readTree( doc.toString() ) }
	}
	
	List getAverageScorePerGolfer (){
		Session session = getSession()
		Schema  db = session.getSchema(schema)
		Collection col = db.getCollection(collection)
		DocResult result = col.find( )
				.fields(
						'CONCAT(lastName, \', \', firstName) as golfer',
						'CAST(AVG(score) AS DECIMAL(4,2)) as avg',
						'CAST(MIN(score) AS SIGNED) AS lowestScore',
						'CAST(MAX(score) AS SIGNED) AS highestScore',
						'COUNT(score) as numberOfRounds'
				)
				.groupBy('lastName', 'firstName')
				.execute()
		List<DbDoc> docs = result.fetchAll()
		session.close()
		docs.collect{ doc -> objectMapper.readTree( doc.toString() ) }
	}
	
	List getCourseScoringData (){
		Session session = getSession()
		Schema  db = session.getSchema(schema)
		Collection col = db.getCollection(collection)
		DocResult result = col.find( )
				.fields(
						'course.name as courseName',
						'course.slope as slope',
						'course.rating as rating',
						'CAST(avg(score) AS DECIMAL(4,2)) AS avg',
						'CAST(MIN(score) AS SIGNED) AS lowestScore',
						'CAST(MAX(score) AS SIGNED) AS highestScore',
						'count(score) AS numberOfRounds'
				)
				.groupBy('course.name')
				.sort('course.name')
				.execute()
		List<DbDoc> docs = result.fetchAll()
		session.close()
		docs.collect{ doc -> objectMapper.readTree( doc.toString() ) }
	}
	
	List getDetailedScoreInfoPerCourse(){
		Session session = getSession()
		String sql = "with rounds as ( " +
				"select doc->> '\$.firstName' as firstName, " +
				"       doc->> '\$.lastName' as lastName, " +
				"       doc->> '\$.score' * 1 as score, " +
				"       doc->> '\$.course.name' as courseName, " +
				"       doc->> '\$.date' as datePlayed " +
				"from mn_demo.scores ), " +
				"roundsAgg as ( " +
				"select courseName, min( score ) lowScore from rounds group by courseName " +
				") " +
				"select  JSON_OBJECT('courseName', ra.courseName, " +
				"                    'score', CAST(ra.lowScore AS SIGNED), " +
				"                    'golfers', ( " +
				"                        select JSON_ARRAYAGG( " +
				"                            JSON_OBJECT('golfer', concat(r.firstName, ' ', r.lastName), 'datePlayed', r.datePlayed) " +
				"                        ) " +
				"                        from rounds r " +
				"                        where r.score = ra.lowScore " +
				"                        and r.courseName = ra.courseName) " +
				"        ) as data " +
				" " +
				"from roundsAgg ra " +
				"group by ra.courseName " +
				"order by ra.courseName;"
		SqlStatement query = session.sql( sql )
		SqlResult docs = query.execute()
		session.close()
		docs.collect{ doc -> objectMapper.readTree( doc.getString(0) ) }
	}
	
	private Session getSession(){
		Client cli = clientFactory.getClient(this.url, '{"pooling":{"enabled":true, "maxSize":8,"maxIdleTime":30000, "queueTimeout":10000} }')
		cli.getSession()
	}
	
	private JsonArray getDemoData(){
		String txt = new File('src/data/scores.json').text
		return JsonParser.parseArray(new StringReader(txt))
	}
}
