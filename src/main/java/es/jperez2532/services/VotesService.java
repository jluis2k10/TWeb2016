package es.jperez2532.services;

import es.jperez2532.entities.Vote;

public interface VotesService {
    boolean isValid(Vote vote, String urlPath, String username);
    String doVote(Vote newVote);
}
