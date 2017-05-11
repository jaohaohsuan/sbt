podTemplate(
    label: 'sbt-uni', 
    containers: [
        containerTemplate(name: 'jnlp', image: 'henryrao/jnlp-slave', args: '${computer.jnlpmac} ${computer.name}', alwaysPullImage: true)
    ],
    volumes: [
        hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
        hostPathVolume(mountPath: '/root/.kube/config', hostPath: '/root/.kube/config'),
        persistentVolumeClaim(claimName: 'helm-repository', mountPath: '/var/helm/repo', readOnly: false)
    ]) {
           
    node('sbt-uni') {
        def image
        stage('git clone') {
            git(url: 'https://github.com/jaohaohsuan/sbt.git', branch: 'universal')
        }
        stage('build image') {
            def scalaVersion = "2.11.8"
            sh """echo 'scalaVersion := "${scalaVersion}"' > global.sbt"""
            image = docker.build("henryrao/sbt:${scalaVersion}", '--pull .')
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
                    sh '''
                    mkdir proj1 && cd $_
                    sbt about | tee /tmp/sbt.log
                    cat /tmp/sbt.log | grep \'2.11.8\'
                    '''
                    
                },
                failFast: true
            }
        }
        stage('push image') {
            withDockerRegistry(url: 'https://index.docker.io/v1/', credentialsId: 'docker-login') {
                parallel versioned: {
                    image.push()
                }, latest: {
                    image.push('latest')
                },
                failFast: false
            }
        }
				stage('package') {
						docker.image('henryrao/helm:2.3.1').inside('') { c ->
								sh '''
								# packaging
								helm package --destination /var/helm/repo scala
								helm repo index --url https://grandsys.github.io/helm-repository/ --merge /var/helm/repo/index.yaml /var/helm/repo
								'''
						}
						build job: 'helm-repository/master', parameters: [string(name: 'commiter', value: "${env.JOB_NAME}\ncommit: ${sh(script: 'git log --format=%B -n 1', returnStdout: true).trim()}")]
				}
    } 
}
