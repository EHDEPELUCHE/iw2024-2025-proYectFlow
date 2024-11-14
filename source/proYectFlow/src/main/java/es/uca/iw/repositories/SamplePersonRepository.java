package es.uca.iw.repositories;


import es.uca.iw.data.SamplePerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SamplePersonRepository extends
            JpaRepository<SamplePerson, Long>,
            JpaSpecificationExecutor<SamplePerson>
{

}
