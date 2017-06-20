			SHUTTERFLY DATA INGESTER CHALLENGE:
			----------------------------------
Project description:

The project aims at finding the LTV(Life time value of customers) from the json format input data file and eventually writes to the output file, the top 'X' customers who have the LTV value computed. Output file also has the LTV value of the data.

Steps to run the project:

1) The project DataIngester is a Maven project which has its dependency file(pom.xml) in the root folder.

2) In the source folder(src), java file 'App.java' is present in 'src/main/java/org/Shutterfly/Coding_Challenge/DataIngester/' path which has the complete project code.

3) Running the project using Eclipse:

This project can be run in Eclipse IDE by choosing the working repository location to be DataIngester's.
Eventually, in DataIngester package, we can notice src/main/java and in which exists org.Shutterfly.Coding_Challenge.DataIngester  package. It contains App.java which has the project code.

This file has to be run which prints output to output.txt file in the output folder. Additionally, output is parallely printed in the console.

Path to the output.txt file: /output/output.txt(This file refreshes each time the code runs with timestamp of file update at the end of file)


IMPORTANT POINTS AND ASSUMPTIONS ABOUT THE PROJECT:

1) Maven dependency was added as a part of the pom.xml file in which json-simple artifact was included for Json parser handle to be used. 
Dependency snippet:
	<dependency>
			<groupId>com.googlecode.json-simple</groupId>
			<artifactId>json-simple</artifactId>
			<version>1.1.1</version>
		</dependency>
  </dependencies>

2)  As mentioned that though all events have a key and event_time, there is no guarantee regarding frequency and order of occurence, JSONParser was used provided by json-simple artifact to extract required dimensions based on their Ids.

3) The dataset was assumed to be complete for the given time frame from input dataset. Thus the missing data(regarding visits/week) if got to be 0 was also considered.

4) All the data is ingested though few parts donot contribute towards LTV calculation with intention of futuristic use if needed.

Edge cases assumed:

5) If a customer's ID in  SITE_VISIT dimension table was not present but accidentally present in ORDER dimension table, then the contribution to LTV factor was not considered.
	
A customer's visit does not guarantee customer's order but his order guarantees his visit.

6) If a customer's visit in a given week number was 0, then the contribution to the average visits per week is not neglected(It lowers his average contribution towards LTV).

Output:

The output file comprises of topXLTVCustomer Ids at the given point of time along with the LTV value computed.

X value for number of customers is taken at run time and gives out based onuser input(for desired number of customers)

The output folder has sample output for top 10 customers.

Time stamp which records file update was mentioned at end.
