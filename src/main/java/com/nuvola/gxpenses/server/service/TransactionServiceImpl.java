package com.nuvola.gxpenses.server.service;

import com.nuvola.gxpenses.server.repos.AccountRepos;
import com.nuvola.gxpenses.server.repos.TransactionRepos;
import com.nuvola.gxpenses.server.util.DateUtils;
import com.nuvola.gxpenses.shared.domaine.Account;
import com.nuvola.gxpenses.shared.domaine.Transaction;
import com.nuvola.gxpenses.shared.dto.PagedData;
import com.nuvola.gxpenses.shared.dto.TransferTransaction;
import com.nuvola.gxpenses.shared.type.PeriodType;
import com.nuvola.gxpenses.shared.type.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private AccountRepos accountRepos;
    @Autowired
    private TransactionRepos transactionRepos;

    @Override
    public void createNewTransaction(Transaction transaction) {
        String tag = transaction.getTags().trim();
        if (tag.lastIndexOf(",") == 0 || tag.equals("")) {
            transaction.setTags(null);
        } else if (tag.lastIndexOf(",") == (tag.length() - 1)) {
            transaction.setTags(tag.substring(0, tag.lastIndexOf(",")));
        }

        if (transaction.getAmount() == null) {
            transaction.setAmount(0d);
        }

        if (transaction.getDate() == null) {
            transaction.setDate(new Date());
        } else {
            Calendar currentCal = Calendar.getInstance();
            Calendar transactionCal = Calendar.getInstance();
            transactionCal.setTime(transaction.getDate());
            transactionCal.set(Calendar.HOUR, currentCal.get(Calendar.HOUR));
            transactionCal.set(Calendar.MINUTE, currentCal.get(Calendar.MINUTE));
            transactionCal.set(Calendar.SECOND, currentCal.get(Calendar.SECOND));
            transaction.setDate(transactionCal.getTime());
        }

        transaction = transactionRepos.save(transaction);
        accountRepos.updateAccountBalance(transaction.getAccount().getId(), transaction.getAmount());
    }

    @Override
    public void removeTransaction(Long transactionId) {
        Transaction transaction = transactionRepos.findOne(transactionId);
        if (transaction.getDestTransaction() != null) {
            accountRepos.updateAccountBalanceInv(transaction.getDestTransaction().getAccount().getId(),
                    transaction.getAmount());
            transactionRepos.delete(transaction.getDestTransaction());
        }

        accountRepos.updateAccountBalanceInv(transaction.getAccount().getId(), transaction.getAmount());
        transactionRepos.delete(transaction);
    }

    @Override
    public void createNewTransferTransaction(TransferTransaction transfer) {
        if ((transfer.getSourceAccount() != null) && (transfer.getTargetAccount() != null)) {
            if (!(transfer.getSourceAccount().equals(transfer.getTargetAccount()) && (transfer.getAmount() > 0d))) {
                Account sourceAccount = accountRepos.findOne(transfer.getSourceAccount().getId());
                Account destinationAccount = accountRepos.findOne(transfer.getTargetAccount().getId());
                String payeeStr = "Transfert from " + sourceAccount.getName() + " to " + destinationAccount.getName();

                Transaction sourceTrans = new Transaction();
                sourceTrans.setPayee(payeeStr);
                sourceTrans.setType(TransactionType.EXPENSE);
                sourceTrans.setDate(new Date());
                sourceTrans.setAmount(-1 * transfer.getAmount());
                sourceTrans.setAccount(sourceAccount);
                sourceTrans = transactionRepos.save(sourceTrans);

                Transaction destTrans = new Transaction();
                destTrans.setPayee(payeeStr);
                destTrans.setType(TransactionType.INCOME);
                destTrans.setDate(new Date());
                destTrans.setAmount(1 * transfer.getAmount());
                destTrans.setAccount(destinationAccount);
                destTrans = transactionRepos.save(destTrans);

                sourceTrans.setDestTransaction(destTrans);
                destTrans.setDestTransaction(sourceTrans);

                accountRepos.updateAccountBalance(transfer.getSourceAccount().getId(), sourceTrans.getAmount());
                accountRepos.updateAccountBalance(transfer.getTargetAccount().getId(), destTrans.getAmount());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PagedData<Transaction> findByAccountAndDateAndType(Long accountId, PeriodType periodFilter,
                                                              TransactionType type, Integer pageNumber, Integer length) {
        Date startDate = DateUtils.getStartDate(periodFilter, new Date());
        Date endDate = DateUtils.getEndDate(periodFilter, new Date());
        PageRequest page = new PageRequest(pageNumber, length, new Sort(Sort.Direction.DESC, "date"));

        Page<Transaction> transactions;
        if (type == TransactionType.ALL) {
            transactions = transactionRepos.findByAccountIdAndDateBetween(accountId, startDate, endDate, page);
        } else {
            transactions = transactionRepos.findByAccountIdAndDateBetweenAndType(accountId, startDate, endDate,
                    type, page);
        }

        return new PagedData<Transaction>(transactions.getContent(), (int) transactions.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Double totalAmountByAccountAndPeriodAndType(Long accountId, PeriodType periodFilter, TransactionType type) {
        Date startDate = DateUtils.getStartDate(periodFilter, new Date());
        Date endDate = DateUtils.getEndDate(periodFilter, new Date());

        if (type == TransactionType.ALL) {
            return transactionRepos.totalByAccountAndDate(accountId, startDate, endDate);
        } else {
            return transactionRepos.totalByAccountAndTypeAndDate(accountId, type, startDate, endDate);
        }
    }

}
