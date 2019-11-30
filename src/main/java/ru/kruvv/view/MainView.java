package ru.kruvv.view;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import ru.kruvv.components.EmployeeEditor;
import ru.kruvv.domain.Employee;
import ru.kruvv.repo.EmployeeRepo;

/**
 * @author Viktor Krupkin
 **/

@Route
public class MainView extends VerticalLayout {

	private final EmployeeRepo employeeRepo;

	private Grid<Employee> grid = new Grid<>(Employee.class);

	private final TextField filter = new TextField("", "Type to filter");

	private final Button addNewBtn = new Button("Add new");

	private final HorizontalLayout toolBar = new HorizontalLayout(filter, addNewBtn);

	private final EmployeeEditor editor;

	@Autowired
	public MainView(EmployeeRepo employeeRepo, EmployeeEditor editor) {
		this.employeeRepo = employeeRepo;
		this.editor = editor;
		add(toolBar, grid, editor);
		showEmployee("");

		grid.setHeight("300px");
		grid.setColumns("id", "firstName", "lastName", "patronymic");
		grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);

		filter.setPlaceholder("Filter by last name");

		// Hook logic to components

		// Replace listing with filtered content when user changes filter
		filter.setValueChangeMode(ValueChangeMode.EAGER);
		filter.addValueChangeListener(e -> showEmployee(e.getValue()));

		// Connect selected Customer to editor or hide if none is selected
		grid.asSingleSelect().addValueChangeListener(e -> {
			editor.editEmployee(e.getValue());
		});

		// Instantiate and edit new Customer the new button is clicked
		addNewBtn.addClickListener(e -> editor.editEmployee(new Employee()));

		// Listen changes made by the editor, refresh data from backend
		editor.setChangeHandler(() -> {
			editor.setVisible(false);
			showEmployee(filter.getValue());
		});

		// Initialize listing
		showEmployee("");
	}

	private void showEmployee(String name) {
		if (name.isEmpty()) {
			grid.setItems(employeeRepo.findAll());
		} else {
			grid.setItems(employeeRepo.findByName(name));
		}
	}

}
