package es.jperez2532.services;

import es.jperez2532.entities.Vote;

import java.util.List;

public interface VotesService {
    void delete(Vote vote);
    boolean isValid(Vote vote, String urlPath, String username);
    void populateVote(Vote vote);
    String doVote(Vote newVote);
    List<Vote> findFilmVotes(Long filmID);
    void deleteVotesFromAccount(Long accountID);
    void deleteVotesFromFilm(Long filmID);
}
