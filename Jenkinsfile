node {
    checkout scm
    docker.image('rabbitmq').withRun('-d --hostname rabbit1 -e "RABBITMQ_DEFAULT_USER=rabbitmq" -e "RABBITMQ_DEFAULT_PASS=rabbitmq" --name rabbitmq --network jenkinsnet') { c ->
        docker.image('mongo').withRun('--name mongo --network jenkinsnet') { m ->
            docker.image('maven:3-alpine').inside('-v /root/.m2:/root/.m2 -v /var/run/docker.sock:/var/run/docker.sock --network jenkinsnet') {
                stage('Build') {
                    sh 'mvn clean compile'
                }
                stage('Test') {
                    sh 'mvn test'
                }
                stage('Create docker images') {
                    if (env.BRANCH_NAME != 'master') {
                        sh "mvn package -Dbuild.number=${env.BUILD_NUMBER} -Dmaven.test.skip=true"
                    }
                }
                stage('Create and push docker images') {
                    if (env.BRANCH_NAME == 'master') {
                        sh "mvn deploy -s settings.xml -Dbuild.number=${env.BUILD_NUMBER} -Dmaven.test.skip=true"
                    }
                }
            }
        }
    }
}