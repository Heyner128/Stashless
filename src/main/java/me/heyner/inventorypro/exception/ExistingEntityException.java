package me.heyner.inventorypro.exception;

public class ExistingEntityException extends RuntimeException {
  public ExistingEntityException(String name) {
    super("The entity " + name + " already exists");
  }
}
