//script C:\Program Files (x86)\GnuWin32\bin\
//import jenkins.model.Jenkins

node{
stage('Build') { // <2>
        echo 'Hello'
        sh 'docker version'
        //bat 'make'
        //sh "C:/Program Files (x86)/GnuWin32/bin/make.exe"  // <3>
        //echo 'Done with make'
        archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
    }

}
