#!/usr/bin/env groovy

def call(Map param) {
    
    pipeline {
        agent {
            label "${param.node}"
        }
        stages {
            stage('Build') {
                steps {
                    sh 'mvn -DskipTests clean package'
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
            stage('Build image') {
                steps {
                    sh 'docker build -t ${param.name} .'
                }
            }
            stage('Run app') {
                steps {
                    sh 'docker run -p ${param.port} ${param.name}'
                }
            }
        }
        post {
            always {
                deleteDir()
            }
        }
    }
}