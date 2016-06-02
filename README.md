# notification-server

### Executing Command
java -jar target/notification-server-0.0.1-SNAPSHOT-fat.jar -conf configuration.json

java -jar target/notification-server-0.0.1-SNAPSHOT-fat.jar start --java-opts="-Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory" -conf configuration.json --redirect-output > out.log

### API Browser URI
https://localhost:8543/?raml=/api/api.raml
https://mbldevapp1.dev.devry.edu:8543/?raml=/api/api.raml


