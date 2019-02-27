package homework;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    // Create a class named Person - String firstName, String lastName, Country country, int age
    // Create a class named Country - boolean eu
    // Read csv file and create streams with suitable classes.
    // Create a class has these methods : findAll, findPersonByName, findPersonByLastName,
    // isPersonEuCitizen,getAverageAge

    public static void main(String[] args) {

        String fileName = "src/main/java/homework/people.csv";
        System.out.println("List of all employees " + findAll(fileName));
        System.out.println("----------------------------------------------");

        String firstnameSearch = "Suzi";
        String lastnameSearch = "Bush";

        System.out.println("Looking for person with first name " + firstnameSearch +": " + findPersonByName(fileName, firstnameSearch));
        System.out.println("Looking for a person with last name " + lastnameSearch + ": " + findPersonByLastName(fileName, lastnameSearch));
        System.out.println("Checking if " + firstnameSearch + " is EU citizen: " + isPersonEuCitizen(fileName, firstnameSearch));
        System.out.println("The average age in the list is: " + getAverageAge(fileName));

    }

    private static double getAverageAge(String fileName) {
        List<Person> result = findAll(fileName);
        return result.stream().mapToInt(Person::getAge).filter(Objects::nonNull).average().getAsDouble();
    }

    private static boolean isPersonEuCitizen(String fileName, String name) {
        List<Person> result = findAll(fileName);
        return Objects.requireNonNull(result.stream()
                .filter(n -> n.getFirstName().equals(name))
                .findAny()
                .orElseGet(() -> {
                    System.out.println("ERROR: No such person in the file");
                    return null;
                })).getCountry()
                .isEu();
    }

    private static Person findPersonByLastName(String fileName, String lastname) {
        List<Person> result = findAll(fileName);
        return result.stream()
                .filter(n -> n.getLastName().equals(lastname))
                .findAny().orElseGet(()-> {
                    System.out.println("ERROR: No such person in the file");
                    return null;
                });
    }

    private static Person findPersonByName(String fileName, String name) {
        List<Person> result = findAll(fileName);
        return result.stream()
                .filter(n -> n.getFirstName().equals(name))
                .findAny()
                .orElseGet(()-> {
                    System.out.println("ERROR: No such person in the file");
                    return null;
                });
    }

    private static List<Person> findAll(String fileName) {
        Stream<String> lines = null;
        try {
            lines = Files.lines(Paths.get(fileName)).skip(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(lines).map(p -> {
            String[] record = p.split(",");
            return new Person(record[0], record[1], Country.valueOf(record[2]), Integer.parseInt(record[3]));
        }).collect(Collectors.toList());
    }
}