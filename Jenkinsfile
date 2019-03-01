#!groovy
def starttime = System.currentTimeMillis()
stage "provision build node"
node('coreosnode') {  //this node label must match jenkins slave with nodejs installed
    println("begin: build node ready in ${(System.currentTimeMillis() - starttime) / 1000}  seconds")
    wrap([$class: 'TimestamperBuildWrapper']) {  //wrap each Jenkins job console output line with timestamp
        stage "build setup"
        checkout scm
        whereami()

        stage "build/test"
        try {
            sh "mvn clean install"
//        sh "./gradlew --no-daemon clean build buildImage pushVersion pushLatest"
        } catch (ex) {
            throw ex
        } finally {
            step([$class: 'JUnitResultArchiver', testResults: 'target/**/TEST-*.xml'])
        }
        stage "archive"

        println "flow complete!"
    }
}
private void whereami() {
    /**
     * Runs a bunch of tools that we assume are installed on this node
     */
    echo "Build is running with these settings:"
    sh "pwd"
    sh "ls -la"
    sh "echo path is \$PATH"
    sh """
uname -a
java -version
mvn -v
docker ps
docker info
#docker-compose -f src/main/docker/app.yml ps
docker-compose version
npm version
gulp --version
bower --version
"""
}
