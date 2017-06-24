package com.itranswarp.crypto.account;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.itranswarp.crypto.ApiError;
import com.itranswarp.crypto.ApiException;
import com.itranswarp.crypto.store.AbstractService;
import com.itranswarp.crypto.symbol.Currency;

@Component
@Transactional
public class AccountService extends AbstractService {

	public void deposit(long userId, Currency currency, long amount) {
		if (amount <= 0) {
			throw new ApiException(ApiError.PARAMETER_INVALID, "amount", "amount must be greater than 0.");
		}
		SpotAccount spotAccount = getSpotAccount(userId, currency);
		db.update("UPDATE SpotAccount SET balance = balance + ? WHERE id = ?", amount, spotAccount.id);
	}

	public void freeze(long userId, Currency currency, long amount) {
		if (amount <= 0) {
			throw new ApiException(ApiError.PARAMETER_INVALID, "amount", "amount must be greater than 0.");
		}
		SpotAccount spotAccount = getSpotAccount(userId, currency);
		FrozenAccount frozenAccount = getFrozenAccount(userId, currency);
		// transfer from spot account to frozen account:
		if (0 == db.update("UPDATE SpotAccount SET balance = balance - ? WHERE id = ? AND balance >= ?", amount,
				spotAccount.id, amount)) {
			throw new ApiException(ApiError.ACCOUNT_FREEZE_FAILED);
		}
		db.update("UPDATE FrozenAccount SET balance = balance + ? WHERE id = ?", amount, frozenAccount.id);
	}

	public void unfreeze(long userId, Currency currency, long amount) {
		if (amount <= 0) {
			throw new ApiException(ApiError.PARAMETER_INVALID, "amount", "amount must be greater than 0.");
		}
		SpotAccount spotAccount = getSpotAccount(userId, currency);
		FrozenAccount frozenAccount = getFrozenAccount(userId, currency);
		// transfer from spot account to frozen account:
		if (0 == db.update("UPDATE FrozenAccount SET balance = balance - ? WHERE id = ? AND balance >= ?", amount,
				frozenAccount.id, amount)) {
			throw new ApiException(ApiError.ACCOUNT_FREEZE_FAILED);
		}
		db.update("UPDATE SpotAccount SET balance = balance + ? WHERE id = ?", amount, spotAccount.id);
	}

	public SpotAccount getSpotAccount(long userId, Currency currency) {
		SpotAccount spotAccount = db.from(SpotAccount.class).where("userId = ? AND currency = ?", userId, currency)
				.first();
		if (spotAccount == null) {
			spotAccount = new SpotAccount();
			spotAccount.balance = 0;
			spotAccount.currency = currency;
			spotAccount.userId = userId;
			db.save(spotAccount);
		}
		return spotAccount;
	}

	public FrozenAccount getFrozenAccount(long userId, Currency currency) {
		FrozenAccount frozenAccount = db.from(FrozenAccount.class)
				.where("userId = ? AND currency = ?", userId, currency).first();
		if (frozenAccount == null) {
			frozenAccount = new FrozenAccount();
			frozenAccount.userId = userId;
			frozenAccount.currency = currency;
			frozenAccount.balance = 0;
			db.save(frozenAccount);
		}
		return frozenAccount;
	}
}
