package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Balance;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Component
public class JdbcaccountDAO implements AccountDAO {

    private JdbcTemplate jdbcTemplate;
    public JdbcaccountDAO(DataSource dataSource) {
        this.jdbcTemplate =new JdbcTemplate(dataSource);
    }
    @Override
    public Balance getBalance(String user) {
        String sqlString = "SELECT balance FROM account JOIN tenmo_user on account.user_id = tenmo_user.user_id WHERE username = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlString,user);
        Balance balance = new Balance();
        if (results.next()) {
                String accountBalance = results.getString("balance");
                balance.setBalance(new BigDecimal((accountBalance)));
            }
        return balance;
    }

    @Override
    public Account getAccountByUserID(int userId) {
        String sql = "Select account_id,user_id,balance FROM account WHERE user_id =?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql,userId);
        Account account = null;
        if(results.next()) {
            account = mapResultsToAccount(results);
        }
        return account;
    }
@Override
    public Account getAccountByAccountID(int accountId){
        String sql = "Select account_id,user_id,balance FROM account WHERE account_id = ?";
    SqlRowSet results = jdbcTemplate.queryForRowSet(sql,accountId);
    Account account = null;
    if(results.next()){
        account = mapResultsToAccount(results);
    }
    return account;
}
@Override
    public void updateAccount(Account accountToUpdate){
        String sql = "UPDATE account SET balance = ?  WHERE account_id=?";
        jdbcTemplate.update(sql, accountToUpdate.getBalance().getBalance(),accountToUpdate.getAccountId());
}
private Account mapResultsToAccount (SqlRowSet results){
        int accountId = results.getInt("account_id");
        int userAccountId = results.getInt("user_id");
        Balance balance = new Balance();
        String accountBalance = results.getString("balance");
        balance.setBalance(new BigDecimal(accountBalance));
        return new Account (accountId,userAccountId,balance);
}
}
