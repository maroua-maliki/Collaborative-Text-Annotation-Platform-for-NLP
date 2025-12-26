pipeline {
    agent any

    tools {
        maven 'maven_3'
    }

    environment {
        // 'nexus' est le nom du conteneur dans votre docker ps
        NEXUS_REGISTRY = "nexus:8081"
        IMAGE_NAME = "java-app"
        IMAGE_TAG = "v1"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master',
                    url: 'https://github.com/maroua-maliki/Collaborative-Text-Annotation-Platform-for-NLP.git',
                    credentialsId: 'github-token'
            }
        }

        stage('Test') {
            steps {
                // Le -U force le retéléchargement des fichiers corrompus
                sh 'mvn -U test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build JAR') {
            steps {
                sh 'mvn -U clean package -DskipTests'
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
                        docker login -u $NEXUS_USER -p $NEXUS_PASS $NEXUS_REGISTRY
                        docker push $NEXUS_REGISTRY/$IMAGE_NAME:$IMAGE_TAG
                    '''
                }
            }
        }
    }

    post {
        success { echo "Pipeline terminée avec succès !" }
        failure { echo "Pipeline échouée. Vérifiez la connexion ou les logs." }
    }
}