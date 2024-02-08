pipeline {
    agent {
        label 'dockernode'
    }
    options {
        timestamps()
        disableConcurrentBuilds()
        ansiColor('xterm')
        timeout(time: 20, unit: 'MINUTES')  //something is really long if it takes 20 minutes
    }
    environment {
        TZ='America/Denver'
        ARTIFACTORY = credentials('frogbuilder-artifactory')
    }
    stages {

        stage ('Artifactory configuration') {
            steps {
                rtServer (
                        id: "ralston-artifactory",
                        url: 'http://ralston.garyclayburg.com:8081/artifactory',
                        credentialsId: 'frogbuilder-artifactory'
                )

                rtMavenDeployer (
                        id: "MAVEN_DEPLOYER",
                        serverId: "ralston-artifactory",
                        releaseRepo: "garyrepo-libs-release-local",
                        snapshotRepo: "garyrepo-libs-snapshot-local",
                        excludePatterns: ["com/example*"] //Keep the integration testing modules out of artifactory.  We want the test results, not the artifacts.
                )

                rtMavenResolver (
                        id: "MAVEN_RESOLVER",
                        serverId: "ralston-artifactory",
                        releaseRepo: "garyrepo-libs-release",
                        snapshotRepo: "garyrepo-libs-snapshot"
                )
                sh 'java -version'
                sh 'sdk list java'
            }
        }
        stage('main build') {
            steps {
                rtMavenRun (
                        //no tool, assume mvn is found on build agent, i.e. sdkman
                        pom: 'pom.xml',
                        goals: 'clean install javadoc:jar source:jar',
                        deployerId: "MAVEN_DEPLOYER",
                        resolverId: "MAVEN_RESOLVER"
                )
            }
        }
        stage('Publish build info') {
            steps {
                rtPublishBuildInfo (
                        serverId: "ralston-artifactory"
                )
            }
        }
    }
    post {
        always {
            junit '**/target/**/TEST-*.xml'
        }
    }
}
