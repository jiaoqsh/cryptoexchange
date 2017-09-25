package com.itranswarp.crypto.account;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.itranswarp.crypto.ApiError;
import com.itranswarp.crypto.ApiException;
import com.itranswarp.crypto.store.AbstractService;
import com.itranswarp.crypto.symbol.Currency;

@Component
@Transactional
public class AccountService extends AbstractService {

	void assertGreaterThanZero(BigDecimal amount) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new ApiException(ApiError.PARAMETER_INVALID, "amount", "amount must be greater than 0.");
		}
	}

	/**
	 * Get all spot account.
	 * 
	 * @param userId
	 *            The user id.
	 * @return List of spot accounts.
	 */
	public List<SpotAccount> getSpotAccounts(long userId) {
		return db.from(SpotAccount.class).where("userId=?", userId).list();
	}

	/**
	 * Deposit specified currency to spot account.
	 * 
	 * @param userId
	 *            The user id.
	 * @param currency
	 *            The currency.
	 * @param amount
	 *            The amount.
	 */
	public void deposit(long userId, Currency currency, BigDecimal amount) {
		assertGreaterThanZero(amount);
		SpotAccount spotAccount = getSpotAccount(userId, currency);
		db.update("UPDATE SpotAccount SET balance = balance + ? WHERE id = ?", amount, spotAccount.id);
	}

	/**
	 * Freeze operation: move balance from spot account to frozen account.
	 * 
	 * @param userId
	 *            The user id.
	 * @param currency
	 *            The currency.
	 * @param amount
	 *            The amount.
	 */
	public void freeze(long userId, Currency currency, BigDecimal amount) {
		assertGreaterThanZero(amount);
		SpotAccount spotAccount = getSpotAccount(userId, currency);
		// transfer balance to frozen:
		if (0 == db.update(
				"UPDATE SpotAccount SET balance = balance - ?, frozen = frozen + ? WHERE id = ? AND balance >= ?",
				amount, amount, spotAccount.id, amount)) {
			throw new ApiException(ApiError.ACCOUNT_FREEZE_FAILED);
		}
	}

	/**
	 * Unfreeze operation: move balance from frozen account to spot account.
	 * 
	 * @param userId
	 *            The user id.
	 * @param currency
	 *            The currency.
	 * @param amount
	 *            The amount.
	 */
	public void unfreeze(long userId, Currency currency, BigDecimal amount) {
		assertGreaterThanZero(amount);
		SpotAccount spotAccount = getSpotAccount(userId, currency);
		// transfer from frozen to balance:
		if (0 == db.update(
				"UPDATE SpotAccount SET balance = balance + ?, frozen = frozen - ? WHERE id = ? AND frozen >= ?",
				amount, amount, spotAccount.id, amount)) {
			throw new ApiException(ApiError.ACCOUNT_UNFREEZE_FAILED);
		}
	}

	/**
	 * Transfer one currency from frozen account to another currency of spot
	 * account.
	 * 
	 * @param userId
	 *            The user id.
	 * @param fromCurrency
	 *            The 'from' currency.
	 * @param fromAmount
	 *            The 'from' amount.
	 * @param toCurrency
	 *            The 'to' currency.
	 * @param toAmount
	 *            The 'to' amount.
	 */
	public void frozenToSpot(long userId, Currency fromCurrency, BigDecimal fromAmount, Currency toCurrency,
			BigDecimal toAmount) {
		assertGreaterThanZero(fromAmount);
		assertGreaterThanZero(toAmount);
		SpotAccount fromAccount = getSpotAccount(userId, fromCurrency);
		SpotAccount toAccount = getSpotAccount(userId, toCurrency);
		if (0 == db.update("UPDATE SpotAccount SET frozen = frozen - ? where id = ? AND frozen >= ?", fromAmount,
				fromAccount.id, fromAmount)) {
			throw new ApiException(ApiError.ACCOUNT_UNFREEZE_FAILED);
		}
		if (0 == db.update("UPDATE SpotAccount SET balance = balance + ? where id = ?", toAmount, toAccount.id)) {
			throw new ApiException(ApiError.ACCOUNT_ADD_BALANCE_FAILED);
		}
	}

	public SpotAccount getSpotAccount(long userId, Currency currency) {
		SpotAccount spotAccount = db.from(SpotAccount.class).where("userId = ? AND currency = ?", userId, currency)
				.first();
		if (spotAccount == null) {
			spotAccount = new SpotAccount();
			spotAccount.userId = userId;
			spotAccount.currency = currency;
			spotAccount.balance = BigDecimal.ZERO;
			spotAccount.frozen = BigDecimal.ZERO;
			db.save(spotAccount);
		}
		return spotAccount;
	}
}
