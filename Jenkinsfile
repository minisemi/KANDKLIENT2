node{
stage('Build') { // <2>
        echo 'Hello'
        bat 'make' // <3>
        archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
    }

}
