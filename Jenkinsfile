//script C:\Program Files (x86)\GnuWin32\bin\
import jenkins.model.Jenkins

node{
stage('Build') { // <2>
        echo 'Hello'
        //bat 'make'
        sh "C:/Program Files (x86)/GnuWin32/bin/make.exe" ,  returnStdout: true // <3>
        archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
    }

}
