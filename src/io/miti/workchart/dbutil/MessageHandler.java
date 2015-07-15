package io.miti.workchart.dbutil;

/**
 * Interface to define a method to pass a String message.
 * 
 * @author mwallace
 * @version 1.0
 */
public interface MessageHandler
{
  /**
   * Method to pass a message.
   * 
   * @param msg the string message
   */
  void sendMessage(final String msg);
}
