package printme;

/**
 * Error messages for every transaction error.
 * @author Narendra Bidari
 * @author Saranya Suresh
 */

public class Helper 
{
    
	public void Help() 
    {
		System.out.println("**************************************************************************************");
		System.out.println("**************************************HELP DOCUMENT***********************************");
		System.out.println("1. To view balance.");
		System.out.println("       Type 'Balance'                                          --(Balance)");
		System.out.println("2. To deposit money into your account.");
		System.out.println("       Type 'Deposit <amount>'                                 --(Deposit 500)");
		System.out.println("3. To withdraw money your account.");
		System.out.println("       Type 'Withdraw <amount>'                                --(Withdraw 250)");
		System.out.println("4. To transfer money to a different account.");
		System.out.println("       Type 'Transfer <destination account number> <amount>'   --(Transfer AB12CD 700)");
        System.out.println("5. To get a snapshot");
		System.out.println("       Type 'Snapshot'                                         --(Snapshot)");
        System.out.println("6. To generate automatic snapshots");
		System.out.println("       Type 'GSnap <Iteration times>'                          --(GSnap 3)");
        System.out.println("7. To generate automate testing");
		System.out.println("       Type 'GTrans <Iteration times> <Active Servers>'        --(GTrans 4 2)");
		System.out.println("8. To exit.");
		System.out.println("       Type 'Exit'");
		System.out.println("***************************************DOCUMENT END***********************************");
		System.out.println("**************************************************************************************");
		System.out.println();
	}
	
    public void DepositError() {
		
		System.out.println("***************************************DEPOSIT ERROR**********************************");
		System.out.println("Invalid Amount or No amount was specified as an argument.");
		System.out.println("**************************************************************************************");
		System.out.println();
		System.out.println("The valid options are:");
		System.out.println();
		Help();
        
	}
    
    public void WithdrawError() {
		
		System.out.println("****************************************WITHDRAW ERROR********************************");
		System.out.println("Invalid Amount or No amount was specified as an argument.");
		System.out.println("**************************************************************************************");
		System.out.println();
		System.out.println("The valid options are:");
		System.out.println();
		Help();
	}
    
    public void BalanceError() {
		
		System.out.println("****************************************BALANCE ERROR*********************************");
		System.out.println("Invalid input for Balance.");
		System.out.println("**************************************************************************************");
		System.out.println();
		System.out.println("The valid options are:");
		System.out.println();
		Help();
	}
    
    public void TransferError() {
		
		System.out.println("*****************************************TRANSFER ERROR*******************************");
		System.out.println("Invalid Amount/Account ");
		System.out.println("**************************************************************************************");
		System.out.println();
		System.out.println("The valid options are:");
		System.out.println();
		Help();
	}
    
    public void SnapshotError() {
		
		System.out.println("*****************************************SNAPSHOT ERROR*******************************");
		System.out.println("Invalid input for Snapshot.");
		System.out.println("**************************************************************************************");
		System.out.println();
		System.out.println("The valid options are:");
		System.out.println();
		Help();
	}
    
    public void TestingError() {
		
		System.out.println("************************************AUTOMATED TESTING ERROR***************************");
		System.out.println("Invalid input for Automated Testing.");
		System.out.println("**************************************************************************************");
		System.out.println();
		System.out.println("The valid options are:");
		System.out.println();
		Help();
	}
}
