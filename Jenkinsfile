//script C:\Program Files (x86)\GnuWin32\bin\
import jenkins.model.Jenkins

node{
stage('Build') { // <2>
        echo 'Hello'
        //bat 'make.exe'
        bat "'C:\Program Files (x86)\GnuWin32\bin\make.exe'" // <3>
        archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
    }

}
