import java.sql.*;
import java.util.*;

public class Atm {

  public static void main(String[] args) {
    try {
      Scanner sc = new Scanner(System.in);
      int userid;
      String password;
      System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");            
      System.out.println("\t\t\t\t\t\t Welcome to our Bank!!!!");
      System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");            

      int val = 2;

      while (val == 2) {
        System.out.println("LOGIN");
        System.out.println("Enter User ID:-");
        userid = sc.nextInt();
        System.out.println("Enter Password:-");
        password = sc.next();
        boolean isCorrect = true;
        User u1 = new User(userid, password);
        DB db = new DB();
        boolean isValidated = u1.validate();
        if (isValidated) {
          System.out.println("Hellooo " + u1.getuserName());
        } else {
          isCorrect = false;
          System.out.println("Please enter correct crendentials!");
        }
        int choice = -1, amount, acc_no;
        String fromDate, toDate;
        if (isCorrect) {
          while (choice != 5) {
          System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");            System.out.println(
              "1.Deposit\n2.Withdraw\n3.Transfer\n4.Transaction History\n5.Exit"
            );
            System.out.println("Enter the choice you want to perform");
            
            choice = sc.nextInt();
            switch (choice) {
              case 1:
                System.out.println("Enter the amount you want to deposit");
                amount = sc.nextInt();
                u1.accounts[0].deposit(amount);
                
                break;
              case 2:
                System.out.println("Enter the amount you want to Withdraw");
                amount = sc.nextInt();
                u1.accounts[0].withdraw(amount);
                
                break;
              case 3:
                System.out.println("Enter the amount you want to Transfer");
                amount = sc.nextInt();
                System.out.println("Enter the account number of reciver");
                acc_no = sc.nextInt();
                u1.accounts[0].transfer(amount, acc_no);
                break;
              case 4:
                System.out.println(
                  "Enter the date from which you want to get records in format of DD/MM(in words)/YYYY"
                );
                fromDate = sc.next();
                System.out.println(
                  "Enter the date upto which you want to get records in format of DD/MM(in words)/YYYY"
                );
                toDate = sc.next();
                System.out.println(fromDate);
                TransacationRecord[] tr_history =
                  u1.accounts[0].getTransactionHistory(fromDate, toDate);
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");
                System.out.println("From User\tFrom Acc.No.\tTo User\t\tTo Acc.No.\tTr.Type\t\t\tAmount\t\t\tDate");
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");


                for (int i = 0; i < tr_history.length; i++) {
                  System.out.println(
                    "\n" +
                    tr_history[i].getfromUser() +
                    "\t\t" +
                    tr_history[i].getfromAcc() +
                    "\t\t" +
                    tr_history[i].gettoUser() +
                    "\t\t" +
                    tr_history[i].gettoAcc() +
                    "\t\t" +
                    tr_history[i].getType() +
                    "\t\t" +
                    tr_history[i].getAmount() +
                    "\t\t" +
                    tr_history[i].getDate()
                  );
                }

                break;
              case 5:
                System.exit(0);
                break;
            }
          }
        }
      }

      DB.getConnection().close();
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}

class User {

  String userName;
  int userID;
  String pass;
  Account accounts[];
  private int acc_index = 0;

  User(int userID, String pass) {
    this.userID = userID;
    this.pass = pass;
    this.accounts = new Account[2];
  }

  void setuserName(String userName) {
    this.userName = userName;
  }

  void setuserID(int userID) {
    this.userID = userID;
  }

  void setPass(String pass) {
    this.pass = pass;
  }

  String getuserName() {
    return userName;
  }

  int getuserID() {
    return userID;
  }

  String getPass() {
    return pass;
  }

  void addAccount(Account a1) {
    this.accounts[acc_index] = a1;
    acc_index++;
  }

  boolean validate() {
    boolean isFound = false;
    try {
      Statement stmt = DB.getConnection().createStatement();
      ResultSet rs = stmt.executeQuery(
        "select au.userID,userName, password, acc_id, acc_no,balance from atm_user au join account ac on au.userID = ac.userID where au.userID = " +
        this.userID
      );
      while (rs.next()) {
        if (!isFound) {
          if (
            this.userID == rs.getInt(1) && this.pass.equals(rs.getString(3))
          ) {
            this.setuserName(rs.getString(2));
            isFound = true;
            Account a = new Account(rs.getInt(4));
            a.setaccNo(rs.getInt(5));
            a.setBalance(rs.getInt(6));
            this.addAccount(a);
            acc_index++;
          } else {
            System.out.println("User not Found");
            break;
          }
        }
      }
    } catch (Exception e) {
      System.out.println(e);
    }

    return isFound;
  }
}

class Account {

  int acc_id;
  int acc_no;
  int balance;

  Account(int acc_id) {
    this.acc_id = acc_id;
  }

  void setaccID(int acc_id) {
    this.acc_id = acc_id;
  }

  void setaccNo(int acc_no) {
    this.acc_no = acc_no;
  }

  void setBalance(int balance) {
    this.balance = balance;
  }

  int getaccID() {
    return acc_id;
  }

  int getaccNo() {
    return acc_no;
  }

  int getBalance() {
    return balance;
  }

  void deposit(int amount) throws SQLException {
    if (amount <= 0) {
      System.out.println("Enter correct ammount");
      return;
    }

    this.balance += amount;

    try {
      int temp_tr_id = 1, temp_tr_no = 5001;

      Statement stmt = DB.getConnection().createStatement();
      DB.getConnection().setAutoCommit(false);
      int rows = stmt.executeUpdate(
        "update account set balance =" +
        this.balance +
        " where acc_id = " +
        this.acc_id
      );
      ResultSet rs = stmt.executeQuery(
        "select max(tr_id), max(tr_no) from transaction"
      );
      while (rs.next()) {
        temp_tr_id = rs.getInt(1) + 1;
        temp_tr_no = rs.getInt(2) + 1;
      }
      String str =
        "insert into transaction values(" +
        temp_tr_id +
        "," +
        TransacationRecord.DEPOSIT +
        "," +
        temp_tr_no +
        "," +
        this.acc_id +
        ",NULL," +
        amount +
        ",(select current_timestamp from dual))";

      rows = stmt.executeUpdate(str);
      System.out.println("Amount deposited succesfully");
      DB.getConnection().commit();
    } catch (Exception e) {
      DB.getConnection().rollback();
      System.out.println(e);
    }
  }

  void withdraw(int amount) throws SQLException {
    if (amount <= 0 || amount > this.balance) {
      System.out.println("Enter correct ammount");
      return;
    }

    this.balance -= amount;

    try {
      int temp_tr_id = 1, temp_tr_no = 5001;

      Statement stmt = DB.getConnection().createStatement();
      DB.getConnection().setAutoCommit(false);
      int rows = stmt.executeUpdate(
        "update account set balance =" +
        this.balance +
        " where acc_id = " +
        this.acc_id
      );
      ResultSet rs = stmt.executeQuery(
        "select max(tr_id), max(tr_no) from transaction"
      );
      while (rs.next()) {
        temp_tr_id = rs.getInt(1) + 1;
        temp_tr_no = rs.getInt(2) + 1;
      }
      String str =
        "insert into transaction values(" +
        temp_tr_id +
        "," +
        TransacationRecord.WITHDRAW +
        "," +
        temp_tr_no +
        "," +
        this.acc_id +
        ",NULL," +
        amount +
        ",(select current_timestamp from dual))";

      rows = stmt.executeUpdate(str);
      System.out.println("Amount withdrawn succesfully");
      DB.getConnection().commit();
    } catch (Exception e) {
      DB.getConnection().rollback();
      System.out.println(e);
    }
  }

  void transfer(int amount, int toacc) throws SQLException {
    if (amount <= 0 || amount < this.balance) {
      System.out.println("Enter correct ammount");
      return;
    }
    this.balance -= amount;
    try {
      int temp_tr_id = 1, temp_tr_no = 5001;
      Statement stmt = DB.getConnection().createStatement();
      DB.getConnection().setAutoCommit(false);
      int rows = stmt.executeUpdate(
        "update account set balance =" +
        this.balance +
        " where acc_id = " +
        this.acc_id
      );
      rows =
        stmt.executeUpdate(
          "update account set balance = balance + " +
          amount +
          " where acc_no =" +
          toacc
        );
      ResultSet rs = stmt.executeQuery(
        "select max(tr_id), max(tr_no) from transaction"
      );
      while (rs.next()) {
        temp_tr_id = rs.getInt(1) + 1;
        temp_tr_no = rs.getInt(2) + 1;
      }
      String str =
        "insert into transaction values(" +
        temp_tr_id +
        "," +
        TransacationRecord.TRANSFER +
        "," +
        temp_tr_no +
        "," +
        this.acc_id +
        ",(select acc_id from account where acc_no =" +
        toacc +
        ")," +
        amount +
        ",(select current_timestamp from dual))";
      System.out.println(str);
      rows = stmt.executeUpdate(str);
      System.out.println("Amount transfered succesfully");
      DB.getConnection().commit();
    } catch (Exception e) {
      DB.getConnection().rollback();
      System.out.println(e);
    }
  }

  TransacationRecord[] getTransactionHistory(String fromDate, String toDate) {
    TransacationRecord[] tr_history = null;
    try {
      int rowcount = 0, count = 0;
      Statement stmt = DB.getConnection().createStatement();
      String str1 =
        "select count(1) from transaction where (from_acc = " +
        this.acc_id +
        " OR to_acc = " +
        this.acc_id +
        " ) and tr_date between '" +
        fromDate +
        "' and '" +
        toDate +
        "'";

      String str2 =
        "select au1.userName as From_User, ac1.acc_no as From_Acc, au2.userName as To_User, ac2.acc_no as To_Acc, transactionName, amount, tr_date from transaction tr join account ac1 on tr.from_acc = ac1.acc_id join transaction_type_master ty on tr.transactionID = ty.transactionID join atm_user au1 on ac1.userID = au1.userID left join account ac2 on tr.to_acc = ac2.acc_id left join atm_user au2 on ac2.userID = au2.userID where (ac1.acc_id = " +
        this.acc_id +
        " OR ac2.acc_id = " +
        this.acc_id +
        " ) and tr.tr_date between '" +
        fromDate +
        "' and '" +
        toDate +
        "' order by tr.tr_date";

      ResultSet rs = stmt.executeQuery(str1);
      while (rs.next()) {
        rowcount = rs.getInt(1);
      }
      tr_history = new TransacationRecord[rowcount];

      rs = stmt.executeQuery(str2);
      while (rs.next()) {
        tr_history[count] = new TransacationRecord();
        tr_history[count].setfromUser(rs.getString(1));
        tr_history[count].settoUser(rs.getString(3));
        tr_history[count].setfromAcc(rs.getInt(2));
        tr_history[count].settoAcc(rs.getInt(4));
        tr_history[count].settr_type(rs.getString(5));
        tr_history[count].setAmount(rs.getInt(6));
        tr_history[count].settr_date(rs.getString(7));
        count++;
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    return tr_history;
  }
}

class TransacationRecord {

  static final int DEPOSIT = 1;
  static final int WITHDRAW = 2;
  static final int TRANSFER = 3;

  String fromUser, toUser, tr_type, tr_date;
  int fromAcc, toAcc, amount;

  void setfromUser(String fromUser) {
    this.fromUser = fromUser;
  }

  void settoUser(String toUser) {
    this.toUser = toUser;
  }

  void setfromAcc(int fromAcc) {
    this.fromAcc = fromAcc;
  }

  void settoAcc(int toAcc) {
    this.toAcc = toAcc;
  }

  void settr_type(String tr_type) {
    this.tr_type = tr_type;
  }

  void settr_date(String tr_date) {
    this.tr_date = tr_date;
  }

  void setAmount(int amount) {
    this.amount = amount;
  }

  String getfromUser() {
    return fromUser;
  }

  String gettoUser() {
    return toUser;
  }

  int getfromAcc() {
    return fromAcc;
  }

  int gettoAcc() {
    return toAcc;
  }

  String getType() {
    return tr_type;
  }

  String getDate() {
    return tr_date;
  }

  int getAmount() {
    return amount;
  }
}

class DB {

  private static Connection con;

  DB() {
    try {
      Class.forName("oracle.jdbc.driver.OracleDriver");
      DB.con =
        DriverManager.getConnection(
          "jdbc:oracle:thin:@localhost:1521:xe",
          "ATM",
          "12345"
        );
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  static Connection getConnection() {
    return con;
  }

  int checkBalance(Account a1) {
    try {
      Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery(
        "select balance from account where acc_id=" + a1.acc_id
      );
      while (rs.next()) {
        a1.setBalance(rs.getInt(1));
      }
    } catch (Exception e) {
      System.out.println(e);
    }

    return 1;
  }
}
