pipeline {
    agent any

    environment {
        NEXUS_REGISTRY = "localhost:8082"
        IMAGE_NAME = "java-app"
        IMAGE_TAG = "v1"
    }

    stages {

        stage('Checkout') {
            steps {
                // Utiliser des credentials GitHub si le repo est privé
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/master']],
                    doGenerateSubmoduleConfigurations: false,
                    extensions: [[$class: 'CloneOption', timeout: 30]], // timeout en minutes
                    userRemoteConfigs: [[
                        url: 'https://github.com/maroua-maliki/Collaborative-Text-Annotation-Platform-for-NLP.git',
                        credentialsId: 'github-creds' // mettre l'ID de vos credentials GitHub ici
                    ]]
                ])
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build JAR') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $NEXUS_REGISTRY/$IMAGE_NAME:$IMAGE_TAG .'
            }
        }

        stage('Push to Nexus') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'nexus-creds',
                    usernameVariable: 'NEXUS_USER',
                    passwordVariable: 'NEXUS_PASS'
                )]) {
                    sh '''
                        docker login $NEXUS_REGISTRY -u $NEXUS_USER -p $NEXUS_PASS
                        docker push $NEXUS_REGISTRY/$IMAGE_NAME:$IMAGE_TAG
                    '''
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline terminé."
        }
    }
}
