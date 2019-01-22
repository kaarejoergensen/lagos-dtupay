node {
    checkout scm
    docker.image('maven:3-alpine').inside('-v /root/.m2:/root/.m2 -v /var/run/docker.sock:/var/run/docker.sock --network jenkinsnet') {
        stage('Build') {
            sh 'mvn clean compile'
        }
        stage('Test') {
            sh 'mvn test'
        }
        stage('Create docker images') {
            sh "mvn package -Dbuild.number=${env.BUILD_NUMBER} -Dmaven.test.skip=true"
        }
        stage('Integration test') {
            sh "mvn verify -Ddockerfile.skip=true -DintegrationTestOnly=true"
        }
        stage('Push docker images') {
            if (env.BRANCH_NAME == 'master') {
                sh "mvn deploy -s settings.xml -Dbuild.number=${env.BUILD_NUMBER} -Ddockerfile.build.skip=true -Dmaven.test.skip=true"
            }
        }
    }
}