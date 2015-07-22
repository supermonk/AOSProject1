								ADVANCED OPERATING SYSTEMS
									PROJECT 1


Group Members:

		NARENDRA BIDARI (nxb110020)
		SARANYA SURESH (sxs117530)


Description:

	A distributed banking system has been implemented and it contains hosts(the net Id,s) which in turn contains a list of servers and each server hosts a set of accounts. The initialization of the banking system such as the number of hosts, the number of servers per host, the available port numbers and the account details such as the account name, balance and on which server it is located is present in the configuration file named as “BankingConfigFile.config”.  A client connects to a required server and performs banking activities and can it also request for a snapshot of the system. 


Assumptions:

•	The servers specified in the configuration file must be run in the order specified.
		E.g., If the configuration file contains,
			host1 = net01.utdallas.edu
			host2 = net05.utdallas.edu
			host3 = net03.utdallas.edu
			host4 = net06.utdallas.edu
then the servers must be started in the same order. 
•	Any change in the server and client code requires a new “jar” file to be created.
•	The configuration file and the log4j.properties file must be present in the folder where the “jar” files are present. If the server and client jar files are present on different folders, the configuration file must be placed in both the servers.
•	Client connects to the server, which hosts the account that the client wishes to access.
•	Only one snapshot can be handled at a time for all the servers.



How to run the server?

•	Log on to the machine specified for the ith server and go to the folder where the “jar” file and the configuration file is present and the run the server in the format specified below.
•	Syntax for running the server:    java –jar <server jar> <configuration file>
		E.g., 
      			java –jar BankingSystemServer.jar ./BankingConfigFile.config


How to run the client?

•	Log on to a machine and go to the folder where the “jar” file and the configuration file are present and the run the client in the format specified below.
•	Syntax for running the server:  java –jar <client jar> <account name> <configuration file>
		E.g., 
      			java –jar BankingSystemClient.jar AB12CD ./BankingConfigFile.config


Accomplishments:

•	sctp protocol has been implemented using the delimiter method and the delimiter user is $.
•	Multiple servers per system.
•	Multiple accounts per server.
•	Retrieving balance.
•	Depositing, withdrawing or transferring money.
•	Vector clock implementation.
•	Snapshot implementation.
•	Balance does not go below zero.
•	Handling unexpected termination of server or client.
•	Input validation.
•	Automated testing that provides random inputs for transfer on one client and snapshot on the other as requested by the client.
•	Implemented log4j to log the details of the run.






