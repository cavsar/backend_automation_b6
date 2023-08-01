package api.scripts.tg_school;

import api.pojo_classes.tg_application.CreateUser;
import api.pojo_classes.tg_application.UpdatePatchStudent;
import api.pojo_classes.tg_application.UpdatePutStudent;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.ConfigReader;

import static org.hamcrest.Matchers.*;

public class TGApplication {
    Response response;
    RequestSpecification baseSpec;
    Faker faker = new Faker();

    @BeforeMethod
    public void setAPI() {
        baseSpec = new RequestSpecBuilder().log(LogDetail.ALL)
                .setBaseUri(ConfigReader.getProperty("TGSchoolBaseURI"))
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test
    public void testCallsTG() {
        //Task01 Retrieve a list of all users.
        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students")
                .then().log().all().assertThat()
                .statusCode(200).time(Matchers.lessThan(5000L))
                .body("", hasSize(greaterThanOrEqualTo(2)))
                .body("[1].firstName", equalTo("John"))
                .body("[1].lastName", equalTo("Doe"))
                .extract().response();

        //Task02 Create a new user
        CreateUser createStudent = CreateUser.builder()
                .firstName(faker.name().firstName()).lastName(faker.name().lastName())
                .email(faker.internet().emailAddress()).DOB("2001-01-01")
                .build();

        response = RestAssured.given()
                .spec(baseSpec)
                .body(createStudent)
                .when().post("/students")
                .then().log().all().assertThat().statusCode(200)
                .time(Matchers.lessThan(5000L))
                .body("firstName", equalTo(createStudent.getFirstName()))
                .body("lastName", equalTo(createStudent.getLastName()))
                .body("email", equalTo(createStudent.getEmail()))
                .body("dob", equalTo(createStudent.getDOB()))
                .extract().response();

        //Task03 Retrieve a specific user-created

        int student_id = response.jsonPath().getInt("id");

        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students/" + student_id)
                .then().log().all().assertThat()
                .statusCode(200).time(Matchers.lessThan(4000L))
                .body("firstName", equalTo(createStudent.getFirstName()))
                .body("lastName", equalTo(createStudent.getLastName()))
                .body("email", equalTo(createStudent.getEmail()))
                .body("dob", equalTo(createStudent.getDOB()))
                .extract().response();

        //Task04 Update an existing user

        UpdatePutStudent updatePutStudent  = UpdatePutStudent.builder()
                .firstName(faker.name().firstName()).lastName(faker.name().lastName())
                .email(faker.internet().emailAddress()).DOB("2001-01-01")
                .build();

        response = RestAssured.given()
                .spec(baseSpec)
                .body(updatePutStudent)
                .when().put("/students/" +student_id)
                .then().log().all().assertThat().statusCode(200)
                .time(Matchers.lessThan(5000L))
                .body("firstName", equalTo(updatePutStudent.getFirstName()))
                .body("lastName", equalTo(updatePutStudent.getLastName()))
                .body("email", equalTo(updatePutStudent.getEmail()))
                .body("dob", equalTo(updatePutStudent.getDOB()))
                .extract().response();

        //Task05 Partially update an existing User

        UpdatePatchStudent updatePatchStudent  = UpdatePatchStudent.builder()
                .email(faker.internet().emailAddress()).DOB("2001-03-01")
                .build();

        response = RestAssured.given()
                .spec(baseSpec)
                .body(updatePatchStudent)
                .when().patch("/students/" +student_id)
                .then().log().all().assertThat().statusCode(200)
                .time(Matchers.lessThan(5000L))
                .body("firstName",equalTo(updatePutStudent.getFirstName()))
                .body("lastName",equalTo(updatePutStudent.getLastName()))
                .body("email", equalTo(updatePatchStudent.getEmail()))
                .body("dob", equalTo(updatePatchStudent.getDOB()))
                .extract().response();

        //Task06  Retrieve a list of all users again

        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students")
                .then().log().all().assertThat()
                .statusCode(200).time(Matchers.lessThan(5000L))
                .body("", hasSize(greaterThanOrEqualTo(3)))
                .extract().response();

        // Task07  Retrieve a specific user created to confirm the update.

        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students/" + student_id)
                .then().log().all().assertThat()
                .statusCode(200).time(Matchers.lessThan(6000L))
                .body("firstName", equalTo(updatePutStudent.getFirstName()))
                .body("lastName", equalTo(updatePutStudent.getLastName()))
                .body("email", equalTo(updatePatchStudent.getEmail()))
                .body("dob", equalTo(updatePatchStudent.getDOB()))
                .extract().response();

        //Task08 Delete the user that you created.
        response = RestAssured.given()
                .spec(baseSpec)
                .when().delete("/students/" + student_id)
                .then().log().all().assertThat()
                .statusCode(200).time(Matchers.lessThan(6000L))
                .extract().response();

    }
}