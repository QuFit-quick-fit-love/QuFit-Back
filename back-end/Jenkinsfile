pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "whgustn730/qufit"
        DOCKER_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                deleteDir()
                checkout([$class: 'GitSCM',
                    branches: [[name: '*/refactor/BE-구조변경']],
                    userRemoteConfigs: [[url: 'https://lab.ssafy.com/s11-webmobile1-sub2/S11P12A209.git',
                                         credentialsId: 'jenkins-gitlab']]])
                sh 'ls -la'
            }
        }

        stage('Prepare Environment') {
            steps {
                withCredentials([file(credentialsId: 'qufit-env-file', variable: 'ENV_FILE')]) {
                    sh '''
                        echo "WORKSPACE value: $WORKSPACE"
                        mkdir -p "$WORKSPACE/back-end"
                        echo "Current working directory:"
                        pwd
                        echo "Creating ENV.env file..."
                        cp "$ENV_FILE" "$WORKSPACE/back-end/ENV.env"
                        echo "Adding DOCKER_TAG..."
                        echo "DOCKER_TAG=${DOCKER_TAG}" >> "$WORKSPACE/back-end/ENV.env"
                        echo "Adding DOCKER_IMAGE..."
                        echo "DOCKER_IMAGE=${DOCKER_IMAGE}" >> "$WORKSPACE/back-end/ENV.env"
                        echo "Contents of ENV.env file:"
                        cat "$WORKSPACE/back-end/ENV.env"
                        echo "Directory contents:"
                        ls -la "$WORKSPACE/back-end"
                    '''
                }
            }
        }

        stage('Debug Info') {
            steps {
                sh 'pwd'
                sh 'ls -la'
                sh 'docker info'
                sh 'id'
            }
        }

        stage('Build Docker Image') {
            steps {
                dir('back-end') {
                    script {
                        sh 'ls -la'
                        sh 'cat Dockerfile'
                        docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}", "-f Dockerfile .")
                    }
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                        docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").push()
                        docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").push("latest")
                    }
                }
            }
        }

        stage('Transfer Files') {
            steps {
                sshagent(['jenkins-ssh-key']) {
                    sh '''
                        scp -o StrictHostKeyChecking=no "$WORKSPACE/back-end/docker-compose.yml" ubuntu@i11a209.p.ssafy.io:/home/ubuntu/qufit/
                        scp -o StrictHostKeyChecking=no "$WORKSPACE/back-end/ENV.env" ubuntu@i11a209.p.ssafy.io:/home/ubuntu/qufit/.env
                    '''
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                sshagent(['jenkins-ssh-key']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ubuntu@i11a209.p.ssafy.io '
                        cd /home/ubuntu/qufit
                        echo "DOCKER_IMAGE=${DOCKER_IMAGE}" >> .env
                        echo "DOCKER_TAG=${DOCKER_TAG}" >> .env
                        docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}
                        docker-compose down
                        docker-compose up -d --build
                        '
                    """
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}