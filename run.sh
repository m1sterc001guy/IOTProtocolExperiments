time=180
# Options: onoff, telemetry, fast, one
model=fast
data=1000
#python python/cpu.py $1 $2 $time $model $data &
#python python/memory.py $1 $2 $time $model $data &
# timer interval (CMD ARG 3) is in milliseconds, other parameters are in seconds
java -cp ./log/:./jars/element-connector-1.0.2.jar:./jars/californium-core-1.0.2.jar:./jars/xmpp-extensions-0.6.2.jar:./jars/xmpp-core-client-0.6.2.jar:./jars/xmpp-core-0.6.2.jar:./jars/xmpp-addr-0.6.2.jar:./jars/org.eclipse.paho.client.mqttv3-1.0.2.jar:./jars/log4j-1.2.17.jar:./jars/rabbitmq-java-client-bin-3.6.0/*:./target/GatewayProtocols-1.0-SNAPSHOT.jar networks.finalproject.RunClient $1 $2 1000 $data $time $model
