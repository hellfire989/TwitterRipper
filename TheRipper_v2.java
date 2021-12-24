//The Twitter Ripper V2 was created by Twitter: _Asidy on 04/08/2021
import twitter4j.*;
import java.util.List;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * TO FIX: Number of tweets and retweets display improper if not re-ran. just do = 0 at top
 * Things I kinda wanna add:
 * Toggle to enable console output (Y/N)
 * Write to default text file or new textfile with custom name (Y/N)
 * Add timer to calculate run time.
 * 
 * Much later on: Choose output type .txt/.json
 */
public class TheRipper_v2 {
	
	private static PrintStream consolePrint;
	
	public static void main(String args[]) throws TwitterException, IOException{
		String response;
		Scanner keyboard = new Scanner (System.in);
		consolePrint = System.out;
		
		Tweeter_v2 twitterObj = new Tweeter_v2(consolePrint);
		//Gets rid of the junk above the console output
		for(int i = 0; i < 10; i++) {
			System.out.println("\n");
		}
		//Prompts user for choice
		do{
			System.out.println(" ");
			System.out.println(" 1. Tweet Out");				//Sends out a tweet
			System.out.println(" 2. Fetch All Tweets");			//Fetches all Tweets & Retweets from a User
			System.out.println(" 3. Fetch User Tweets");		//Fetches all Tweets from a User
			System.out.println(" 4. Fetch Most Tweeted Day");	//Fetches Day that a given User tweets the most
			System.out.println(" 5. Send a Direct Message");	//Sends a direct message to a given user
			System.out.println(" ");							//Return for formatting
			System.out.println(" 0. Quit Program");				//Quits the program
			System.out.println(" ");							//Return for formatting
			System.out.print("Enter your choice: ");			//User Prompt
			response=keyboard.next();							//User input
			
	        if(response.contentEquals("0")) {					//Quit Case
	        	System.out.println("Quitting");
	        	System.exit(0);
	        }
			switch (response){
				case "1": twitterObj.sendTweet(); 				//Sends out a tweet
					break;
				case "2": twitterObj.fetchAllTweets();			//Gets all user tweets and retweets
					break;
				case "3": twitterObj.fetchUserTweets();			//Gets all user tweets 
					break;
				case "4": twitterObj.mostTweetedDayOfWeek();	//Gets most tweeted day of the week
					break;
				case "5": twitterObj.sendDirectMessageToUser();	//Sends direct message to user
				default:
					System.out.println("Not Valid Input");
			}   
		} while (!response.equals("0"));
	}
}

class Tweeter_v2 {
   private Twitter twitter;
   private List<Status> statuses;
   private String userInput;
  
   /*
    * Constructor for the Tweeter_V2 object
    */
   public Tweeter_v2(PrintStream console) {
      // Makes an instance of Twitter - this is re-useable and thread safe.
      // Connects to Twitter and performs authorizations.
      twitter = TwitterFactory.getSingleton(); 
      statuses = new ArrayList<Status>();
   }
   
   /*
    * This method tweets out a tweet from your account
    * based on the input that you give the method.
    */
   public void sendTweet() throws TwitterException, IOException {
	   Scanner input = new Scanner (System.in);
	   
	   System.out.print("Enter what message you'd like to tweet out: ");
	   userInput = input.nextLine();
	   
	   //Sends Tweet
	   twitter.updateStatus(userInput);
   }
   
   /*
    * This method gets all tweets a User can view on someones timeline
    * The amount this method gets may be less than what Twitter says
    * I'm not yet sure why that is...
    */
   public void fetchAllTweets() throws TwitterException, IOException {
	   statuses.clear();
	   //String for userAt
	   String atOfUser;
	   //User Variable for parsing from Twitter
	   User user;
	   //Count for number of tweets and retweets
	   double numUserTweets = 0;
	   int countRetweets = 0;
	   int numberTweets = 0;
	   //Scanner for user Input
	   Scanner input = new Scanner (System.in);
	   
	   PrintStream fileout = new PrintStream(new FileOutputStream("tweets.txt"));
	   
	   //User input
	   System.out.print("Enter the @ of the person whose tweets you want to rip: ");
	   atOfUser = input.nextLine();
	   System.out.println("\tRunning. . .");
	   
	   //Creates user object if @ is valid
	   //Try catch for if @ is not found
	   try {
		   user = twitter.showUser(atOfUser);
		   numUserTweets = user.getStatusesCount();
	   }catch(Exception e) {
		   System.out.println("User not found");
	   }
	   Paging page = new Paging (1,40);
	   int currPage = 1;
	   //Calcualtes total number of pages
	   //Number of tweets/40, rounded up to nearest whole int
	   double pageCount = Math.ceil(numUserTweets/40);
	   //Goes through all pages
       while (currPage <= pageCount){
          page.setPage(currPage);
          statuses.addAll(twitter.getUserTimeline(atOfUser,page)); 
          currPage++;     
       }
       numberTweets = statuses.size();
       //Count number of retweets
       for (Status userTweet: statuses){
    	   if(userTweet.getText().contains("RT")) {
      		 countRetweets++;
      		 String currentStatus = userTweet.getCreatedAt().toString();
      	 	}
       }
       //File output
       fileout.println("Username parsed from program: " + atOfUser + "\n");
       fileout.println("Tweets displayed on twitter: "  + (int)numUserTweets + "\n");
       fileout.println("Actual tweets: " + numberTweets + "\n");
       
       //Goes through all tweets parsed
       int count=1;
       for (Status userTweet: statuses){
    	   fileout.println(userTweet.getCreatedAt().toString() + "\n");
    	   fileout.println("\t" + count + ".  " + userTweet.getText() + "\n");
    	   
    	   count++;
    	}
       System.out.println("\tDone.");
   }
   
   /*
    * This method gets all tweets a User can view on someones timeline
    * with the exception of their retweets.
    * The amount this method gets may be less than what Twitter says
    * I'm not yet sure why that is...
    */
   public void fetchUserTweets() throws TwitterException, IOException {
	   statuses.clear();
	   //String for userAt and text file name
	   String atOfUser;
	   String textFileName = "tweets.txt";
	   String yesOrNo = "N";
	   //User Variable for parsing from Twitter
	   User user;
	   //Count for number of tweets and retweets
	   double numUserTweets = 0;
	   int countRetweets = 0;
	   int numberTweets = 0;
	   boolean customTextFile = false;
	   //Scanner for user Input
	   Scanner input = new Scanner (System.in);
	   
	   
	   
	   //User input
	   System.out.print("Enter the @ of the person whose tweets you want to rip: ");
	   atOfUser = input.nextLine();
	   System.out.print("Would you like to use default text file? (tweets.txt) (Y/N): ");
	   yesOrNo = input.nextLine();
	   if(yesOrNo.toLowerCase().equals("y")) {
		   System.out.print("Enter your custom text file name (.txt not needed): ");
		   textFileName = input.nextLine();
		   textFileName += ".txt";
	   }
	   System.out.println("\tRunning. . .");
	   
	   
	   PrintStream fileout = new PrintStream(new FileOutputStream(textFileName));
	   //Creates user object if @ is valid
	   //Try catch for if @ is not found
	   try {
		   user = twitter.showUser(atOfUser);
		   numUserTweets = user.getStatusesCount();
	   }catch(Exception e) {
		   System.out.println("User not found");
	   }
	   Paging page = new Paging (1,40);
	   int currPage = 1;
	   //Calcualtes total number of pages
	   //Number of tweets/40, rounded up to nearest whole int
	   double pageCount = Math.ceil(numUserTweets/40);
	   //Goes through all pages
       while (currPage <= pageCount){
          page.setPage(currPage);
          statuses.addAll(twitter.getUserTimeline(atOfUser,page)); 
          currPage++;     
       }
       numberTweets = statuses.size();
       //Count number of retweets
       for (Status userTweet: statuses){
    	   if(userTweet.getText().contains("RT")) {
      		 countRetweets++;
      		 String currentStatus = userTweet.getCreatedAt().toString();
      	 	}
       }
       //File output
       fileout.println("Username parsed from program: " + atOfUser + "\n");
       fileout.println("Tweets displayed on twitter: "  + (int)numUserTweets + "\n");
       fileout.println("Number of user retweets: " + countRetweets + "\n");
       fileout.println("Actual tweets: " + numberTweets + "\n");
       
       //Goes through all tweets parsed
       int count=1;
       for (Status userTweet: statuses){
    	   if(userTweet.getText().contains("RT")) {
    		   //Do nothing if it's a retweet
    	   }
    	   else {
    		   fileout.println(userTweet.getCreatedAt().toString() + "\n");
    		   fileout.println("\t" + count + ".  " + userTweet.getText() + "\n");
  
    	   }
    	   count++;
    	}
       System.out.println("\tDone.");
   }
   
   /*
    * This method sends a message to a given user
    */
   public void sendDirectMessageToUser() throws TwitterException, IOException {
	   //Scanner and String instance variables
	   Scanner input = new Scanner (System.in);
	   String atOfUser;
	   String messageToBeSent;
	   
	   System.out.print("Enter the @ of the person you want to direct message: ");
	   atOfUser = input.nextLine();
	   
	   System.out.print("What do you want the message to say?: ");
	   messageToBeSent = input.nextLine();
	   
	   //Creates user object with the @ of the inputted username
	   try {
		   User user = twitter.showUser(atOfUser);
		   long userID = user.getId();
		   //Actually sends the direct message
		   twitter.sendDirectMessage(user.getId(), messageToBeSent);
		   System.out.println("Sent: " + messageToBeSent + " to @" + userInput);
	   }catch(Exception e) {
		   System.out.println("User not found");
	   }
   }
   /*
    * This method gets the day of the week that  
    */
   public void mostTweetedDayOfWeek() throws TwitterException, IOException {
	   statuses.clear();
	   /*
	    * String for userAt, Current status day, and first 3 char of string
	    * User Variable for parsing from Twitter
	    * Count for number of tweets and retweets
	    * Scanner for user Input
	    * PrintStream to print to output file
	    * Int array to count instances of days 0 - Monday, 6 - Sunday
	    */
	   String atOfUser;
	   String workingString;
	   String firstThree;
	   int max = -1;
	   String mostTweetedDay = "";
	   User user;
	   int days[] = new int[7];
	   //To find which day of the week has the most uses
	   String week[][] = {
			   {"Monday    ","0"}, 
			   {"Tuesday   ","0"}, 
			   {"Wednesday ","0"}, 
			   {"Thursday  ","0"}, 
			   {"Friday    ","0"}, 
			   {"Satday    ","0"}, 
			   {"Sunday    ","0"}
			   };
	   double numUserTweets = 0;
	   Scanner input = new Scanner (System.in);
	   PrintStream fileout = new PrintStream(new FileOutputStream("userInformation.txt"));
	   
	   //User input
	   System.out.print("Enter the @ of the person to find their most tweeted day: ");
	   atOfUser = input.nextLine();
	   System.out.println("\tRunning. . .");
	   
	   //Creates user object if @ is valid
	   //Try catch for if @ is not found
	   try {
		   user = twitter.showUser(atOfUser);
		   numUserTweets = user.getStatusesCount();
	   }catch(Exception e) {
		   System.out.println("User not found");
	   }
	   Paging page = new Paging (1,40);
	   int currPage = 1;
	   //Calcualtes total number of pages
	   //Number of tweets/40, rounded up to nearest whole int
	   double pageCount = Math.ceil(numUserTweets/40);
	   //Goes through all pages
       while (currPage <= pageCount){
          page.setPage(currPage);
          statuses.addAll(twitter.getUserTimeline(atOfUser,page)); 
          currPage++;     
       }
       //Go through all tweets
       for (Status userTweet: statuses){
    	   workingString = userTweet.getCreatedAt().toString();
    	   firstThree = workingString.substring(0,3);
    	   switch (firstThree){
			case "Mon": days[0]++;
				break;
			case "Tue": days[1]++;
				break;
			case "Wed": days[2]++;
				break;
			case "Thu": days[3]++;
				break;
			case "Fri": days[4]++;
				break;
			case "Sat": days[5]++;
				break;
			case "Sun": days[6]++;
				break;
			default:
				
		   }
       }
       System.out.println("\tDone.\n");
       //Changes int array to Strings for 2d array
       for(int i = 0; i < 7; i++) {
    	   week[i][1] = String.valueOf(days[i]);
       }
       //Find most tweeted day
       for(int i = 0; i < 7; i++) {
    	   int currNum = Integer.valueOf(week[i][1]);
    	   if(currNum > max) {
    		   mostTweetedDay = week[i][0];
    		   max = currNum;
    	   }
       }
       //Output
       for(int i = 0; i < 7; i++) {
    	   System.out.println(week[i][0] + week[i][1]);
       }
       System.out.println("\nMost Tweeted Day: " + mostTweetedDay);
       System.out.println("");
       //File output
       fileout.println("");
       
       
   }
}