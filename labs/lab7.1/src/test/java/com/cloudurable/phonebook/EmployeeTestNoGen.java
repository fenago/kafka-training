package com.fenago.phonebook;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.*;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EmployeeTestNoGen {


    @Test
    public void test() throws Exception {

        //TODO load schema
        // HINT final String schemaLoc = "src/main/avro/com/fenago/phonebook/Employee.avsc";
        // HINT final File schemaFile = new File(schemaLoc);
        // HINT final Schema schema = new Schema.Parser().parse(schemaFile);

        // Use GenericRecord to create bob object follow README.md as guide.

        //HINTS
//        GenericRecord bob = new GenericData.Record(schema);
//        bob.put("firstName", "Bob");
//        bob.put("lastName", "Smith");
//        bob.put("age", 35);
//        bob.put("status", new GenericData.EnumSymbol(schema, "SALARY"));

        //TODO assert properties are set correctly
        // HINT assertEquals("Bob", bob.get("firstName"));

    }


    @Test
    public void testWrite() throws Exception {

        //TODO load schema


        final List<GenericRecord> employeeList = new ArrayList<>();

        //TODO Populate list with 100 employee records using GenericRecord
        for (int index = 0; index < 100; index++) {

            // HINT GenericRecord employee = new GenericData.Record(schema);
            // TODO populate all fields
            // HINT employee.put("firstName", "Bob" + index);
            // HINT employee.put("status", new GenericData.EnumSymbol(schema, "SALARY"));
            //employeeList.add(employee);

        }



        //final DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        //final DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);

        // TODO use dataFileWriter to write records to file
        //try {
            // HINT dataFileWriter.create(employeeList.get(0).getSchema(), new File("employees2.avro"));
            // HINT employeeList.forEach(employee -> {
            // HINT        dataFileWriter.append(employee);

        testRead();
    }


    public void testRead() throws Exception {

        // TODO read records from file

        //final File file = new File("employees2.avro");
        //final List<GenericRecord> employeeList = new ArrayList<>();
        //final DatumReader<GenericRecord> empReader = new GenericDatumReader<>();
        //final DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(file, empReader);

        //Use README.md as guide.

    }
}
