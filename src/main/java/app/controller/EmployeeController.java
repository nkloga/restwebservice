package app.controller;

import app.entity.Department;
import app.entity.Employee;
import app.repository.EmployeeRepository;
import app.resource.EmployeeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/employees", produces = "application/hal+json")
public class EmployeeController extends ResourceSupport {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping
    public ResponseEntity<Resources<EmployeeResource>> all() {
        final List<EmployeeResource> collection =
                employeeRepository.findAll().stream()
                        .map(EmployeeResource::new).collect(Collectors.toList());

        final Resources<EmployeeResource> resources = new Resources<>(collection);
        final String uriString = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
        resources.add(new Link(uriString, "self"));
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResource> get(@PathVariable final long id) {
        return employeeRepository
                .findById(id)
                .map(p -> ResponseEntity.ok(new EmployeeResource(p)))
                .orElseThrow(() -> new RuntimeException("Employee not found:" + id));
    }

    @PostMapping
    public ResponseEntity<EmployeeResource> post(@RequestBody final Employee employeeFromRequest) {
        final Employee employee = employeeRepository.save(employeeFromRequest);
        final URI uri =
                MvcUriComponentsBuilder.fromController(getClass())
                        .path("/{id}")
                        .buildAndExpand(employee.getId())
                        .toUri();
        return ResponseEntity.created(uri).body(new EmployeeResource(employee));
    }

    @GetMapping("/letter/{byLetter}")
    public ResponseEntity findEmployeeWithSurenameThatBeginsWith(@PathVariable final String byLetter) {
        List<Employee> collect = employeeRepository
                .findAll()
                .stream()
                .filter(employee -> employee.getSurname().contains(byLetter))
                .collect(Collectors.toList());
        return ResponseEntity.ok(collect);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResource> put(
            @PathVariable("id") final long id, @RequestBody Employee employeeFromRequest) {
        Employee emp = employeeRepository.findById(id)
                .map(employee -> {
                    employee.setName(employeeFromRequest.getName());
                    employee.setAge(employeeFromRequest.getAge());
                    employee.setDepartment(employeeFromRequest.getDepartment());
                    return employeeRepository.save(employee);
                })
                .orElseGet(() -> {
                    employeeFromRequest.setId(id);
                    return employeeRepository.save(employeeFromRequest);
                });

        final EmployeeResource resource = new EmployeeResource(emp);
        final URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(uri).body(resource);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        employeeRepository.deleteById(id);
    }


    @GetMapping("/by-department")
    public ResponseEntity<Map<Department, List<Employee>>> groupEmployeesByDepartmant (){
        Map<Department,List<Employee>>  collect = employeeRepository
                .findAll()
                .stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));
        return ResponseEntity.ok(collect);
    }

    @GetMapping("/rich-guys")
    public ResponseEntity findEmployeeHigherThanAHoundredThousand () {
        List<Employee> collect = employeeRepository
                .findAll()
                .stream()
                .filter(Objects::nonNull)
                .filter(employee -> employee.getSalary().compareTo(BigDecimal.valueOf(100000L)) == 1)
                .collect(Collectors.toList());
        return ResponseEntity.ok(collect);
    }
}
