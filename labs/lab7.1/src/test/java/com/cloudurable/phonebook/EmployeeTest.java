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

        //TODO create a new Employee called bob with a builder.
        // TODO set the age to 35, first name to Bob and last name to Jones.

        //TODO assert properties are set
        //assertEquals("Bob", bob.getFirstName());

    }

    @Test
    public void testWrite() throws Exception {

        //TODO write out a list of Employees use README as guide.

        // HINT final List<Employee> employeeList = new ArrayList<>();
        // HINT for (int index = 0; index < 100; index++) {
            // employeeList.add(Employee.newBuilder().setAge(index % 35 + 25)
            //        .setFirstName("Bob" + index)
            //        .setLastName("Jones" + index)
             //       .setPhoneNumber(PhoneNumber.newBuilder()


        //HINT final DatumWriter<Employee> datumWriter = new SpecificDatumWriter<>(Employee.class);
        //HINT final DataFileWriter<Employee> dataFileWriter = new DataFileWriter<>(datumWriter);
        // HINT employeeList.forEach(employee -> { dataFileWriter.append(employee);

        testRead();
    }


    public void testRead() throws Exception {

            //TODO Read Avro file from disk

    }
}
