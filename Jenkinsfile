pipeline {
    agent any

    // Charge les outils configurés dans Jenkins
    tools {
        maven 'maven_3'
    }

    environment {
        // Pour Docker Desktop sous Windows, l'IP de l'hôte est souvent 172.17.0.1
        // ou le nom du conteneur si sur le même réseau.
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
                sh 'mvn test'
            }
            post {
                always {
                    // Utilisation de double asterisque pour trouver les rapports n'importe où
                    junit '**/target/surefire-reports/*.xml'
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
                // Cette commande nécessite que docker.io soit installé dans le conteneur Jenkins
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
        success {
            echo "Pipeline terminée avec succès !"
        }
        failure {
            echo "Pipeline échouée. Vérifiez les logs (Maven ou Docker)."
        }
    }
}