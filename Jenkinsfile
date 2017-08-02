def podLabel = "${env.JOB_BASE_NAME}-${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
podTemplate(
    label: podLabel, 
    containers: [
        containerTemplate(name: 'jnlp', image: env.JNLP_SLAVE_IMAGE, args: '${computer.jnlpmac} ${computer.name}', alwaysPullImage: true),
        containerTemplate(name: 'dind', image: 'docker:stable-dind', privileged: true, ttyEnabled: true, command: 'dockerd', args: '--host=unix:///var/run/docker.sock --host=tcp://0.0.0.0:2375 --storage-driver=vfs')
    ],
    volumes: [
        emptyDirVolume(mountPath: '/var/run', memory: false),
        hostPathVolume(mountPath: "/etc/docker/certs.d/${env.PRIVATE_REGISTRY}/ca.crt", hostPath: "/etc/docker/certs.d/${env.PRIVATE_REGISTRY}/ca.crt"),
        hostPathVolume(mountPath: '/home/jenkins/.kube/config', hostPath: '/etc/kubernetes/admin.conf'),
        persistentVolumeClaim(claimName: env.HELM_REPOSITORY, mountPath: '/var/helm/', readOnly: false)
    ]) {
     
    node(podLabel) {
        ansiColor('xterm'){
            def scalaVersion = "2.12.3"
            def image
            stage('git clone') {
                checkout scm
            }
            stage('build image') {
                
                sh """echo 'scalaVersion := "${scalaVersion}"' > global.sbt"""
                def imgName = "${env.PRIVATE_REGISTRY}/library/sbt:${scalaVersion}-${env.BRANCH_NAME}"
                image = docker.build(imgName, '--pull .')
            }
            stage('testing') {
                image.inside {
                    parallel cached: {
                        sh 'test $(du -s ~/.sbt/ | awk \'{print $1}\') -gt 10240'
                        sh 'du -hs ~/.sbt'
                        sh 'test $(du -s ~/.ivy2/ | awk \'{print $1}\') -gt 10240'
                        sh 'du -hs ~/.ivy2'
                    }, 'root-dir': {
                        sh 'cat /etc/passwd | grep \'root:/home/jenkins\''
                        echo 'root $HOME switch to /home/jenkins'
                    }, functionality: {
                        sh """
                        mkdir proj1 && cd \$_
                        sbt about | tee /tmp/sbt.log
                        cat /tmp/sbt.log | grep '${scalaVersion}'
                        """
                        
                    },
                    failFast: true
                }
            }
            stage('push image') {
                withDockerRegistry(url: env.PRIVATE_REGISTRY_UR, credentialsId: 'docker-login') {
                    image.push()
                }
            }
            // stage('package') {
            // 		docker.image('henryrao/helm:2.3.1').inside('') { c ->
            // 				sh '''
            // 				# packaging
            // 				helm package --destination /var/helm/repo scala
            // 				helm repo index --url https://grandsys.github.io/helm-repository/ --merge /var/helm/repo/index.yaml /var/helm/repo
            // 				'''
            // 		}
            // 		build job: 'helm-repository/master', parameters: [string(name: 'commiter', value: "${env.JOB_NAME}\ncommit: ${sh(script: 'git log --format=%B -n 1', returnStdout: true).trim()}")]
            // }
        }
    }
}
