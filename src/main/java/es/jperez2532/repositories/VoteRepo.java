package es.jperez2532.repositories;

import es.jperez2532.entities.Vote;
import es.jperez2532.entities.VotePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Atención al segundo parámetro de JpaRepository. La PK de la tabla Votes es compuesta,
 * luego para buscar, insertar, borrar, modificar (CRUD) se utilizará un objeto VotePK
 * que modela esta clave compuesta para indexar las consultas a BBDD.
 */
@Repository
public interface VoteRepo extends JpaRepository<Vote, VotePK> {
    List<Vote> findByIdFilm(Long filmID);
    List<Vote> findByIdAccount(Long accountID);
}
