package org.hwx.online.sme.security.demo.storm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;
import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;

public class POSEventProcessorTopology {
	private static final Logger LOG = Logger.getLogger(POSEventProcessorTopology.class);
	private Properties topologyConfig;
	
	public POSEventProcessorTopology(String configFileLocation) throws Exception{
		topologyConfig = new Properties();
		try {
			topologyConfig.load(new FileInputStream(configFileLocation));
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
	
	private void buildAndSubmit() throws Exception {
		TopologyBuilder builder = new TopologyBuilder();
		
		/* Set up Kafka Spout to ingest from */
		configureKafkaSpout(builder);
		
		/* Setup HBse Bolt for to persist violations and all events (if configured to do so)*/
//		configureHBaseBolt(builder);
		
		/* This conf is for Storm and it needs be configured with things like the following:
		 * 	Zookeeper server, nimbus server, ports, etc... All of this configuration will be picked up
		 * in the ~/.storm/storm.yaml file that will be located on each storm node.
		 */
		Config conf = new Config();
		conf.setDebug(true);	
		/* Set the number of workers that will be spun up for this topology. 
		 * Each worker represents a JVM where executor thread will be spawned from */
		Integer topologyWorkers = Integer.valueOf(topologyConfig.getProperty("storm.trucker.topology.workers"));
		conf.put(Config.TOPOLOGY_WORKERS, topologyWorkers);
		
		try {
			StormSubmitter.submitTopology("truck-event-processor", conf, builder.createTopology());	
		} catch (Exception e) {
			LOG.error("Error submiting Topology", e);
		}
	}
	
//	public void configureHBaseBolt(TopologyBuilder builder) {
//		HBaseBolt hbaseBolt = new HBaseBolt(topologyConfig);
//		builder.setBolt("hbase_bolt", hbaseBolt, 2 ).shuffleGrouping("kafkaSpout");
//	}
	
	public int configureKafkaSpout(TopologyBuilder builder) {
		KafkaSpout kafkaSpout = constructKafkaSpout();
		
		int spoutCount = Integer.valueOf(topologyConfig.getProperty("spout.thread.count"));
		int boltCount = Integer.valueOf(topologyConfig.getProperty("bolt.thread.count"));
		
		builder.setSpout("kafkaSpout", kafkaSpout, spoutCount);
		return boltCount;
	}



	/**
	 * Construct the KafkaSpout which comes from the jar storm-kafka-0.8-plus
	 * @return
	 */
	private KafkaSpout constructKafkaSpout() {
		KafkaSpout kafkaSpout = new KafkaSpout(constructKafkaSpoutConf());
		return kafkaSpout;
	}

	/**
	 * Construct 
	 * @return
	 */
	private SpoutConfig constructKafkaSpoutConf() {
		BrokerHosts hosts = new ZkHosts(topologyConfig.getProperty("kafka.zookeeper.host.port"));
		String topic = topologyConfig.getProperty("kafka.topic");
		String zkRoot = topologyConfig.getProperty("kafka.zkRoot");
		String consumerGroupId = topologyConfig.getProperty("kafka.consumer.group.id");
		
		SpoutConfig spoutConfig = new SpoutConfig(hosts, topic, zkRoot, consumerGroupId);
		
		/* Custom TruckScheme that will take Kafka message of single truckEvent 
		 * and emit a 2-tuple consisting of truckId and truckEvent. This driverId
		 * is required to do a fieldsSorting so that all driver events are sent to the set of bolts */
//		spoutConfig.scheme = new SchemeAsMultiScheme(new TruckScheme2());
		
		return spoutConfig;
	}
	
	public static void main(String[] args) throws Exception {
		String configFileLocation = args[0];
		POSEventProcessorTopology truckTopology = new POSEventProcessorTopology(configFileLocation);
		truckTopology.buildAndSubmit();
		
	}	
}
