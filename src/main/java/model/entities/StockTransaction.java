package main.java.model.entities;

import java.time.LocalDate;
import java.util.Objects;

import main.java.model.util.Util;

/**
 * This represents a particular Stock Transaction. It holds information like the type of
 * transaction(buy/sell), transactionDate,commission,and the quantity left with the user after the
 * transaction.
 */
public class StockTransaction implements Comparable<StockTransaction> {
  private final LocalDate transactionDate;
  Util.TransactionType transactionType;
  private Double quantityAfterTransaction = 0d;
  private Double commission;

  /**
   * Constructs a Stock Transaction object associated with a certain stock.
   *
   * @param transactionDate          date when then transaction is conducted.
   * @param quantityAfterTransaction the quantity of stock left with the user after
   *                                 transaction.
   * @param commission               the commission associated with the transaction.
   * @param transactionType          type of transaction whether buy or sell.
   */
  public StockTransaction(LocalDate transactionDate, Double quantityAfterTransaction,
                          Double commission, Util.TransactionType transactionType) {
    this.transactionDate = transactionDate;
    this.quantityAfterTransaction = quantityAfterTransaction;
    this.commission = commission;
    this.transactionType = transactionType;
  }

  /**
   * An overloaded constructor which builds transaction with just the date.
   *
   * @param transactionDate date when then transaction is conducted
   */
  public StockTransaction(LocalDate transactionDate) {
    this.transactionDate = transactionDate;
  }

  /**
   * Determining whether two transaction objects are equal or not.
   *
   * <p>Since we maintain a Navigable set of these stock transactions sorted by the date.
   * Hence, the equals method returns true if two transactions have same date. Another transaction
   * received on same date is then grouped with the existing one.</p>
   *
   * @param o Input object to compare with Transaction Object.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof StockTransaction)) {
      return false;
    }
    StockTransaction that = (StockTransaction) o;
    return getTransactionDate().equals(that.getTransactionDate());
  }

  /**
   * Returns hashcode of the transaction object.
   *
   * @return hashcode of the transaction.
   */
  @Override
  public int hashCode() {
    return Objects.hash(getTransactionDate());
  }

  /**
   * Returns commission that is associated with the current object.
   *
   * @return commission that was inccured for the transaction.
   */
  public Double getCommission() {
    return commission;
  }

  /**
   * Returns the transactionDate associated with the current transaction.
   *
   * @return date of the current transaction.
   */
  public LocalDate getTransactionDate() {
    return transactionDate;
  }

  /**
   * Returns the quantity left with the user after the transaction.
   *
   * @return quantity left with the user.
   */
  public Double getQuantityAfterTransaction() {
    return quantityAfterTransaction;
  }

  /**
   * Set the quantity left with the user after the transaction.
   *
   * @param quantityAfterTransaction quantity left with the user.
   */
  public void setQuantityAfterTransaction(Double quantityAfterTransaction) {
    this.quantityAfterTransaction = quantityAfterTransaction;
  }

  /**
   * Returns the transaction type (buy/sell).
   *
   * @return the transaction type of the given transaction
   */
  public Util.TransactionType getTransactionType() {
    return transactionType;
  }

  /**
   * Compares two transaction object.
   *
   * @param o the object to be compared.
   * @return 0 if two transaction objects have same date 1 if current transaction's date is greater
   *         than the incoming object -1 if current transaction's date is smaller than the incoming
   *         object.
   */
  @Override
  public int compareTo(StockTransaction o) {
    return this.transactionDate.compareTo(o.getTransactionDate());
  }
}
