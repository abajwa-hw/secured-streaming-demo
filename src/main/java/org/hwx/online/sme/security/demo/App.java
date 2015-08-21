package org.hwx.online.sme.security.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.apache.log4j.Logger;


/**
 * Hello world!
 *
 */
public class App {
	private static final long serialVersionUID = -2990121166902741545L;
	private static final Logger LOG = Logger.getLogger(App.class);
	private static Properties props = null;
	private static String csvFileLocation = null;
	private static List<POSRecord> posRecordList = null;
    
	public static void main( String[] args ){
        
        try {
 			loadProperties();
 			loadCsvFile();
 			submitToKafka();
 			
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
    }
	
	private static void loadProperties() throws Exception{
    	
    	try {
    		
    		props = new Properties();
			props.load(new FileInputStream("./config.properties"));
			
    	} catch (FileNotFoundException e) {
			LOG.error("Encountered error while reading configuration properties: "
					+ e.getMessage());
			throw e;
		} catch (IOException e) {
			LOG.error("Encountered error while reading configuration properties: "
					+ e.getMessage());
			throw e;
		}	
    }
	
	private static void loadCsvFile() throws Exception{
		csvFileLocation = props.getProperty("pos.location");
		
		File file = new File(csvFileLocation);
		if(!file.exists()){
			System.out.println("CSV file cannot be found");
			System.exit(0);
		} else{
			
			posRecordList = new ArrayList<POSRecord>();
			
			BufferedReader br = new BufferedReader(new FileReader(file));
			 
			String line = null;
			
			while ((line = br.readLine()) != null) {
				if(line.contains("Transaction_date")) {continue;}
				
				String values[] = line.split(",");
				POSRecord record = new POSRecord();
				
				record.setTransactionDate(values[0]);
				record.setProduct(values[1]);
				record.setPrice(Integer.valueOf(values[2]));
				record.setPaymentType(values[3]);
				record.setConsumerName(values[4]);
				record.setCity(values[5]);
				record.setState(values[6]);
				record.setCountry(values[7]);
				record.setAccountCreatedDate(values[8]);
				record.setLastLoginDate(values[9]);
				record.setLongitude(values[10]);
				record.setLatitude(values[11]);
				record.setKey(values[4] + values[0] + values[10] + values[11]);
				
				posRecordList.add(record);
			}
			
			br.close();
		}
	}
	/**
	 * When runing this pass the kafka_client_jass.conf file i.e. -Dblah_blah_blah
	 * @throws Exception
	 */
	private static void submitToKafka() throws Exception{
		Properties kafkaProps = new Properties();
		kafkaProps.put("metadata.broker.list", props.get("metadata.broker.list"));
		kafkaProps.put("serializer.class", props.get("serializer.class"));
		kafkaProps.put("security.protocol", props.get("security.protocol"));
		
		ProducerConfig config = new ProducerConfig(kafkaProps);
		Producer<String, String> producer = new Producer<String, String>(config);
		
		if(!posRecordList.isEmpty()){
			Iterator<POSRecord> itr = posRecordList.iterator();
			while(itr.hasNext()){
				POSRecord posRecord = itr.next();
				KeyedMessage<String, String> data = new KeyedMessage<String, String>(props.getProperty("topic.pos"), posRecord.getKey(), posRecord.convertToMap().toString());
				producer.send(data);
			}
			System.out.println("All records have been sent to Kafka.....");
			System.exit(0);
		} else{
			System.out.println("There are no records to be sent at this time.....");
			System.exit(0);
		}
	}
}
