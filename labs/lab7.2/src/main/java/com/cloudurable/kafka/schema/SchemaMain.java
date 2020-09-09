package com.fenago.kafka.schema;

import okhttp3.*;

import java.io.IOException;

public class SchemaMain {

    private final static MediaType SCHEMA_CONTENT =
            MediaType.parse("application/vnd.schemaregistry.v1+json");

    private final static String EMPLOYEE_SCHEMA = "{\n" +
            "  \"schema\": \"" +
            "  {" +
            "    \\\"namespace\\\": \\\"com.fenago.phonebook\\\"," +
            "    \\\"type\\\": \\\"record\\\"," +
            "    \\\"name\\\": \\\"Employee\\\"," +
            "    \\\"fields\\\": [" +
            "        {\\\"name\\\": \\\"fName\\\", \\\"type\\\": \\\"string\\\"}," +
            "        {\\\"name\\\": \\\"lName\\\", \\\"type\\\": \\\"string\\\"}," +
            "        {\\\"name\\\": \\\"age\\\",  \\\"type\\\": \\\"int\\\"}," +
            "        {\\\"name\\\": \\\"phoneNumber\\\",  \\\"type\\\": \\\"string\\\"}" +
            "    ]" +
            "  }\"" +
            "}";

    public static void main(String... args) throws IOException {

        System.out.println(EMPLOYEE_SCHEMA);

        final OkHttpClient client = new OkHttpClient();

        //TODO POST A NEW SCHEMA
        // HINT: Request request = new Request.Builder()
        //        .post(RequestBody.create(SCHEMA_CONTENT, EMPLOYEE_SCHEMA))
        //        .url("http://localhost:8081/subjects/Employee/versions")
        //        .build();

        // String output = client.newCall(request).execute().body().string();
        //System.out.println(output);

        //TODO LIST ALL SCHEMAS
        // HINT        .url("http://localhost:8081/subjects")

        // HINT output = client.newCall(request).execute().body().string();
        //System.out.println(output);


        //TODO SHOW ALL VERSIONS OF EMPLOYEE
        //request = new Request.Builder()
        //        .url("http://localhost:8081/subjects/Employee/versions/")
        //        .build();

        //output = client.newCall(request).execute().body().string();
        //System.out.println(output);

        //TODO SHOW VERSION 2 OF EMPLOYEE (you may have to make some changes to the schema before this will run.
        // SKIP this one at first.
        //request = new Request.Builder()
        //        .url("http://localhost:8081/subjects/Employee/versions/2")
        //        .build();

        //output = client.newCall(request).execute().body().string();
        //System.out.println(output);

        //TODO SHOW THE SCHEMA WITH ID 3 - SKIP until you have three versions.
        //request = new Request.Builder()
        //        .url("http://localhost:8081/schemas/ids/3")
        //        .build();

        //output = client.newCall(request).execute().body().string();
        //System.out.println(output);


        // TODO SHOW THE LATEST VERSION OF EMPLOYEE 2
        //request = new Request.Builder()
        //        .url("http://localhost:8081/subjects/Employee/versions/latest")
        //        .build();

        //output = client.newCall(request).execute().body().string();
        //System.out.println(output);



        //TODO CHECK IF SCHEMA IS REGISTERED
        //request = new Request.Builder()
        //        .post(RequestBody.create(SCHEMA_CONTENT, EMPLOYEE_SCHEMA))
        //        .url("http://localhost:8081/subjects/Employee")
        //        .build();

        //output = client.newCall(request).execute().body().string();
        //System.out.println(output);


        //TODO TEST COMPATIBILITY - HOLD off on this until you have two schemas defined.
        //request = new Request.Builder()
        //        .post(RequestBody.create(SCHEMA_CONTENT, EMPLOYEE_SCHEMA))
        //        .url("http://localhost:8081/compatibility/subjects/Employee/versions/latest")
        //        .build();

        //output = client.newCall(request).execute().body().string();
        //System.out.println(output);

// Later hints
//
//        // TOP LEVEL CONFIG
//        request = new Request.Builder()
//                .url("http://localhost:8081/config")
//                .build();
//
//        output = client.newCall(request).execute().body().string();
//        System.out.println(output);
//
//
//        // SET TOP LEVEL CONFIG
//        // VALUES are none, backward, forward and full
//        request = new Request.Builder()
//                .put(RequestBody.create(SCHEMA_CONTENT, "{\"compatibility\": \"none\"}"))
//                .url("http://localhost:8081/config")
//                .build();
//
//        output = client.newCall(request).execute().body().string();
//        System.out.println(output);
//
//        // SET CONFIG FOR EMPLOYEE
//        // VALUES are none, backward, forward and full
//        request = new Request.Builder()
//                .put(RequestBody.create(SCHEMA_CONTENT, "{\"compatibility\": \"backward\"}"))
//                .url("http://localhost:8081/config/Employee")
//                .build();
//
//        output = client.newCall(request).execute().body().string();
//        System.out.println(output);
//


    }
}
