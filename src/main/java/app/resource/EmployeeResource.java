package app.resource;

import app.controller.EmployeeController;
import app.entity.Employee;
import lombok.Getter;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
@Getter
public class EmployeeResource extends ResourceSupport {
    private final Employee employee;

    public EmployeeResource(final Employee employee) {
        this.employee = employee;
        final long id = employee.getId();
        add(linkTo(EmployeeController.class).withRel("employees"));
        add(linkTo(methodOn(EmployeeController.class).getId()).withSelfRel());
    }
}
