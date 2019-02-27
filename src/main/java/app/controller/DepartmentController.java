package app.controller;

import app.entity.Department;
import app.repository.DepartmentRepository;
import app.resource.DepartmentResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/departments", produces = "application/hal+json")
public class DepartmentController extends ResourceSupport {

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping
    public ResponseEntity<Resources<DepartmentResource>> all() {
        final List<DepartmentResource> collection =
                departmentRepository.findAll().stream()
                        .map(DepartmentResource::new).collect(Collectors.toList());

        final Resources<DepartmentResource> resources = new Resources<>(collection);
        final String uriString = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
        resources.add(new Link(uriString, "self"));
        return ResponseEntity.ok(resources);
    }

    @PostMapping
    public ResponseEntity<DepartmentResource> post(@RequestBody final Department departmentFromRequest) {
        final Department department = departmentRepository.save(departmentFromRequest);
        final URI uri =
                MvcUriComponentsBuilder.fromController(getClass())
                        .path("/{deptId}")
                        .buildAndExpand(department.getDeptId())
                        .toUri();
        return ResponseEntity.created(uri).body(new DepartmentResource(department));
    }

    @DeleteMapping("/{deptId}")
    public void delete(@PathVariable Long deptId) {
        departmentRepository.deleteById(deptId);
    }

    @PutMapping("/{deptId}")
    public ResponseEntity<DepartmentResource> put(
            @PathVariable("deptId") final long deptId, @RequestBody Department departmentFromRequest) {
        Department emp = departmentRepository.findById(deptId)
                .map(department -> {
                    department.setDeptName(departmentFromRequest.getDeptName());
                    return departmentRepository.save(department);
                })
                .orElseGet(() -> {
                    departmentFromRequest.setDeptId(deptId);
                    return departmentRepository.save(departmentFromRequest);
                });

        final DepartmentResource resource = new DepartmentResource(emp);
        final URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(uri).body(resource);
    }
}
