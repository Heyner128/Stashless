package me.heyner.inventorypro.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ConflictingIndexesException extends RuntimeException {

  public ConflictingIndexesException() {
    super(
        "Conflicting indexes inside the request body and path, which one would you like to update?");
  }
}
