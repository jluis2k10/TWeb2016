package es.jperez2532.repositories;

import es.jperez2532.entities.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface ActorRepo extends JpaRepository<Actor, Long> {

    Actor findByName(String name);

}