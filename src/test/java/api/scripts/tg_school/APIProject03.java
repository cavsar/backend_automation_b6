package api.scripts.tg_school;

import api.pojo_classes.tg_application.UpdatePutStudent;
import api.pojo_classes.tg_school.CreateStudent;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.DBUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class APIProject03 {
    Response response;
    RequestSpecification baseSpec;
    Faker faker = new Faker();



    @BeforeMethod
    public void setAPI(){
        baseSpec = new RequestSpecBuilder().log(LogDetail.ALL)
                .setBaseUri(ConfigReader.getProperty("TGSchoolBaseURI"))
                .setContentType(ContentType.JSON)
                .build();

        DBUtil.createDBConnection();// to establish a database connection in a software application
    }


    @Test
    public void TGAPIProject03(){
/*
1. Create a new user
- Make a POST call for all TechGlobal students.
- Verify that a POST request status is 200 successes.
- Assert that the response time is less than a particular value, say 200ms, to ensure the API's performance is within acceptable limits. ( If it’s more than 200, you can increase the limit )
- Validate first name, last name, email, and dob you sent in the request body is reflected on the Database.
 */
        CreateStudent createStudent= CreateStudent.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .DOB("2000-01-01").build();
        response = RestAssured.given()
                .spec(baseSpec)
                .body(createStudent)
                .when().post("/students")
                .then().log().all()
                .assertThat().statusCode(200)
                .time(Matchers.lessThan(4000l))
                .extract().response();

        int id = response.jsonPath().getInt("id");
        String query = "SELECT * FROM STUDENT WHERE id = " + id;

        List<List<Object>> queryResultList = DBUtil.getQueryResultList(query);


        List<Object> dbResult = queryResultList.get(0);

        BigDecimal dbId = (BigDecimal) dbResult.get(0);
        int dbIdInt = dbId.intValue();


        List<Object> formattedDBResult = new ArrayList<>(dbResult);
        formattedDBResult.set(0, dbIdInt);


        Assert.assertEquals(formattedDBResult,
                Arrays.asList(id,
                        createStudent.getDOB(),
                        createStudent.getEmail(),
                        createStudent.getFirstName(),
                        createStudent.getLastName()));

        /**TASK02 Retrieve a specific user-created
         * Make a GET call for the specific user created.
         * Verify that a GET request status is 200 successes.
         * Assert that the response time is less than a particular value, say 200ms, to ensure the API's
         * performance is within acceptable limits. ( If it’s more than 200, you can increase the limit )
         * Confirm that the user details retrieved from the specific GET API call match exactly with the user data
         * created and stored in the Database
         */
        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students/" + id)
                .then().log().all()
                .assertThat().statusCode(200)
                .time(Matchers.lessThan(4000l))
                .extract().response();

        Assert.assertEquals(formattedDBResult,
                Arrays.asList(id,
                        createStudent.getDOB(),
                        createStudent.getEmail(),
                        createStudent.getFirstName(),
                        createStudent.getLastName()));

        UpdatePutStudent updateStudent = UpdatePutStudent.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .DOB("2002-02-03").build();

        /**TASK03  Update an existing user
         *Make a PUT call to update ANY details of a created TechGlobal student you want to update.
         * Verify that the PUT request status is 200 (success).
         * Assert that the response time is less than a particular value, say 200ms, to ensure the API's
         * performance is within acceptable limits. ( If it’s more than 200, you can increase the limit )
         * Verify that the updates made through the PUT request are accurately reflected and match the
         * corresponding user data in the Database
         */


        response = RestAssured.given()
                .spec(baseSpec)
                .body(updateStudent)
                .when().put("/students/" + id)
                .then().log().all()
                .assertThat().statusCode(200)
                .time(Matchers.lessThan(4000l))
                .extract().response();

        String query1 = "SELECT * FROM STUDENT WHERE id = " + id;

        List<List<Object>> queryResultList1 = DBUtil.getQueryResultList(query1);

        List<Object> dbResult1 = queryResultList1.get(0);

        BigDecimal dbId1 = (BigDecimal) dbResult1.get(0);
        int dbIdInt1 = dbId1.intValue();


        List<Object> formattedDBResult1 = new ArrayList<>(dbResult1);
        formattedDBResult1.set(0, dbIdInt1);

        Assert.assertEquals(formattedDBResult1,
                Arrays.asList(id,
                        updateStudent.getDOB(),
                        updateStudent.getEmail(),
                        updateStudent.getFirstName(),
                        updateStudent.getLastName()));


        /** TASK04 Retrieve a specific user created to confirm the update.
         * Make a GET call for the specific user created again.
         * Verify that a GET request status is 200 successes.
         * Assert that the response time is less than a particular value, say 200ms, to ensure the API's
         * performance is within acceptable limits. ( If it’s more than 200, you can increase the limit )
         * Validate that the information in the response body of a specific user’s GET call is matching with the
         * values you updated, and it is reflected on the Database.
         */

        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students/" + id)
                .then().log().all()
                .assertThat().statusCode(200)
                .time(Matchers.lessThan(4000l))
                .extract().response();

        Assert.assertEquals(formattedDBResult1,
                Arrays.asList(id,
                        updateStudent.getDOB(),
                        updateStudent.getEmail(),
                        updateStudent.getFirstName(),
                        updateStudent.getLastName()));

        /** TASK05  delete the user that you created.
         * Verify that a DELETE request status is 200 success.
         * Assert that the response time is less than a particular value, say 200ms, to ensure the API's
         * performance is within acceptable limits. ( If it’s more than 200, you can increase the limit )
         * Make sure the user created is also removed from the Database
         */


        response = RestAssured.given()
                .spec(baseSpec)
                .when().delete("/students/" + id)
                .then().log().all()
                .assertThat().statusCode(200)
                .time(Matchers.lessThan(4000l))
                .extract().response();

        queryResultList = DBUtil.getQueryResultList(query);

        Assert.assertTrue(queryResultList.isEmpty(),
                " The student with id: " + id + " was not deleted from the database.");
    }
}


