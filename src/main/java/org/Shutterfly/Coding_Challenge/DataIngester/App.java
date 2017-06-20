package org.Shutterfly.Coding_Challenge.DataIngester;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TimeZone;

public class App
{
	public static void main(String[] args)
	{
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter X value for TopXCustomers desired");
		int TopXCustomers=sc.nextInt();
		JSONParser parser = new JSONParser();
		try
		{

			Object obj = parser.parse(new FileReader(
					"input/input.txt"));

			JSONArray msg = (JSONArray) obj;
			Iterator<JSONObject> iterator = msg.iterator();
			int i = 1;

			HashMap<String, Integer> visits = new HashMap<String, Integer>();
 
			HashMap<String, ArrayList<Integer>> visitsArray = new HashMap<String, ArrayList<Integer>>();

			HashMap<String, Integer> noOfPurchases = new HashMap<String, Integer>();
			HashMap<String, Double> expenditures = new HashMap<String, Double>();

			HashMap<String, Double> customer_value = new HashMap<String, Double>();
			HashMap<String,String> Customer_LastName=new HashMap<String,String>();

			while (iterator.hasNext())
			{
				JSONObject jsonObject = (JSONObject) iterator.next();
				i++;
				String type = (String) jsonObject.get("type");

				if (type.equals("SITE_VISIT"))
				{
					String cid = (String) jsonObject.get("customer_id");

					visits.put(cid, visits.getOrDefault(cid, 0) + 1);

					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
					format.setTimeZone(TimeZone.getTimeZone("UTC"));

					String dateInString = (String) jsonObject.get("event_time");
					Date date;

					try
					{
						date = format.parse(dateInString);
						Calendar cal = Calendar.getInstance();
						cal.setTime(date);
						int week = cal.get(Calendar.WEEK_OF_YEAR);
						ArrayList<Integer> al;
						if (visitsArray.containsKey(cid))
						{
							al = visitsArray.get(cid);
						}
						else
						{
							al = new ArrayList<Integer>();
							for (i = 0; i <= 52; i++)
								al.add(0);
						}
						al.set(week, al.get(week) + 1);
						visitsArray.put(cid, al);
					} 
					catch (java.text.ParseException e1)
					{
						e1.printStackTrace();
					}
				} 
				else if (type.equals("ORDER")) 
				{
					String cid = (String) jsonObject.get("customer_id");
					String total_amount = (String) jsonObject.get("total_amount");
					Double amount = Double.parseDouble(total_amount.split("\\s+")[0]);

					noOfPurchases.put(cid, noOfPurchases.getOrDefault(cid, 0) + 1);
					expenditures.put(cid, expenditures.getOrDefault(cid, 0.0) + amount);
				}
				else if(type.equals("CUSTOMER"))
				{
					String cid=(String) jsonObject.get("key");
					String Last_Name= (String) jsonObject.get("last_name");
					Customer_LastName.put(cid, Last_Name);
				}
			}
			double sum_customer_value_per_week = 0;
			double num_customer = 0;

			HashMap<String,Double> LTV_Customer = new HashMap<String, Double>();

			for (String key : visitsArray.keySet())
			{
				int num_visits = 0;
				ArrayList<Integer> al = visitsArray.get(key);
				for (Integer v : al)
				{
					if (v != 0)
					{
						num_visits += v;
						
					}		
					
				}
				
				double num_visits_per_week =(double) num_visits /52;
				double expend_per_visit;
				if (expenditures.containsKey(key))
				{
					expend_per_visit = expenditures.get(key) / noOfPurchases.get(key);
				} 
				else
					expend_per_visit = 0;
				sum_customer_value_per_week += expend_per_visit * num_visits_per_week;

				LTV_Customer.put(key,expend_per_visit * num_visits_per_week*520);  //Putting LTV of a customer into his ID(key)
				num_customer++;
			}
			double a_value = sum_customer_value_per_week / num_customer;
			a_value=a_value*520;
			
			TopXSimpleLTVCustomers(a_value,TopXCustomers,LTV_Customer,Customer_LastName);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
	}
	public static void TopXSimpleLTVCustomers(double a_value, int topXCustomers, HashMap<String,Double> LTV_Customer,HashMap<String, String> Customer_LastName)
	{
		try
		{	
			PrintWriter writer = new PrintWriter("output/output.txt", "UTF-8");
			int count=0;
			System.out.println("\nTOP "+topXCustomers+" LTV Customers are: ");

			writer.println("\nTOP "+topXCustomers+" Customers are: \n");
			System.out.println("CUSTOMER ID:				CUSTOMER LAST_NAME");
			System.out.println("--------------------------------------");
			writer.println(" \nCUSTOMER IDs:				CUSTOMER LAST_NAME ");
			writer.println("-------------------------------------------");
			
			 List<Map.Entry<String , Double>> list = new LinkedList<Map.Entry<String, Double>>( LTV_Customer.entrySet() );
			
			 Collections.sort( list, new Comparator<Map.Entry<String, Double>>()
		     {
		            public int compare( Map.Entry<String, Double> o1, Map.Entry<String, Double> o2 )
		            {
		                return (o2.getValue()).compareTo( o1.getValue() );
		            }
		     });
			 int counter=1;
			for(Entry<String, Double> e : list)
			{
				if(counter<=topXCustomers)
				{
				System.out.println(e.getKey()+" "+e.getValue()+"		"+Customer_LastName.get(e.getKey()));
				writer.println(e.getKey()+" "+e.getValue()+"		"+Customer_LastName.get(e.getKey()));
				counter++;
				}
				else
				break;
			}
			writer.println("\nLTV value computed from data is: "+a_value);
			System.out.println("\nLTV value computed from data is: "+a_value);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date date=new Date();

			writer.println("\nFile updated on "+format.format(date)+" at "+date.getTime());
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
