import java.io.Serializable;
public class Message implements Serializable  {
 String text;
 int id;

 public void Message(String text , int id) {
  this.id = id ;
  this.text = text ;
 }
 
 public String getText() {
  return text ;
 }
 public int getID() {
  return id ;
 }
 
}