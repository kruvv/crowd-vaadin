package ru.kruvv.components;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import lombok.Setter;
import ru.kruvv.domain.Employee;
import ru.kruvv.repo.EmployeeRepo;

/**
 * @author Viktor Krupkin
 **/

@SpringComponent
@UIScope
public class EmployeeEditor extends VerticalLayout implements KeyNotifier {

	private final EmployeeRepo employeeRepo;

	/**
	 * The currently edited customer
	 */
	private Employee employee;

	/* Fields to edit properties in Customer entity */
	TextField firstName = new TextField("First name");
	TextField lastName = new TextField("Last name");
	TextField patronymic = new TextField("Patronymic");

	/* Action buttons */
	private Button save = new Button("Save", VaadinIcon.CHECK.create());
	private Button cancel = new Button("Cancel");
	private Button delete = new Button("Delete", VaadinIcon.TRASH.create());
	private HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

	private Binder<Employee> binder = new Binder<>(Employee.class);

	@Setter
	private ChangeHandler changeHandler;

	public interface ChangeHandler {
		void onChange();
	}

	@Autowired
	public EmployeeEditor(EmployeeRepo employeeRepo) {
		this.employeeRepo = employeeRepo;

		add(lastName, firstName, patronymic, actions);

		// bind using naming convention
		binder.bindInstanceFields(this);

		// Configure and style components
		setSpacing(true);

		save.getElement().getThemeList().add("primary");
		delete.getElement().getThemeList().add("error");

		addKeyPressListener(Key.ENTER, e -> save());

		// wire action buttons to save, delete and reset
		save.addClickListener(e -> save());
		delete.addClickListener(e -> delete());
		cancel.addClickListener(e -> editEmployee(employee));
		setVisible(false);
	}

	private void delete() {
		employeeRepo.delete(employee);
		changeHandler.onChange();
	}

	private void save() {
		employeeRepo.save(employee);
		changeHandler.onChange();
	}

	public void editEmployee(Employee newEmp) {
		if (newEmp == null) {
			setVisible(false);
			return;

		}

		if (newEmp.getId() != null) {
			// Find fresh entity for editing
			employee = employeeRepo.findById(newEmp.getId()).orElse(newEmp);
		} else {
			employee = newEmp;
		}

		// Bind customer properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(employee);

		setVisible(true);

		// Focus first name initially
		firstName.focus();

	}
}
