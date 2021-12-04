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

        final String schemaLoc = "src/main/avro/com/fenago/phonebook/Employee.avsc";
        final File schemaFile = new File(schemaLoc);
        final Schema schema = new Schema.Parser().parse(schemaFile);

        GenericRecord bob = new GenericData.Record(schema);
        bob.put("firstName", "Bob");
        bob.put("lastName", "Smith");
        bob.put("age", 35);
        bob.put("status", new GenericData.EnumSymbol(schema, "SALARY"));
        assertEquals("Bob", bob.get("firstName"));

    }


    @Test
    public void testWrite() throws Exception {
        final String schemaLoc = "src/main/avro/com/fenago/phonebook/Employee.avsc";
        final File schemaFile = new File(schemaLoc);
        final Schema schema = new Schema.Parser().parse(schemaFile);


        final List<GenericRecord> employeeList = new ArrayList<>();

        for (int index = 0; index < 100; index++) {

            GenericRecord employee = new GenericData.Record(schema);
            employee.put("firstName", "Bob" + index);
            employee.put("lastName", "Smith"+ index);
            employee.put("age", index % 35 + 25);
            employee.put("status", new GenericData.EnumSymbol(schema, "SALARY"));
            employee.put("emails", Collections.emptyList());
            employeeList.add(employee);

        }



        final DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        final DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);

        try {
            dataFileWriter.create(employeeList.get(0).getSchema(),
                    new File("employees2.avro"));
            employeeList.forEach(employee -> {
                try {
                    dataFileWriter.append(employee);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } finally {
            dataFileWriter.close();
        }

        testRead();
    }


    public void testRead() throws Exception {

        final File file = new File("employees2.avro");
        final List<GenericRecord> employeeList = new ArrayList<>();
        final DatumReader<GenericRecord> empReader = new GenericDatumReader<>();
        final DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(file, empReader);

        while (dataFileReader.hasNext()) {
            employeeList.add(dataFileReader.next(null));
        }

        employeeList.forEach(System.out::println);


    }
}
