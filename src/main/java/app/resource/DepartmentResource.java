package app.resource;

import app.controller.DepartmentController;
import app.entity.Department;
import lombok.Getter;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Getter
public class DepartmentResource extends ResourceSupport {
    private final Department department;

    public DepartmentResource(final Department department) {
        this.department = department;
        final long deptId = department.getDeptId();
        add(linkTo(DepartmentController.class).withRel("departments"));
        add(linkTo(methodOn(DepartmentController.class).getId()).withSelfRel());
    }
}
