package tests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class BookingTests {
    String createToken;
    int bookingId;

    @BeforeClass
    public void loginToken()
    {
        String endPoint = "https://restful-booker.herokuapp.com/auth/";
        String body = """
                {
                    "username" : "admin",
                    "password" : "password123"
                }""";
        ValidatableResponse validatableResponserese = given().body(body)
                .header("Content-Type" , "application/json")
                .when().post(endPoint).then();
        Response response = validatableResponserese.extract().response();
        JsonPath jsonPath = response.jsonPath();
        createToken = jsonPath.getString("token");
        System.out.println( " token: " + createToken);

    }
    @Test(priority = 0)
    public void testCreateBooking(){
        String endPoint ="https://restful-booker.herokuapp.com/booking/";
        String body = """
                {
                    "firstname" : "Jim",
                    "lastname" : "Brown",
                    "totalprice" : 111,
                    "depositpaid" : true,
                    "bookingdates" : {
                        "checkin" : "2018-01-01",
                        "checkout" : "2019-01-01"
                    },
                    "additionalneeds" : "Breakfast"
                }""";
        ValidatableResponse validatableResponse = given().body(body)
                .header("Content-Type" , "application/json")
                .when().post(endPoint).then();
        validatableResponse.body("booking.firstname" , equalTo("Jim"));
        validatableResponse.statusCode(200);
        Response response = validatableResponse.extract().response();
        JsonPath jsonPath = response.jsonPath();
        bookingId = jsonPath.getInt("bookingid");
        validatableResponse.log().all();
    }

    @Test(priority = 1 , dependsOnMethods = "testCreateBooking")
    public void testEditBooking() {
        String endPoint = "https://restful-booker.herokuapp.com/booking/" + bookingId;
        String body = """
                {
                    "firstname" : "Asmaa",
                    "lastname" : "Ismail",
                    "totalprice" : 111,
                    "depositpaid" : true,
                    "bookingdates" : {
                        "checkin" : "2018-01-01",
                        "checkout" : "2019-01-01"
                    },
                    "additionalneeds" : "Breakfast"
                }""";
        ValidatableResponse validatableResponse = given()
                .body(body)
                .header("Content-Type" , "application/json")
                .header("cookie" , "token=" + createToken)
                .log().all()
                .when().put(endPoint).then();
        validatableResponse.header("Content-Type" , "application/json; charset=utf-8");
        validatableResponse.body("firstname" , equalTo("Asmaa"));
        validatableResponse.body("lastname" , equalTo("Ismail"));
        validatableResponse.statusCode(200);
    }
    @Test(priority = 2 , dependsOnMethods = "testEditBooking")
    public void testGetBooking()
    {
        String endPoint = "https://restful-booker.herokuapp.com/booking/" + bookingId;
        ValidatableResponse validatableResponse = given()
                .header("Content-Type" , "application/json; charset=utf-8")
                .log().all()
                .when().get(endPoint).then();
        validatableResponse.body("firstname" , equalTo("Asmaa"));
        validatableResponse.body("lastname" , equalTo("Ismail"));
        validatableResponse.statusCode(200);
    }
    @Test(priority = 3 , dependsOnMethods = "testGetBooking")
    public void testDeleteBooking()
    {
        String endPoint = "https://restful-booker.herokuapp.com/booking/" + bookingId;
        ValidatableResponse validatableResponse = given()
                .header("Content-Type" , "application/json")
                .header("cookie" , "token=" + createToken)
                .when().delete(endPoint).then();
        Response response = validatableResponse.extract().response();
        JsonPath jsonPath = response.jsonPath();
        validatableResponse.statusCode(201);
    }
}
