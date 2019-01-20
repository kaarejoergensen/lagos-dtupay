node {
    docker.image('rabbitmq').withRun('-d --hostname rabbit1 -e "RABBITMQ_DEFAULT_USER=rabbitmq" -e "RABBITMQ_DEFAULT_PASS=rabbitmq --name rabbitmq"') { c ->
        docker.image('rabbitmq').inside("--network jenkinsnet") {
            echo 'Image started'
        }
        docker.image('maven:3-alpine').inside('-v /root/.m2:/root/.m2 -v /var/run/docker.sock:/var/run/docker.sock --link --network jenkinsnet') {
            stage('Build') {
                sh 'mvn clean compile'
            }
            stage('Test') {
                sh 'mvn test'
            }
            stage('Create docker images') {
                if (env.BRANCH_NAME != 'master') {
                    sh "mvn package -Dbuild.number=${env.BUILD_NUMBER}"
                }
            }
            stage('Create and push docker images') {
                if (env.BRANCH_NAME == 'master') {
                    sh "mvn deploy -s settings.xml -Dbuild.number=${env.BUILD_NUMBER}"
                }
            }
        }
    }
}