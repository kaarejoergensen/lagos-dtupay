pipeline {
    agent {
        docker {
            image 'maven:3-alpine'
            args '-v /root/.m2:/root/.m2 -v /var/run/docker.sock:/var/run/docker.sock'
        }
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Create docker images') {
            steps {
                sh "mvn package -Dbuild.number=${env.BUILD_NUMBER}"
            }
        }
        stage('Push docker images') {
            when {
                branch 'master'
            }
            steps {
                sh "mvn deploy -s settings.xml -Dbuild.number=${env.BUILD_NUMBER}"
            }
        }
    }
}
