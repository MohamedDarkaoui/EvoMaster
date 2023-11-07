package org.evomaster.e2etests.spring.examples.adaptivehypermutation


import  org.junit.jupiter.api.AfterAll
import  org.junit.jupiter.api.BeforeAll
import  org.junit.jupiter.api.BeforeEach
import  org.junit.jupiter.api.Test
import  org.junit.jupiter.api.Assertions.*
import  io.restassured.RestAssured
import  io.restassured.RestAssured.given
import  org.evomaster.client.java.controller.SutHandler
import  org.evomaster.client.java.sql.dsl.SqlDsl.sql
import  org.hamcrest.Matchers.*
import  io.restassured.config.JsonConfig
import  io.restassured.path.json.config.JsonPathConfig
import  io.restassured.config.RedirectConfig.redirectConfig
import  org.evomaster.client.java.controller.contentMatchers.NumberMatcher.*




/**
 * This file was automatically generated by EvoMaster on 2020-12-04T12:00:08.402+01:00\[Europe/Oslo\]
 * 
 * The generated test suite contains 15 tests
 * 
 * Covered targets: 179
 * 
 * Used time: 0h 4m 18s
 * 
 * Needed budget for current results: 85%
 * 
 * 
 */
internal class EvoMasterAWHSampleTest {

    
    companion object {
        private val controller : SutHandler = com.foo.rest.examples.spring.adaptivehypermutation.AHypermutationRestController()
        private lateinit var baseUrlOfSut: String
        
        
        @BeforeAll
        @JvmStatic
        fun initClass() {
            controller.setupForGeneratedTest()
            baseUrlOfSut = controller.startSut()
            assertNotNull(baseUrlOfSut)
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
            RestAssured.useRelaxedHTTPSValidation()
            RestAssured.urlEncodingEnabled = false
            RestAssured.config = RestAssured.config()
                .jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE))
                .redirect(redirectConfig().followRedirects(false))
        }
        
        
        @AfterAll
        @JvmStatic
        fun tearDown() {
            controller.stopSut()
        }
    }
    
    
    @BeforeEach
    fun initTest() {
        controller.resetDatabase(null)
        controller.resetStateOfSUT()
    }
    
    
    
    
    @Test
    fun test_0_with500()  {
        val insertions = sql().insertInto("FOO", 21L)
                .d("X", "1296040198")
                .d("Y", "\"evomaster_56_input\"")
                .d("ZC", "-64674")
                .d("ZT", "\"2018-01-16\"")
            .and().insertInto("FOO", 20L)
                .d("X", "131530")
                .d("Y", "\"evomaster_49_input\"")
                .d("ZC", "-1179483")
                .d("ZT", "\"1906-06-10\"")
            .and().insertInto("FOO", 19L)
                .d("X", "724")
                .d("Y", "\"evomaster_57_input\"")
                .d("ZC", "2960")
                .d("ZT", "\"1998-06-23\"")
            .dtos()
        controller.execInsertionsIntoDatabase(insertions)
        
        given().accept("*/*")
                .contentType("application/json")
                .body(" { " + 
                    " \"c\": 860.0, " + 
                    " \"d1\": \"evomaster_55_input\", " + 
                    " \"d2\": \"GN\", " + 
                    " \"t\": \"9238-5-92\" " + 
                    " } ")
                .post("${baseUrlOfSut}/api/foos/761?y=foo")
                .then()
                .statusCode(500) // com/foo/rest/examples/spring/adaptivehypermutation/service/FooRestAPI_38_createFoo
                .assertThat()
                .contentType("text/html")
        
        given().accept("*/*")
                .get("${baseUrlOfSut}/api/foos/761")
                .then()
                .statusCode(404)
                .assertThat()
                .contentType("")
                .body(isEmptyOrNullString())
    }
    
    
    @Test
    fun test_1()  {
        val insertions = sql().insertInto("FOO", 21L)
                .d("X", "1073739543")
                .d("Y", "\"efUmaUUerU3UU_inUu\"")
                .d("ZC", "124")
                .d("ZT", "\"1900-09-17\"")
            .and().insertInto("FOO", 20L)
                .d("X", "4138")
                .d("Y", "\"fxolUsteY_914_inpUtA\"")
                .d("ZC", "384")
                .d("ZT", "\"1919-04-22\"")
            .and().insertInto("FOO", 19L)
                .d("X", "1073206527")
                .d("Y", "\"orf.h2.mvstore.db.MVTablec\"")
                .d("ZC", "8176")
                .d("ZT", "\"1900-01-11\"")
            .dtos()
        controller.execInsertionsIntoDatabase(insertions)
        
        given().accept("*/*")
                .contentType("application/json")
                .body(" { " + 
                    " \"c\": 100.0, " + 
                    " \"d1\": \"efUmaUUerU3UU_inUuU\", " + 
                    " \"d2\": \"org.h2.mvstore.db.MVTable\", " + 
                    " \"t\": \"2024-08-14\" " + 
                    " } ")
                .post("${baseUrlOfSut}/api/foos/761?y=foo")
                .then()
                .statusCode(200)
                .assertThat()
                .contentType("text/plain")
                .body(containsString("B1"))
        
        given().accept("application/json")
                .get("${baseUrlOfSut}/api/foos/761")
                .then()
                .statusCode(200)
                .assertThat()
                .contentType("application/json")
                .body("'x'", numberMatches(761.0))
                .body("'y'", containsString("foo"))
                .body("'z'.'c'", numberMatches(100.0))
                .body("'z'.'t'", containsString("2024-08-14"))
                .body("'z'.'d1'", containsString("efUmaUUerU3UU_inUuU"))
                .body("'z'.'d2'", containsString("org.h2.mvstore.db.MVTable"))
    }
    
    
    @Test
    fun test_2()  {
        val insertions = sql().insertInto("FOO", 21L)
                .d("X", "1073742638")
                .d("Y", "\"dpnmaqvfj_568_jnp\"")
                .d("ZC", "417")
                .d("ZT", "\"2056-12-11\"")
            .and().insertInto("FOO", 20L)
                .d("X", "43")
                .d("Y", "\"[\"")
                .d("ZC", "16")
                .d("ZT", "\"1900-10-31\"")
            .and().insertInto("FOO", 19L)
                .d("X", "1073741861")
                .d("Y", "\"eUUsbUUhrU3UU`itUtU\"")
                .d("ZC", "510")
                .d("ZT", "\"2000-10-15\"")
            .dtos()
        controller.execInsertionsIntoDatabase(insertions)
        
        given().accept("*/*")
                .contentType("application/json")
                .body(" { " + 
                    " \"c\": 200.0, " + 
                    " \"t\": \"2008-07-09\" " + 
                    " } ")
                .post("${baseUrlOfSut}/api/foos/761?y=foo")
                .then()
                .statusCode(200)
                .assertThat()
                .contentType("text/plain")
                .body(containsString("B2"))
        
        given().accept("application/json")
                .get("${baseUrlOfSut}/api/foos/761")
                .then()
                .statusCode(200)
                .assertThat()
                .contentType("application/json")
                .body("'x'", numberMatches(761.0))
                .body("'y'", containsString("foo"))
                .body("'z'.'c'", numberMatches(200.0))
                .body("'z'.'t'", containsString("2008-07-09"))
    }
    
    
    @Test
    fun test_3()  {
        val insertions = sql().insertInto("FOO", 21L)
                .d("X", "1077985119")
                .d("Y", "\"evomaster_481_input\"")
                .d("ZC", "64")
                .d("ZT", "\"2016-06-11\"")
            .and().insertInto("FOO", 20L)
                .d("X", "1073217599")
                .d("Y", "\"evomUUteVU6A1UinUmt\"")
                .d("ZC", "-288")
                .d("ZT", "\"1984-01-12\"")
            .and().insertInto("FOO", 19L)
                .d("X", "304")
                .d("Y", "\"yiYzMOQfhOv9op\"")
                .d("ZC", "0")
                .d("ZT", "\"2030-02-09\"")
            .dtos()
        controller.execInsertionsIntoDatabase(insertions)
        
        given().accept("*/*")
                .contentType("application/json")
                .body(" { " + 
                    " \"c\": 300.0, " + 
                    " \"d1\": \"efUmaUUerU3UU_inUuU\", " + 
                    " \"d2\": \"evomaster_614_input\", " + 
                    " \"t\": \"1992-04-30\" " + 
                    " } ")
                .post("${baseUrlOfSut}/api/foos/761?y=foo")
                .then()
                .statusCode(200)
                .assertThat()
                .contentType("text/plain")
                .body(containsString("B3"))
        
        given().accept("application/json")
                .get("${baseUrlOfSut}/api/foos/761")
                .then()
                .statusCode(200)
                .assertThat()
                .contentType("application/json")
                .body("'x'", numberMatches(761.0))
                .body("'y'", containsString("foo"))
                .body("'z'.'c'", numberMatches(300.0))
                .body("'z'.'t'", containsString("1992-04-30"))
                .body("'z'.'d1'", containsString("efUmaUUerU3UU_inUuU"))
                .body("'z'.'d2'", containsString("evomaster_614_input"))
    }
    
    
    @Test
    fun test_4()  {
        val insertions = sql().insertInto("FOO", 11L)
                .d("X", "840")
                .d("Y", "\"wxidntGoxb2\"")
                .d("ZC", "410")
                .d("ZT", "\"2064-01-12\"")
            .and().insertInto("FOO", 10L)
                .d("X", "0")
                .d("Y", "\"RiF7DjDRUh9PA\"")
                .d("ZC", "16777497")
                .d("ZT", "\"2048-01-09\"")
            .dtos()
        controller.execInsertionsIntoDatabase(insertions)
        
        given().accept("*/*")
                .delete("${baseUrlOfSut}/api/foos/0")
                .then()
                .statusCode(200)
                .assertThat()
                .contentType("")
                .body(isEmptyOrNullString())
    }
    
    
    @Test
    fun test_5()  {
        val insertions = sql().insertInto("FOO", 21L)
                .d("X", "1073741823")
                .d("Y", "\"evgmaster_113_input\"")
                .d("ZC", "-8192")
                .d("ZT", "\"2100-07-19\"")
            .and().insertInto("FOO", 20L)
                .d("X", "0")
                .d("Y", "\"UvomaslUU_49_UUpU\"")
                .d("ZC", "0")
                .d("ZT", "\"2000-04-20\"")
            .and().insertInto("FOO", 19L)
                .d("X", "42")
                .d("Y", "\"evomaster_114_input\"")
                .d("ZC", "524288")
                .d("ZT", "\"1992-07-29\"")
            .dtos()
        controller.execInsertionsIntoDatabase(insertions)
        
        given().accept("*/*")
                .contentType("application/json")
                .body(" { " + 
                    " \"c\": -3236.0, " + 
                    " \"d1\": \"evomasxer_55_input\", " + 
                    " \"d2\": \"GN7\", " + 
                    " \"d3\": \"evomaster_859_input\", " + 
                    " \"t\": \"2064-01-22\" " + 
                    " } ")
                .post("${baseUrlOfSut}/api/foos/761?y=foo")
                .then()
                .statusCode(200)
                .assertThat()
                .contentType("text/plain")
                .body(containsString("B0B5"))
        
        given().accept("application/json")
                .get("${baseUrlOfSut}/api/foos/761")
                .then()
                .statusCode(200)
                .assertThat()
                .contentType("application/json")
                .body("'x'", numberMatches(761.0))
                .body("'y'", containsString("foo"))
                .body("'z'.'c'", numberMatches(-3236.0))
                .body("'z'.'t'", containsString("2064-01-22"))
                .body("'z'.'d1'", containsString("evomasxer_55_input"))
                .body("'z'.'d2'", containsString("GN7"))
                .body("'z'.'d3'", containsString("evomaster_859_input"))
    }
    
    
    @Test
    fun test_6()  {
        val insertions = sql().insertInto("FOO", 21L)
                .d("X", "1073741775")
                .d("Y", "\"abQUdLQbnO4OQU_SpSp_gn\"")
                .d("ZC", "-18")
                .d("ZT", "\"2068-01-13\"")
            .and().insertInto("FOO", 20L)
                .d("X", "928")
                .d("Y", "\"efUmaUUerU3UU_inUuU\"")
                .d("ZC", "-48")
                .d("ZT", "\"2100-01-31\"")
            .and().insertInto("FOO", 19L)
                .d("X", "42")
                .d("Y", "\"UWb/)h2,USwUsraUU\\\\,bKVCTdTug1S\"")
                .d("ZC", "24")
                .d("ZT", "\"1900-12-31\"")
            .dtos()
        controller.execInsertionsIntoDatabase(insertions)
        
        given().accept("*/*")
                .contentType("application/json")
                .body(" { " + 
                    " \"c\": 0.0, " + 
                    " \"d2\": \"org.h2.mvstore.db.MVTable\", " + 
                    " \"t\": \"2020-06-21\" " + 
                    " } ")
                .post("${baseUrlOfSut}/api/foos/761?y=foo")
                .then()
                .statusCode(200)
                .assertThat()
                .contentType("text/plain")
                .body(containsString("B0B4B5"))
        
        given().accept("application/json")
                .get("${baseUrlOfSut}/api/foos/761")
                .then()
                .statusCode(200)
                .assertThat()
                .contentType("application/json")
                .body("'x'", numberMatches(761.0))
                .body("'y'", containsString("foo"))
                .body("'z'.'c'", numberMatches(0.0))
                .body("'z'.'t'", containsString("2020-06-21"))
                .body("'z'.'d2'", containsString("org.h2.mvstore.db.MVTable"))
    }
    
    
    @Test
    fun test_7()  {
        
        given().accept("application/json")
                .get("${baseUrlOfSut}/api/foos")
                .then()
                .statusCode(200)
                .assertThat()
                .contentType("application/json")
                .body("size()", equalTo(0))
                .body("isEmpty()", `is`(true))
    }
    
    
    @Test
    fun test_8()  {
        
        given().accept("application/json")
                .get("${baseUrlOfSut}/api/bars")
                .then()
                .statusCode(200)
                .assertThat()
                .contentType("application/json")
                .body("size()", equalTo(0))
                .body("isEmpty()", `is`(true))
    }
    
    
    @Test
    fun test_9()  {
        val insertions = sql().insertInto("FOO", 14L)
                .d("X", "196")
                .d("Y", "\"BDJycbEZEdQnoZ4\"")
                .d("ZC", "-876170080")
                .d("ZT", "\"1919-12-23\"")
            .and().insertInto("FOO", 13L)
                .d("X", "142")
                .d("Y", "\"xn\"")
                .d("ZC", "887")
                .d("ZT", "\"2070-02-20\"")
            .and().insertInto("FOO", 12L)
                .d("X", "106")
                .d("Y", "\"salW_G\"")
                .d("ZC", "983")
                .d("ZT", "\"2087-06-28\"")
            .dtos()
        controller.execInsertionsIntoDatabase(insertions)
        
        given().accept("application/json")
                .get("${baseUrlOfSut}/api/bars")
                .then()
                .statusCode(200)
                .assertThat()
                .contentType("application/json")
                .body("size()", equalTo(0))
                .body("isEmpty()", `is`(true))
        
        given().accept("*/*")
                .delete("${baseUrlOfSut}/api/foos/765")
                .then()
                .statusCode(404)
                .assertThat()
                .contentType("")
                .body(isEmptyOrNullString())
        
        given().accept("application/json")
                .get("${baseUrlOfSut}/api/foos")
                .then()
                .statusCode(200)
                .assertThat()
                .contentType("application/json")
                .body("size()", equalTo(3))
                .body("'x'", hasItem(numberMatches(106.0)))
                .body("'y'", hasItem(containsString("salW_G")))
                .body("'x'", hasItem(numberMatches(142.0)))
                .body("'y'", hasItem(containsString("xn")))
                .body("'x'", hasItem(numberMatches(196.0)))
                .body("'y'", hasItem(containsString("BDJycbEZEdQnoZ4")))
        
        given().accept("application/json")
                .get("${baseUrlOfSut}/api/bars")
                .then()
                .statusCode(200)
                .assertThat()
                .contentType("application/json")
                .body("size()", equalTo(0))
                .body("isEmpty()", `is`(true))
        
        given().accept("*/*")
                .get("${baseUrlOfSut}/api/foos/46")
                .then()
                .statusCode(404)
                .assertThat()
                .contentType("")
                .body(isEmptyOrNullString())
    }
    
    
    @Test
    fun test_10()  {
        val insertions = sql().insertInto("FOO", 21L)
                .d("X", "1073741823")
                .d("Y", "\"evomaster_56_input\"")
                .d("ZC", "-8192")
                .d("ZT", "\"2000-05-20\"")
            .and().insertInto("FOO", 20L)
                .d("X", "131530")
                .d("Y", "\"evomaster_49_inpur\"")
                .d("ZC", "1024")
                .d("ZT", "\"2064-06-09\"")
            .and().insertInto("FOO", 19L)
                .d("X", "724")
                .d("Y", "\"evomUstUpUUU_UopuU\"")
                .d("ZC", "524288")
                .d("ZT", "\"2000-07-27\"")
            .dtos()
        controller.execInsertionsIntoDatabase(insertions)
        
        given().accept("*/*")
                .contentType("application/json")
                .body(" { " + 
                    " \"c\": 860.0, " + 
                    " \"d1\": \"evomaster_55_input\", " + 
                    " \"d2\": \"GN7\", " + 
                    " \"d3\": \"evomaster_68_input\", " + 
                    " \"t\": \"2064-05-22\" " + 
                    " } ")
                .post("${baseUrlOfSut}/api/foos/761?y=foo")
                .then()
                .statusCode(200)
                .assertThat()
                .contentType("text/plain")
                .body(containsString("B0"))
        
        given().accept("application/json")
                .get("${baseUrlOfSut}/api/foos/761")
                .then()
                .statusCode(200)
                .assertThat()
                .contentType("application/json")
                .body("'x'", numberMatches(761.0))
                .body("'y'", containsString("foo"))
                .body("'z'.'c'", numberMatches(860.0))
                .body("'z'.'t'", containsString("2064-05-22"))
                .body("'z'.'d1'", containsString("evomaster_55_input"))
                .body("'z'.'d2'", containsString("GN7"))
                .body("'z'.'d3'", containsString("evomaster_68_input"))
    }
    
    
    @Test
    fun test_11()  {
        
        given().accept("*/*")
                .contentType("application/json")
                .body(" { " + 
                    " \"c\": 828.0, " + 
                    " \"d2\": \"_b2hKk2\", " + 
                    " \"t\": \"StZfCbGXg\" " + 
                    " } ")
                .post("${baseUrlOfSut}/api/foos/369?y=G446BF")
                .then()
                .statusCode(400)
                .assertThat()
                .contentType("text/html")
    }
    
    
    @Test
    fun test_12()  {
        val insertions = sql().insertInto("FOO", 21L)
                .d("X", "1296040198")
                .d("Y", "\"oiBk\"")
                .d("ZC", "-64674")
                .d("ZT", "\"2002-01-16\"")
            .and().insertInto("FOO", 20L)
                .d("X", "131531")
                .d("Y", "\"evomaster_49_input\"")
                .d("ZC", "-1048411")
                .d("ZT", "\"1906-06-10\"")
            .and().insertInto("FOO", 19L)
                .d("X", "724")
                .d("Y", "\"TWP2QqSnl\"")
                .d("ZC", "2964")
                .d("ZT", "\"2000-06-24\"")
            .dtos()
        controller.execInsertionsIntoDatabase(insertions)
        
        given().accept("*/*")
                .contentType("application/json")
                .body(" { " + 
                    " \"c\": 604.0, " + 
                    " \"d1\": \"4qrSUzaRr0arXg\", " + 
                    " \"d2\": \"GN\", " + 
                    " \"t\": \"9238-5-92\" " + 
                    " } ")
                .post("${baseUrlOfSut}/api/foos/761?y=evomaster_38_input")
                .then()
                .statusCode(400)
                .assertThat()
                .contentType("")
        
        given().accept("*/*")
                .get("${baseUrlOfSut}/api/foos/761")
                .then()
                .statusCode(404)
                .assertThat()
                .contentType("")
                .body(isEmptyOrNullString())
    }
    
    
    @Test
    fun test_13()  {
        
        given().accept("*/*")
                .get("${baseUrlOfSut}/api/foos/277")
                .then()
                .statusCode(404)
                .assertThat()
                .contentType("")
                .body(isEmptyOrNullString())
    }
    
    
    @Test
    fun test_14()  {
        
        given().accept("*/*")
                .delete("${baseUrlOfSut}/api/foos/171")
                .then()
                .statusCode(404)
                .assertThat()
                .contentType("")
                .body(isEmptyOrNullString())
    }


}
