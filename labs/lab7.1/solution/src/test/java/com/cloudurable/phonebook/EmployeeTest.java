package com.fenago.phonebook;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EmployeeTest {


    @Test
    public void test() {

        Employee bob = Employee.newBuilder().setAge(35)
                .setFirstName("Bob")
                .setLastName("Jones")
                .setPhoneNumber(
                        PhoneNumber.newBuilder()
                                .setAreaCode("301")
                                .setCountryCode("1")
                                .setPrefix("555")
                                .setNumber("1234")
                                .build())
                .build();

        assertEquals("Bob", bob.getFirstName());

    }

    @Test
    public void testWrite() throws Exception {

        final List<Employee> employeeList = new ArrayList<>();
        for (int index = 0; index < 100; index++) {
            employeeList.add(Employee.newBuilder().setAge(index % 35 + 25)
                    .setFirstName("Bob" + index)
                    .setLastName("Jones" + index)
                    .setPhoneNumber(PhoneNumber.newBuilder()
                            .setAreaCode("301")
                            .setCountryCode("1")
                            .setPrefix("555")
                            .setNumber("1234")
                            .build())
                    .build());
        }


        final DatumWriter<Employee> datumWriter = new SpecificDatumWriter<>(Employee.class);
        final DataFileWriter<Employee> dataFileWriter = new DataFileWriter<>(datumWriter);

        try {
            dataFileWriter.create(employeeList.get(0).getSchema(),
                    new File("employees.avro"));
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

            final File file = new File("employees.avro");
            final List<Employee> employeeList = new ArrayList<>();
            final DatumReader<Employee> empReader = new SpecificDatumReader<>(Employee.class);
            final DataFileReader<Employee> dataFileReader = new DataFileReader<>(file, empReader);

            while (dataFileReader.hasNext()) {
                employeeList.add(dataFileReader.next(new Employee()));
            }

            employeeList.forEach(System.out::println);

    }
}
