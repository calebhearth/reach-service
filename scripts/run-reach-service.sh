#!/bin/bash

# Set up classpath and invoke 'java' with it ...


cp=".:./../resources:./../dist/ReachService.jar"

cp=$cp:$(echo ./../lib/*.jar | tr ' ' :)
echo "classpath are: $cp"

#ja="-javaagent:/home/tjopt/GIT_opt/tapjoyoptimization/opt_server/newrelic/newrelic.jar"
#echo "Javaagent: $ja"

exec java -server $gc  -cp $cp com.tapjoy.reach.service.ReachService 
echo $! > pid
