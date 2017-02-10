script /usr/bin/make
import jenkins.model.Jenkins

node{
stage('Build') { // <2>
        echo 'Hello'
        bat 'make' // <3>
        archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
    }

}
