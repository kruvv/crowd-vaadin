package ru.kruvv.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.kruvv.domain.Employee;

/**
 * @author Viktor Krupkin
 **/

public interface EmployeeRepo extends JpaRepository<Employee, Long> {

	@Query("from Employee e where concat(e.lastName, ' ', e.firstName, ' ', e.patronymic) like concat('%', :name, '%') ")
	List<Employee> findByName(@Param("name") String name);
}
