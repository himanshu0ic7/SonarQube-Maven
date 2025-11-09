
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import mavenLearning.AccountService;
import mavenLearning.FundTransferService;

public class FundTransferServiceTest {

    private AccountService accountService;
    private FundTransferService fundTransferService;

    @BeforeEach
    void setUp() {
        accountService = Mockito.mock(AccountService.class);
        fundTransferService = new FundTransferService(accountService);
    }

    @Test
    void testInvalidAmount() {
        String result = fundTransferService.transfer("A1", "A2", -100);
        assertEquals("FAILURE: Invalid amount", result);
    }

    @Test
    void testSameAccountTransfer() {
        String result = fundTransferService.transfer("A1", "A1", 500);
        assertEquals("FAILURE: Source and destination cannot be same", result);
    }

    @Test
    void testInsufficientFunds() {
        when(accountService.getBalance("A1")).thenReturn(100.0);
        String result = fundTransferService.transfer("A1", "A2", 500);
        assertEquals("FAILURE: Insufficient funds", result);
    }

    @Test
    void testDestinationAccountNotFound() {
        when(accountService.getBalance("A1")).thenReturn(1000.0);
        when(accountService.exists("A2")).thenReturn(false);

        String result = fundTransferService.transfer("A1", "A2", 200);
        assertEquals("FAILURE: Destination account not found", result);
    }

    @Test
    void testTransactionSuccess() {
        when(accountService.getBalance("A1")).thenReturn(1000.0);
        when(accountService.exists("A2")).thenReturn(true);
        when(accountService.debit("A1", 200)).thenReturn(true);
        when(accountService.credit("A2", 200)).thenReturn(true);

        
        String result = fundTransferService.transfer("A1", "A2", 200);
        assertEquals("SUCCESS: Transfer completed", result);

        verify(accountService).debit("A1", 200);
        verify(accountService).credit("A2", 200);
    }

    @Test
    void testTransactionFailure() {
        when(accountService.getBalance("A1")).thenReturn(1000.0);
        when(accountService.exists("A2")).thenReturn(true);
        when(accountService.debit("A1", 200)).thenReturn(true);
        when(accountService.credit("A2", 200)).thenReturn(false);

        String result = fundTransferService.transfer("A1", "A2", 200);
        assertEquals("FAILURE: Transaction error", result);
        
    }
}